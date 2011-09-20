package pl.psnc.dl.wf4ever.webapp.pages;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.OpenIdData;
import pl.psnc.dl.wf4ever.webapp.model.myexp.User;
import pl.psnc.dl.wf4ever.webapp.services.HibernateService;
import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;
import pl.psnc.dl.wf4ever.webapp.services.OpenIdService;
import pl.psnc.dl.wf4ever.webapp.utils.Constants;
import pl.psnc.dl.wf4ever.webapp.utils.WicketUtils;

/**
 * 
 * @author Piotr Hołubowicz
 *
 */
public class AuthenticationPage
	extends TemplatePage
{

	private static final long serialVersionUID = -8975579933617712699L;

	private static final Logger log = Logger.getLogger(AuthenticationPage.class);

	private static final String GOOGLE_URL = "https://www.google.com/accounts/o8/id";

	private String returnToUrl;


	public AuthenticationPage()
	{
		this(new PageParameters());
	}


	@SuppressWarnings("serial")
	public AuthenticationPage(PageParameters pageParameters)
	{
		super(pageParameters);

		// FIXME replaceAll because "../" gets inserted, don't know why
		returnToUrl = WicketUtils.getCompleteUrl(this,
			AuthenticationPage.class, true).replaceAll("\\.\\./", "");

		if (!pageParameters.get(MyExpApi.OAUTH_VERIFIER).isEmpty()) {
			OAuthService service = MyExpApi.getOAuthService(WicketUtils
					.getCompleteUrl(this, MyExpImportPage.class, true));

			Token accessToken = retrieveAccessToken(pageParameters, service);
			getSession().setAttribute(Constants.SESSION_TEMPORARY_ACCESS_TOKEN,
				accessToken);

			try {
				User user = retrieveMyExpUser(accessToken, service);

				if (user.getOpenId() == null) {
					throw new Exception(
							"Your myExperiment profile does not contain any openID.");
				}
				applyForAuthentication(user.getOpenId());
				getSession()
						.info(
							"You have been logged in using OpenID: "
									+ user.getOpenId());
			}
			catch (Exception e) {
				error(e.getMessage());
			}
		}

		if ("true".equals(pageParameters.get("is_return").toString())) {
			processOpenIdResponse(pageParameters);
		}

		final DlibraUser tempUser = new DlibraUser();

		Form<DlibraUser> form = new Form<DlibraUser>("openIdForm",
				new CompoundPropertyModel<DlibraUser>(tempUser)) {

			@Override
			protected void onSubmit()
			{
				super.onSubmit();
				applyForAuthentication(tempUser.getOpenId());
			}
		};
		content.add(form);
		TextField<String> openId = new RequiredTextField<String>("openId");
		openId.setLabel(new Model<String>("Your Open ID"));
		form.add(openId);
		form.add(new Button("confirmOpenIdButton"));

		Form< ? > form2 = new Form<Void>("form");
		content.add(form2);
		form2.add(new Button("logInWithGoogle") {

			@Override
			public void onSubmit()
			{
				super.onSubmit();
				applyForAuthentication(GOOGLE_URL);
			}
		});

		form2.add(new Button("logInWithMyExp") {

			@Override
			public void onSubmit()
			{
				super.onSubmit();
				startMyExpAuthorization();
			}
		});
	}


	/**
	 * @param pageParameters
	 */
	private void processOpenIdResponse(PageParameters pageParameters)
	{
		String openIdMode = pageParameters.get("openid.mode").toString();
		if ("cancel".equals(openIdMode)) {
			info("The authentication request has been rejected");
		}
		else {
			Session session = getSession();
			DiscoveryInformation discoveryInformation = (DiscoveryInformation) session
					.getAttribute(Constants.SESSION_DISCOVERY_INFORMATION);

			OpenIdData openIdData = OpenIdService.processReturn(
				discoveryInformation, pageParameters, returnToUrl);
			if (openIdData == null) {
				error("Open ID Confirmation Failed. No information was retrieved from the OpenID Provider.");
				return;
			}
			DlibraUser user = HibernateService.loadUser(openIdData.getOpenId());
			if (user == null) {
				user = new DlibraUser();
				user.setOpenId(openIdData.getOpenId());
			}
			user.setOpenIdData(openIdData);
			logIn(user);
			if (getSession().getAttribute(Constants.SESSION_REDIRECT_URI) == null) {
				goToPage(DlibraRegistrationPage.class, null);
			}
			else {
				getRequestCycle().scheduleRequestHandlerAfterCurrent(
					new RedirectRequestHandler((String) getSession()
							.getAttribute(Constants.SESSION_REDIRECT_URI)));
				log.debug("Redirecting to: " + (String) getSession()
							.getAttribute(Constants.SESSION_REDIRECT_URI));
				getSession().setAttribute(Constants.SESSION_REDIRECT_URI, null);
			}
		}
	}


	public void applyForAuthentication(String userSuppliedIdentifier)
	{
		DiscoveryInformation discoveryInformation = OpenIdService
				.performDiscoveryOnUserSuppliedIdentifier(userSuppliedIdentifier);
		// Store the discovery results in session.
		Session session = getSession();
		session.setAttribute(Constants.SESSION_DISCOVERY_INFORMATION,
			discoveryInformation);
		// Create the AuthRequest
		AuthRequest authRequest = OpenIdService.createOpenIdAuthRequest(
			discoveryInformation, returnToUrl);
		// Now take the AuthRequest and forward it on to the OP
		IRequestHandler reqHandler = new RedirectRequestHandler(
				authRequest.getDestinationUrl(true));
		getRequestCycle().scheduleRequestHandlerAfterCurrent(reqHandler);
	}

}
