package pl.psnc.dl.wf4ever.webapp.pages;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;

/**
 * 
 * @author Piotr Hołubowicz
 *
 */
public class DlibraRegistrationPage
	extends TemplatePage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(DlibraRegistrationPage.class);


	/**
	 * Default Constructor
	 */
	public DlibraRegistrationPage()
	{
		this(new PageParameters());
	}


	/**
	 * Constructor called by Wicket with an auth response (since the response
	 * has parameters associated with it... LOTS of them!). And, by the way,
	 * the auth response is the Request for this class (not to be confusing).
	 * 
	 * @param pageParameters The request parameters (which are the response
	 *  parameters from the OP).
	 */
	@SuppressWarnings("serial")
	public DlibraRegistrationPage(PageParameters pageParameters)
	{
		super(pageParameters);
		if (willBeRedirected)
			return;

		final DlibraUser user = getDlibraUserModel();

		Form<DlibraUser> form = new Form<DlibraUser>("form",
				new CompoundPropertyModel<DlibraUser>(user));
		form.setOutputMarkupId(true);
		content.add(form);

		final WebMarkupContainer message = createMessageFragment(user, content);
		message.setOutputMarkupId(true);
		form.add(message);

		final WebMarkupContainer credentials = createCredentialsDiv(user);
		form.add(credentials);

		form.add(
			new AjaxButton("registerButtonText", new PropertyModel<String>(
					user, "registerButtonText")) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					DlibraUser user = (DlibraUser) getForm().getModelObject();
					try {
						String infoMessage;
						if (user.isRegistered()) {
							DlibraService.deleteUser(user);
							infoMessage = "Account has been deleted.";
						}
						else {
							if (!DlibraService.createUser(user)) {
								infoMessage = "An account for this username already existed "
										+ "in dLibra, you have been registered with it.";
							}
							else {
								infoMessage = "New account has been created.";
							}
						}
						getSession().info(infoMessage);
						setResponsePage(getPage());
					}
					catch (Exception e) {
						error(e.getMessage() != null ? e.getMessage()
								: "Unknown error");
					}
					target.add(message);
					target.add(this);
					WebMarkupContainer div = createCredentialsDiv(user);
					getParent().replace(div);
					Fragment f = createMessageFragment(user, content);
					getParent().replace(f);
					target.add(getParent());

				}


				@Override
				protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
				{
				}
			}).setOutputMarkupId(true);
	}


	private WebMarkupContainer createCredentialsDiv(DlibraUser model)
	{
		WebMarkupContainer div = new WebMarkupContainer("credentials");
		if (model.isRegistered()) {
			div.add(new Label("username", new PropertyModel<String>(model,
					"username")));
		}
		else {
			div.setVisible(false);
		}
		return div;
	}


	private Fragment createMessageFragment(DlibraUser model,
			MarkupContainer container)
	{
		Fragment f;
		if (model.isRegistered()) {
			f = new Fragment("message", "registered", container);
		}
		else {
			f = new Fragment("message", "notRegistered", container);
		}
		return f;
	}

}
