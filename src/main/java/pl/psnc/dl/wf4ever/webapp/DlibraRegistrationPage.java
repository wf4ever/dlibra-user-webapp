package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openid4java.discovery.DiscoveryInformation;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;
import pl.psnc.dl.wf4ever.webapp.services.Constants;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;
import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;
import pl.psnc.dl.wf4ever.webapp.services.OpenIdService;
import pl.psnc.dl.wf4ever.webapp.utils.WicketUtils;

public class DlibraRegistrationPage
	extends TemplatePage
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
		super(pageParameters);
		DlibraUserModel model = getDlibraUserModel();
		if (!pageParameters.isEmpty()) {
			String isReturn = pageParameters.get("is_return").toString();
			if ("true".equals(isReturn)) {
				Session session = getSession();
				DiscoveryInformation discoveryInformation = (DiscoveryInformation) session
						.getAttribute(Constants.SESSION_DISCOVERY_INFORMATION);

				OpenIdService.processReturn(model, discoveryInformation,
					pageParameters, WicketUtils.getCompleteUrl(this,
						DlibraRegistrationPage.class, true));
				if (model.getOpenIdData() == null) {
					error("Open ID Confirmation Failed. No information was retrieved from the OpenID Provider. You will have to enter all information by hand into the text fields provided.");
				}
				DlibraService.provisionAuthenticatedUserModel(model);
				logIn(model);
			}
		}

		add(new UserInfoDisplayForm("dLibraForm", model));
		add(new MyExpImportForm("myExpImportForm", model));
		add(new FeedbackPanel("feedback"));
	}


	private void startMyExpImport()
	{
		String oauthCallbackURL = WicketUtils.getCompleteUrl(this,
			MyExpImportPage.class, false);

		OAuthService service = MyExpApi.getOAuthService(oauthCallbackURL);
		Token requestToken = service.getRequestToken();
		getSession()
				.setAttribute(Constants.SESSION_REQUEST_TOKEN, requestToken);
		String authorizationUrl = service.getAuthorizationUrl(requestToken);
		getRequestCycle().scheduleRequestHandlerAfterCurrent(
			new RedirectRequestHandler(authorizationUrl));
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
					startMyExpImport();
				}
			};
			importButton.setOutputMarkupId(true);
			add(importButton);

		}
	}

}
