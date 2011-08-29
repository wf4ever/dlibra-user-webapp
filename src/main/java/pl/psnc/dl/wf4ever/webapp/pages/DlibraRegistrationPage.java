package pl.psnc.dl.wf4ever.webapp.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;
import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;
import pl.psnc.dl.wf4ever.webapp.utils.Constants;
import pl.psnc.dl.wf4ever.webapp.utils.WicketUtils;

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
		
		DlibraUser model = getDlibraUserModel();

		Form<DlibraUser> form = new Form<DlibraUser>("form",
				new CompoundPropertyModel<DlibraUser>(model));
		form.setOutputMarkupId(true);
		content.add(form);

		final WebMarkupContainer message = createMessageFragment(model, content);
		message.setOutputMarkupId(true);
		form.add(message);

		final WebMarkupContainer credentials = createCredentialsDiv(model);
		form.add(credentials);

		final Button importButton = new Button("myExpImportButton") {

			@Override
			public void onSubmit()
			{
				startMyExpImport();
			}
		};
		importButton.setEnabled(model.isRegistered());
		form.add(importButton).setOutputMarkupId(true);

		form.add(
			new AjaxButton("registerButtonText", new PropertyModel<String>(
					model, "registerButtonText")) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					DlibraUser model = (DlibraUser) getForm().getModelObject();
					try {
						if (model.isRegistered()) {
							DlibraService.deleteWorkspace(model);
						}
						else {
							if (!DlibraService.createWorkspace(model)) {
								info("Registered an existing account");
							}
						}
					}
					catch (Exception e) {
						error(e.getMessage() != null ? e.getMessage()
								: "Unknown error");
					}
					target.add(message);
					target.add(this);
					importButton.setEnabled(model.isRegistered());
					target.add(importButton);
					WebMarkupContainer div = createCredentialsDiv(model);
					getParent().replace(div);
					Fragment f = createMessageFragment(model,
						content);
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
			div.add(new Label("password", new PropertyModel<String>(model,
					"password")));
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
			f = new RegisteredFragment("message", "registered", container,
					model);
		}
		else {
			f = new Fragment("message", "notRegistered", container);
		}
		return f;
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

	@SuppressWarnings("serial")
	private class RegisteredFragment
		extends Fragment
	{

		public RegisteredFragment(String id, String markupId,
				MarkupContainer markupProvider, DlibraUser model)
		{
			super(id, markupId, markupProvider);
			add(new Label("dLibraAccessTokenString", new PropertyModel<String>(model,
					"dlibraAccessTokenString")));
		}

	}

}
