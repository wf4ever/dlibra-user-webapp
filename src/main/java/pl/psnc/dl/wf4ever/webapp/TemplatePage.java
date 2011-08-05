package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;

public abstract class TemplatePage
	extends WebPage
{

	private static final long serialVersionUID = 4677896071331937974L;

	public static final String USER_MODEL = "userModel";

	protected SidebarPanel sidebarPanel;


	public TemplatePage(PageParameters pageParameters)
	{
		DlibraUserModel userModel = getDlibraUserModel();
		if (userModel.getOpenId() == null
				&& !(this instanceof AuthenticationPage)) {
			String home = urlFor(AuthenticationPage.class, pageParameters)
					.toString();
			getRequestCycle().scheduleRequestHandlerAfterCurrent(
				new RedirectRequestHandler(home));

		}
		sidebarPanel = new SidebarPanel("sidebar", userModel);
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

}
