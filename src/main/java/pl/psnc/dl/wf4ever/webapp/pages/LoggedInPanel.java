/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.OpenIdData;

/**
 * @author Piotr Hołubowicz
 *
 */
public class LoggedInPanel
	extends Panel
{

	Form<OpenIdData> userDetails;


	public LoggedInPanel(String id, DlibraUser model)
		throws Exception
	{
		super(id, new CompoundPropertyModel<DlibraUser>(model));
		setOutputMarkupId(true);

		if (model == null || model.getOpenId() == null) {
			throw new Exception("User is not logged in");
		}
		
		userDetails = new Form<OpenIdData>("userDetails",
				new CompoundPropertyModel<OpenIdData>(
						model.getOpenIdData()));
		userDetails.add(new Label("fullName"));
		//			userDetails.add(new Label("openId"));
		userDetails.add(new Label("emailAddress"));
		userDetails.add(new Label("country"));
		userDetails.add(new Label("language"));
		userDetails.setOutputMarkupId(true);
		add(userDetails);
		
		@SuppressWarnings("serial")
		Link<String> logout = new Link<String>("logout") {

			@Override
			public void onClick()
			{
				TemplatePage page = (TemplatePage) getPage();
				page.logOut();
			}
			
		};
		add(logout);
	}

	private static final long serialVersionUID = -3775797988389365540L;

}
