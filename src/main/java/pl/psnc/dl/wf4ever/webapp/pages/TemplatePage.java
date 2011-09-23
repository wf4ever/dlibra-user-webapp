package pl.psnc.dl.wf4ever.webapp.pages;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.MyExpUser;
import pl.psnc.dl.wf4ever.webapp.model.OpenIdUser;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;
import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;
import pl.psnc.dl.wf4ever.webapp.services.OAuthException;
import pl.psnc.dl.wf4ever.webapp.services.OAuthHelpService;
import pl.psnc.dl.wf4ever.webapp.utils.Constants;
import pl.psnc.dl.wf4ever.webapp.utils.WicketUtils;

/**
 * 
 * @author Piotr Hołubowicz
 *
 */
public abstract class TemplatePage
	extends WebPage
{

	private static final Logger log = Logger.getLogger(TemplatePage.class);

	private static final long serialVersionUID = 4677896071331937974L;

	protected Panel sidebarPanel;

	protected WebMarkupContainer content;

	protected boolean willBeRedirected = false;

	private static final Class< ? >[] publicPages = { AuthenticationPage.class,
			AboutPage.class, HelpPage.class};


	public TemplatePage(PageParameters pageParameters)
	{
		getSession().bind();
		OpenIdUser user = getOpenIdUserModel();
		content = new WebMarkupContainer("content");
		if (user == null
				&& !ArrayUtils.contains(publicPages, this.getClass())) {
			content.setVisible(false);
			willBeRedirected = true;
			String url = RequestCycle
					.get()
					.getUrlRenderer()
					.renderFullUrl(
						Url.parse(urlFor(this.getClass(), pageParameters)
								.toString()));
			getSession().setAttribute(Constants.SESSION_REDIRECT_URI, url);
			goToPage(AuthenticationPage.class, pageParameters);
		}
		if (user == null) {
			sidebarPanel = new LoggedOutPanel("sidebar");
		}
		else {
			try {
				sidebarPanel = new LoggedInPanel("sidebar", user);
			}
			catch (Exception e) {
				error(e.getMessage());
				sidebarPanel = new LoggedOutPanel("sidebar");
			}
		}

		add(new BookmarkablePageLink<Void>("home", getApplication()
				.getHomePage()));
		add(new BookmarkablePageLink<Void>("about", AboutPage.class));
		add(new BookmarkablePageLink<Void>("help", HelpPage.class));

		add(content);
		add(sidebarPanel);
		content.add(new FeedbackPanel("feedback"));
	}


	public OpenIdUser getOpenIdUserModel()
	{
		return (OpenIdUser) getSession().getAttribute(
			Constants.SESSION_USER_MODEL);
	}


	public boolean logIn(OpenIdUser user)
	{
		try {
			getSession().setAttribute(Constants.SESSION_USER_MODEL, user);
			sidebarPanel.replaceWith(new LoggedInPanel("sidebar", user));
			register(user);
			return true;
		}
		catch (Exception e) {
			error(e.getMessage());
			return false;
		}
	}


	private void register(OpenIdUser user)
	{
		if (!DlibraService.userExistsInDlibra(user.getOpenId())) {
			try {
				String message;
				if (!DlibraService.createUser(user)) {
					message = "An account for this username already existed "
							+ "in dLibra, you have been registered with it.";
				}
				else {
					message = "New account has been created.";
				}
				getSession().info(message);
			}
			catch (Exception e) {
				getSession().error(
					e.getMessage() != null ? e.getMessage() : "Unknown error");
			}
		}

	}


	public void logOut()
	{
		getSession().setAttribute(Constants.SESSION_USER_MODEL, null);
		reloadPage();
	}


	protected void goToPage(Class< ? extends TemplatePage> pageClass,
			PageParameters pageParameters)
	{
		String url = urlFor(pageClass, pageParameters).toString();
		log.debug("Will redirect to: " + url);
		getRequestCycle().scheduleRequestHandlerAfterCurrent(
			new RedirectRequestHandler(url));
	}


	private void reloadPage()
	{
		String url = urlFor(this.getClass(), null).toString();
		getRequestCycle().scheduleRequestHandlerAfterCurrent(
			new RedirectRequestHandler(url));
	}


	protected void startMyExpAuthorization()
	{
		String oauthCallbackURL = WicketUtils.getCompleteUrl(this,
			AuthenticationPage.class, false);

		OAuthService service = MyExpApi.getOAuthService(oauthCallbackURL);
		Token requestToken = service.getRequestToken();
		getSession()
				.setAttribute(Constants.SESSION_REQUEST_TOKEN, requestToken);
		String authorizationUrl = service.getAuthorizationUrl(requestToken);
		log.debug("Request token: " + requestToken.toString() + " service: "
				+ service.getAuthorizationUrl(requestToken));
		getRequestCycle().scheduleRequestHandlerAfterCurrent(
			new RedirectRequestHandler(authorizationUrl));
	}


	/**
	 * @param pageParameters
	 * @param service
	 * @return
	 */
	protected Token retrieveAccessToken(PageParameters pageParameters,
			OAuthService service)
	{
		Token accessToken = null;
		if (!pageParameters.get(MyExpApi.OAUTH_VERIFIER).isEmpty()) {
			Verifier verifier = new Verifier(pageParameters.get(
				MyExpApi.OAUTH_VERIFIER).toString());
			Token requestToken = (Token) getSession().getAttribute(
				Constants.SESSION_REQUEST_TOKEN);
			log.debug("Request token: " + requestToken.toString()
					+ " verifier: " + verifier.getValue() + " service: "
					+ service.getAuthorizationUrl(requestToken));
			accessToken = service.getAccessToken(requestToken, verifier);
		}
		return accessToken;
	}


	/**
	 * @param user
	 * @param service
	 * @return
	 * @throws OAuthException
	 * @throws JAXBException
	 */
	protected MyExpUser retrieveMyExpUser(Token accessToken,
			OAuthService service)
		throws OAuthException, JAXBException
	{
		MyExpUser myExpUser;
		Response response = OAuthHelpService.sendRequest(service, Verb.GET,
			MyExpApi.WHOAMI_URL, accessToken);
		myExpUser = createMyExpUserModel(response.getBody());

		response = OAuthHelpService.sendRequest(service, Verb.GET,
			String.format(MyExpApi.GET_USER_URL, myExpUser.getId()),
			accessToken);
		myExpUser = createMyExpUserModel(response.getBody());
		return myExpUser;
	}


	private MyExpUser createMyExpUserModel(String xml)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(MyExpUser.class);

		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return (MyExpUser) u.unmarshal(new StreamSource(new StringReader(xmlStr
				.toString())));
	}

}
