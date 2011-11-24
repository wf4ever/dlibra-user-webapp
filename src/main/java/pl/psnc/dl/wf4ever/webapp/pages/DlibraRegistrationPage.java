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

import pl.psnc.dl.wf4ever.webapp.model.OpenIdUser;
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
	private static final Logger log = Logger.getLogger(DlibraRegistrationPage.class);

	private boolean userRegistered;


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

		final OpenIdUser user = getOpenIdUserModel();

		userRegistered = DlibraService.userExistsInDlibra(user.getOpenId());

		Form<OpenIdUser> form = new Form<OpenIdUser>("form", new CompoundPropertyModel<OpenIdUser>(user));
		form.setOutputMarkupId(true);
		content.add(form);

		final WebMarkupContainer message = createMessageFragment(content);
		message.setOutputMarkupId(true);
		form.add(message);

		final WebMarkupContainer credentials = createCredentialsDiv(user);
		form.add(credentials);

		form.add(new AjaxButton("registerButtonText", new PropertyModel<String>(this, "registerButtonText")) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				OpenIdUser user = (OpenIdUser) getForm().getModelObject();
				try {
					String infoMessage;
					if (userRegistered) {
						DlibraService.deleteUser(user);
						infoMessage = "Account has been deleted.";
					}
					else {
						if (!DlibraService.createUser(user.getOpenId(), user.getFullName())) {
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
					error(e.getMessage() != null ? e.getMessage() : "Unknown error");
				}
				userRegistered = DlibraService.userExistsInDlibra(user.getOpenId());
				target.add(message);
				target.add(this);
				WebMarkupContainer div = createCredentialsDiv(user);
				getParent().replace(div);
				Fragment f = createMessageFragment(content);
				getParent().replace(f);
				target.add(getParent());

			}


			@Override
			protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
			{
			}
		}).setOutputMarkupId(true);
	}


	private WebMarkupContainer createCredentialsDiv(OpenIdUser model)
	{
		WebMarkupContainer div = new WebMarkupContainer("credentials");
		if (userRegistered) {
			div.add(new Label("openId", model.getOpenId()));
		}
		else {
			div.setVisible(false);
		}
		return div;
	}


	private Fragment createMessageFragment(MarkupContainer container)
	{
		Fragment f;
		if (userRegistered) {
			f = new Fragment("message", "registered", container);
		}
		else {
			f = new Fragment("message", "notRegistered", container);
		}
		return f;
	}


	public String getRegisterButtonText()
	{
		if (userRegistered) {
			return "Delete account in dLibra";
		}
		else {
			return "Create account in dLibra";
		}
	}

}
