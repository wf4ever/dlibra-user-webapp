package pl.psnc.dl.wf4ever.webapp.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;
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

	private static final long serialVersionUID = 4677896071331937974L;

	protected Panel sidebarPanel;

	protected WebMarkupContainer content;

	protected boolean willBeRedirected = false;


	public TemplatePage(PageParameters pageParameters)
	{
		DlibraUser userModel = getDlibraUserModel();
		content = new WebMarkupContainer("content");
		if (userModel == null && !(this instanceof AuthenticationPage)) {
			content.setVisible(false);
			willBeRedirected = true;
			goToPage(AuthenticationPage.class, pageParameters);
		}
		if (userModel == null) {
			sidebarPanel = new LoggedOutPanel("sidebar");
		}
		else {
			try {
				sidebarPanel = new LoggedInPanel("sidebar", userModel);
			}
			catch (Exception e) {
				error(e.getMessage());
				sidebarPanel = new LoggedOutPanel("sidebar");
			}
		}

		add(new BookmarkablePageLink<Void>("home", getApplication()
				.getHomePage()));
		add(new BookmarkablePageLink<Void>("about", AboutPage.class));

		add(content);
		add(sidebarPanel);
		content.add(new FeedbackPanel("feedback"));
	}


	public DlibraUser getDlibraUserModel()
	{
		return (DlibraUser) getSession().getAttribute(
			Constants.SESSION_USER_MODEL);
	}


	public boolean logIn(DlibraUser model)
	{
		try {
			getSession().setAttribute(Constants.SESSION_USER_MODEL, model);
			sidebarPanel.replaceWith(new LoggedInPanel("sidebar", model));
			return true;
		}
		catch (Exception e) {
			error(e.getMessage());
			return false;
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
			MyExpImportPage.class, false);

		OAuthService service = MyExpApi.getOAuthService(oauthCallbackURL);
		Token requestToken = service.getRequestToken();
		getSession()
				.setAttribute(Constants.SESSION_REQUEST_TOKEN, requestToken);
		String authorizationUrl = service.getAuthorizationUrl(requestToken);
		getRequestCycle().scheduleRequestHandlerAfterCurrent(
			new RedirectRequestHandler(authorizationUrl));
	}

}
