package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openid4java.discovery.DiscoveryInformation;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;
import pl.psnc.dl.wf4ever.webapp.model.OpenIdUserModel;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;
import pl.psnc.dl.wf4ever.webapp.services.OpenIdService;

public class DlibraRegistrationPage
	extends WebPage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


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
	public DlibraRegistrationPage(PageParameters pageParameters)
	{
		OpenIdUserModel registrationModel = new OpenIdUserModel();
		if (!pageParameters.isEmpty()) {
			String isReturn = pageParameters.get("is_return").toString();
			if ("true".equals(isReturn)) {
				Session session = getSession();
				DiscoveryInformation discoveryInformation = (DiscoveryInformation) session
						.getAttribute(OpenIdService.DISCOVERY_INFORMATION);

				registrationModel = OpenIdService.processReturn(
					discoveryInformation, pageParameters,
					WicketUtils.getOpenIdCallbackUrl(this));
				if (registrationModel == null) {
					error("Open ID Confirmation Failed. No information was retrieved from the OpenID Provider. You will have to enter all information by hand into the text fields provided.");
					return;
				}
			}
		}
		add(new OpenIdRegistrationInformationDisplayForm("form",
				registrationModel));

		DlibraUserModel userModel = DlibraService.createDlibraUserModel(
			registrationModel.getOpenId(),
			(String) getSession().getAttribute(OpenIdService.MY_EXP_ID));

		add(new UserInfoDisplayForm("dLibraForm", userModel));
		add(new MyExpImportForm("myExpImportForm", userModel));
		add(new FeedbackPanel("feedback"));
	}

	private class OpenIdRegistrationInformationDisplayForm
		extends Form<OpenIdUserModel>
	{

		private static final long serialVersionUID = -1045594133856989168L;


		/**
		 * Constructor, takes the wicket:id value (probably "form") and the
		 * RegistrationModel object to be used as the model for the form.
		 * 
		 * @param id
		 * @param registrationModel
		 */
		@SuppressWarnings("serial")
		public OpenIdRegistrationInformationDisplayForm(String id,
				OpenIdUserModel registrationModel)
		{
			super(id, new CompoundPropertyModel<OpenIdUserModel>(
					registrationModel));

			TextField<String> openId = new TextField<String>("openId");
			openId.setEnabled(false);
			add(openId);

			TextField<String> fullName = new RequiredTextField<String>(
					"fullName");
			add(fullName);

			TextField<String> emailAddress = new RequiredTextField<String>(
					"emailAddress");
			add(emailAddress);

			TextField<String> country = new TextField<String>("country");
			add(country);

			TextField<String> language = new TextField<String>("language");
			add(language);

			Button saveButton = new Button("saveButton") {

				public void onSubmit()
				{
					// TODO, maybe
				}
			};
			saveButton.setEnabled(false);
			add(saveButton);
		}

	}

	private class UserInfoDisplayForm
		extends Form<DlibraUserModel>
	{

		private static final long serialVersionUID = 8454343676077898053L;


		@SuppressWarnings("serial")
		public UserInfoDisplayForm(String id, final DlibraUserModel model)
		{
			super(id, new CompoundPropertyModel<DlibraUserModel>(model));

			final Label message = new Label("message");
			message.setOutputMarkupId(true);
			add(message);

			Button actionButton = new AjaxButton("registerButton",
					new PropertyModel<String>(model, "buttonText")) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					if (model.isRegistered()) {
						DlibraService.deleteWorkspace(model);
					}
					else {
						DlibraService.createWorkspace(model);
					}
					target.add(message);
					target.add(this);
				}


				@Override
				protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
				{
					// TODO Auto-generated method stub

				}
			};
			actionButton.setOutputMarkupId(true);
			add(actionButton);
		}
	}

	private class MyExpImportForm
		extends Form<DlibraUserModel>
	{

		private static final long serialVersionUID = 8454343676077898053L;


		public MyExpImportForm(String id, DlibraUserModel model)
		{
			super(id, new CompoundPropertyModel<DlibraUserModel>(model));

			TextField<String> myExpId = new RequiredTextField<String>("myExpId");
			add(myExpId);

			@SuppressWarnings("serial")
			final Button importButton = new Button("myExpImportButton") {

				@Override
				public void onSubmit()
				{
					// TODO Auto-generated method stub
					super.onSubmit();
				}
			};
			importButton.setOutputMarkupId(true);
			add(importButton);

		}
	}

}
