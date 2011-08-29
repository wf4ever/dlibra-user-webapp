package pl.psnc.dl.wf4ever.webapp.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.utils.Constants;

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


	@SuppressWarnings("serial")
	public TemplatePage(PageParameters pageParameters)
	{
		DlibraUser userModel = getDlibraUserModel();
		content = new WebMarkupContainer("content");
		if (userModel == null && !(this instanceof AuthenticationPage)) {
			content.setVisible(false);
			willBeRedirected = true;
			goToAuthenticationPage(pageParameters);
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

		add(new Link<Void>("home") {

			@Override
			public void onClick()
			{
				setResponsePage(getApplication().getHomePage());
			}

		});
		add(new Link<Void>("about") {

			@Override
			public void onClick()
			{
				setResponsePage(new AboutPage());
			}

		});
		add(content);
		add(sidebarPanel);
		content.add(new FeedbackPanel("feedback"));
	}


	protected DlibraUser getDlibraUserModel()
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


	protected void goToAuthenticationPage(PageParameters pageParameters)
	{
		String home = urlFor(AuthenticationPage.class, pageParameters)
				.toString();
		getRequestCycle().scheduleRequestHandlerAfterCurrent(
			new RedirectRequestHandler(home));
	}


	private void reloadPage()
	{
		String url = urlFor(this.getClass(), null).toString();
		getRequestCycle().scheduleRequestHandlerAfterCurrent(
			new RedirectRequestHandler(url));
	}

}
