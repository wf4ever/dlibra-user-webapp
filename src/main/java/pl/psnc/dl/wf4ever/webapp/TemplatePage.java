package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;

public abstract class TemplatePage
	extends WebPage
{

	private static final long serialVersionUID = 4677896071331937974L;

	public static final String USER_MODEL = "userModel";

	protected Panel sidebarPanel;


	public TemplatePage(PageParameters pageParameters)
	{
		DlibraUserModel userModel = getDlibraUserModel();
		if (userModel.getOpenId() == null
				&& !(this instanceof AuthenticationPage)) {
			goToAuthenticationPage(pageParameters);
		}
		if (userModel.getOpenId() == null) {
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
		add(sidebarPanel);
	}


	protected DlibraUserModel getDlibraUserModel()
	{
		if (getSession().getAttribute(USER_MODEL) == null) {
			DlibraUserModel model = new DlibraUserModel();
			getSession().setAttribute(USER_MODEL, model);
		}
		return (DlibraUserModel) getSession().getAttribute(USER_MODEL);
	}


	public boolean logIn(DlibraUserModel model)
	{
		try {
			getSession().setAttribute(USER_MODEL, model);
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
		DlibraUserModel model = new DlibraUserModel();
		getSession().setAttribute(USER_MODEL, model);
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
