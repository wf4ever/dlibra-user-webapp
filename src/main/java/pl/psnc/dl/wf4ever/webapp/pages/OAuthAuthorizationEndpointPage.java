/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters.NamedPair;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.OAuthClient;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;
import pl.psnc.dl.wf4ever.webapp.services.OAuthHelpService;

/**
 * @author Piotr Hołubowicz
 * 
 * This is the OAuth 2.0 authorization endpoint.
 */
public class OAuthAuthorizationEndpointPage
	extends TemplatePage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3793214124123802219L;

	private static final Logger log = Logger
			.getLogger(OAuthAuthorizationEndpointPage.class);

	private OAuthClient client;

	private String state;


	public OAuthAuthorizationEndpointPage(PageParameters pageParameters)
	{
		super(pageParameters);
		if (willBeRedirected)
			return;
		if (pageParameters.get(OAuthHelpService.QUERY_PARAM_RESPONSE_TYPE)
				.isNull()) {
			error("Missing response type.");
		}
		else {
			String responseType = pageParameters.get(
				OAuthHelpService.QUERY_PARAM_RESPONSE_TYPE).toString();
			if (responseType.equals(OAuthHelpService.RESPONSE_TYPE_AUTH_CODE)) {
				error("Authorization code flow is not supported yet.");
			}
			else if (responseType.equals(OAuthHelpService.RESPONSE_TYPE_TOKEN)) {
				this.client = processImplicitGrantFlow(pageParameters);
			}
			else {
				error(String.format("Unknown response type: %s.", responseType));
			}
		}

		if (client != null) {
			content.add(new AuthorizeFragment("entry", "validRequest", content,
					client));
		}
		else {
			content.add(new Fragment("entry", "invalidRequest", content));
		}
	}


	/**
	 * @param pageParameters
	 * @return 
	 */
	private OAuthClient processImplicitGrantFlow(PageParameters pageParameters)
	{
		if (pageParameters.get(OAuthHelpService.QUERY_PARAM_CLIENT_ID).isNull()) {
			error("Missing client id.");
		}
		else {
			String clientId = pageParameters.get(
				OAuthHelpService.QUERY_PARAM_CLIENT_ID).toString();
			try {
				OAuthClient client = DlibraService.getClient(clientId);
				if (pageParameters.get(
					OAuthHelpService.QUERY_PARAM_REDIRECT_URI).isNull()) {
					error("Missing redirect URI.");
				}
				else {
					String redirectUri = pageParameters.get(
						OAuthHelpService.QUERY_PARAM_REDIRECT_URI).toString();
					if (!client.getRedirectionURI().equals(redirectUri)) {
						error("Redirect URI does not match client redirect URI.");
					}
					else {
						if (!pageParameters.get(
							OAuthHelpService.QUERY_PARAM_STATE).isNull()) {
							state = pageParameters.get(
								OAuthHelpService.QUERY_PARAM_STATE).toString();
						}
						return client;
					}
				}
			}
			catch (Exception e) {
				error("Invalid client id: " + e.getMessage() + ".");
			}
		}
		return null;
	}


	private String createResponseURL(String redirectionURI,
			PageParameters params)
	{
		List<String> p = new ArrayList<String>();
		for (NamedPair np : params.getAllNamed()) {
			p.add(np.getKey() + "=" + np.getValue());
		}
		return redirectionURI + "#" + StringUtils.join(p, "&");
	}

	private class AuthorizeFragment
		extends Fragment
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -3040124186474465047L;


		@SuppressWarnings("serial")
		public AuthorizeFragment(String id, String markupId,
				MarkupContainer markupProvider, final OAuthClient client)
		{
			super(id, markupId, markupProvider);
			Form< ? > form = new Form<Void>("form");
			add(form);
			form.add(new Label("name", client.getName()));
			form.add(new Button("authorize") {

				@Override
				public void onSubmit()
				{
					super.onSubmit();
					DlibraUser user = getDlibraUserModel();
					try {
						String token = DlibraService.getAccessToken(
							user.getUsername(), client.getClientId());
						PageParameters params = new PageParameters();
						params.add(OAuthHelpService.QUERY_PARAM_ACCESS_TOKEN,
							token);
						params.add(OAuthHelpService.QUERY_PARAM_TOKEN_TYPE,
							"bearer");
						if (state != null) {
							params.add(OAuthHelpService.QUERY_PARAM_STATE,
								state);
						}
						String url = createResponseURL(
							client.getRedirectionURI(), params);
						getRequestCycle().scheduleRequestHandlerAfterCurrent(
							new RedirectRequestHandler(url));
					}
					catch (Exception e) {
						error(e);
						log.error(e);
					}
				}
			});
			form.add(new Button("reject") {

				@Override
				public void onSubmit()
				{
					super.onSubmit();
					PageParameters params = new PageParameters();
					params.add("error", "access_denied");
					if (state != null) {
						params.add(OAuthHelpService.QUERY_PARAM_STATE, state);
					}
					String url = createResponseURL(client.getRedirectionURI(),
						params);
					getRequestCycle().scheduleRequestHandlerAfterCurrent(
						new RedirectRequestHandler(url));
				}
			});
		}

	}

}
