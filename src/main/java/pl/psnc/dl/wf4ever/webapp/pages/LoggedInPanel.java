/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pl.psnc.dl.wf4ever.webapp.model.OpenIdUser;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class LoggedInPanel extends Panel {

	Form<OpenIdUser> userDetails;

	public LoggedInPanel(String id, OpenIdUser user) throws Exception {
		super(id, new CompoundPropertyModel<OpenIdUser>(user));
		setOutputMarkupId(true);

		if (user == null || user.getOpenId() == null) {
			throw new Exception("User is not logged in");
		}

		userDetails = new Form<OpenIdUser>("userDetails",
				new CompoundPropertyModel<OpenIdUser>(user));
		userDetails.add(new Label("fullName"));
		userDetails.add(new WebMarkupContainer("liEmailAddress").add(
				new Label("emailAddress")).setVisible(
				user.getEmailAddress() != null));
		userDetails.add(new WebMarkupContainer("liCountry").add(
				new Label("country"))
				.setVisible(user.getCountry() != null));
		userDetails.add(new WebMarkupContainer("liLanguage").add(
				new Label("language")).setVisible(
				user.getLanguage() != null));
		userDetails.setOutputMarkupId(true);
		add(userDetails);

		@SuppressWarnings("serial")
		Link<String> logout = new Link<String>("logout") {

			@Override
			public void onClick() {
				TemplatePage page = (TemplatePage) getPage();
				page.logOut();
			}

		};
		add(logout);
	}

	private static final long serialVersionUID = -3775797988389365540L;

}
