/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;
import pl.psnc.dl.wf4ever.webapp.model.OpenIdDataModel;

/**
 * @author piotrhol
 *
 */
public class SidebarPanel
	extends Panel
{
	
	Form<OpenIdDataModel> userDetails;

	public SidebarPanel(String id, DlibraUserModel model)
	{
		super(id, new CompoundPropertyModel<DlibraUserModel>(model));
		setOutputMarkupId(true);

		Label userMessage;
		if (model == null || model.getOpenId() == null) {
			userMessage = new Label("userData", new Model<String>(
					"You are not logged in"));
			userDetails = new Form<OpenIdDataModel>("userDetails",
					new CompoundPropertyModel<OpenIdDataModel>(model.getOpenIdData()));
			userDetails.setVisible(false);
		}
		else {
			String login;
			if (model.getOpenIdData() != null
					&& model.getOpenIdData().getFullName() != null) {
				login = model.getOpenIdData().getFullName();
			}
			else {
				login = model.getOpenId();
			}
			String message = String.format(
				"You are logged in as <strong>%s</strong>.", login);
			userMessage = new Label("userData", new Model<String>(message));
			userMessage.setEscapeModelStrings(false);
			userDetails = new Form<OpenIdDataModel>("userDetails",
					new CompoundPropertyModel<OpenIdDataModel>(model.getOpenIdData()));
			userDetails.add(new Label("openId"));
			userDetails.add(new Label("emailAddress"));
			userDetails.add(new Label("country"));
			userDetails.add(new Label("language"));
			userDetails.setOutputMarkupId(true);
		}
		add(userMessage);
		add(userDetails);
	}

	private static final long serialVersionUID = -3775797988389365540L;

}
