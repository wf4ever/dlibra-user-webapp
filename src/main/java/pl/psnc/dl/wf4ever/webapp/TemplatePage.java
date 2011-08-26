package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;
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


	@SuppressWarnings("serial")
	public TemplatePage(PageParameters pageParameters)
	{
		DlibraUserModel userModel = getDlibraUserModel();
		if (!userModel.isAuthenticated()
				&& !(this instanceof AuthenticationPage)) {
			goToAuthenticationPage(pageParameters);
		}
		if (!userModel.isAuthenticated()) {
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
		add(sidebarPanel);
		add(new FeedbackPanel("feedback"));
	}


	protected DlibraUserModel getDlibraUserModel()
	{
		if (getSession().getAttribute(Constants.SESSION_USER_MODEL) == null) {
			DlibraUserModel model = new DlibraUserModel();
			getSession().setAttribute(Constants.SESSION_USER_MODEL, model);
		}
		return (DlibraUserModel) getSession().getAttribute(
			Constants.SESSION_USER_MODEL);
	}


	public boolean logIn(DlibraUserModel model)
	{
		try {
			getSession().setAttribute(Constants.SESSION_USER_MODEL, model);
			sidebarPanel.replaceWith(new LoggedInPanel("sidebar", model));
			model.setAuthenticated(true);
			return true;
		}
		catch (Exception e) {
			error(e.getMessage());
			return false;
		}
	}


	public void logOut()
	{
		DlibraUserModel model = new DlibraUserModel();
		getSession().setAttribute(Constants.SESSION_USER_MODEL, model);
		model.setAuthenticated(false);
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
