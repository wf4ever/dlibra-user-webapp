package pl.psnc.dl.wf4ever.webapp.pages;

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

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.OpenIdData;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;
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

		String isReturn = pageParameters.get("is_return").toString();
		if ("true".equals(isReturn)) {
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
					error("Open ID Confirmation Failed. No information was retrieved from the OpenID Provider. You will have to enter all information by hand into the text fields provided.");
				}
				DlibraUser user = DlibraService.loadOrCreateUser(openIdData
						.getOpenId());
				user.setOpenIdData(openIdData);
				confirmAuthentication(user);
				logIn(user);
			}
		}

		final DlibraUser tempUser = new DlibraUser();

		Form<DlibraUser> form = new Form<DlibraUser>("form",
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
		Button google = new Button("logInWithGoogle") {

			@Override
			public void onSubmit()
			{
				super.onSubmit();
				tempUser.setOpenId(GOOGLE_URL);
				applyForAuthentication(GOOGLE_URL);
			}
		};
		google.setDefaultFormProcessing(false);
		form.add(google);
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


	public void confirmAuthentication(DlibraUser model)
	{
		String url = urlFor(DlibraRegistrationPage.class, null).toString();
		getRequestCycle().scheduleRequestHandlerAfterCurrent(
			new RedirectRequestHandler(url));
	}

}
