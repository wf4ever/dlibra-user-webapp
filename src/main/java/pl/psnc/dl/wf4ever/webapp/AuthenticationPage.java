package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;
import pl.psnc.dl.wf4ever.webapp.services.OpenIdService;

public class AuthenticationPage
	extends TemplatePage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8975579933617712699L;


	public AuthenticationPage()
	{
		this(new PageParameters());
	}


	public AuthenticationPage(PageParameters pageParameters)
	{
		super(pageParameters);
		DlibraUserModel model = getDlibraUserModel();
		if (!pageParameters.get(OpenIdService.MY_EXP_ID).isNull()) {
			model.setMyExpId(pageParameters.get(OpenIdService.MY_EXP_ID)
					.toString());
		}
		add(new OpenIdRegistrationForm("form", this,
				WicketUtils.getOpenIdCallbackUrl(this), model));
	}

	/**
	 * The Form used for this Page.
	 * 
	 * @author J Steven Perry
	 * @author http://makotoconsulting.com
	 */
	public static class OpenIdRegistrationForm
		extends Form<DlibraUserModel>
	{

		private static final long serialVersionUID = 3828134783479387778L;


		public OpenIdRegistrationForm(String id,
				final AuthenticationPage owningPage, final String returnToUrl,
				final DlibraUserModel model)
		{

			super(id);
			//
			setModel(new CompoundPropertyModel<DlibraUserModel>(model));
			//
			TextField<String> openId = new RequiredTextField<String>("openId");
			openId.setLabel(new Model<String>("Your Open ID"));
			add(openId);
			// This is the "business end" of making the authentication request.
			/// The sequence of interaction with the OP is really hidden from us
			/// here by using RegistrationService.
			Button confirmOpenIdButton = new Button("confirmOpenIdButton") {

				private static final long serialVersionUID = -723600550506568627L;


				public void onSubmit()
				{
					// Delegate to Open ID code
					String userSuppliedIdentifier = model.getOpenId();
					DiscoveryInformation discoveryInformation = OpenIdService
							.performDiscoveryOnUserSuppliedIdentifier(userSuppliedIdentifier);
					// Store the disovery results in session.
					Session session = owningPage.getSession();
					session.setAttribute(OpenIdService.DISCOVERY_INFORMATION,
						discoveryInformation);
					// Create the AuthRequest
					AuthRequest authRequest = OpenIdService
							.createOpenIdAuthRequest(discoveryInformation,
								returnToUrl);
					// Now take the AuthRequest and forward it on to the OP
					IRequestHandler reqHandler = new RedirectRequestHandler(
							authRequest.getDestinationUrl(true));
					getRequestCycle().scheduleRequestHandlerAfterCurrent(
						reqHandler);
				}
			};
			add(confirmOpenIdButton);
		}
	}
}
