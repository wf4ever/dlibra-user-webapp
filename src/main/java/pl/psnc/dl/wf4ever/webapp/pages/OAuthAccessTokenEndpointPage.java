/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.pages;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.webapp.model.AuthCodeData;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;
import pl.psnc.dl.wf4ever.webapp.utils.Constants;

/**
 * @author Piotr Hołubowicz
 * 
 * This is the OAuth 2.0 access token endpoint.
 */
public class OAuthAccessTokenEndpointPage
	extends WebPage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3793214124123802219L;

	private static final Logger log = Logger
			.getLogger(OAuthAccessTokenEndpointPage.class);

	private String json;

	private int status;


	@SuppressWarnings("unchecked")
	public OAuthAccessTokenEndpointPage(PageParameters pageParameters)
	{
		super(pageParameters);

		prepareResponse(
			(Map<String, AuthCodeData>) getSession().getAttribute(
				Constants.SESSION_AUTH_CODE_DATA), pageParameters);

		getRequestCycle().replaceAllRequestHandlers(new IRequestHandler() {

			@Override
			public void respond(IRequestCycle requestCycle)
			{
				WebResponse response = ((WebResponse) requestCycle
						.getResponse());
				response.setStatus(status);
				response.setContentType("application/json;charset=UTF-8");
				response.addHeader("Cache-control", "no-store");
				response.addHeader("Pragma", "no-cache");
				response.write(json);
				log.debug("Returning access token: " + json);
			}


			@Override
			public void detach(IRequestCycle arg0)
			{
				// TODO Auto-generated method stub

			}
		});
	}


	private void prepareResponse(Map<String, AuthCodeData> authCodeData,
			PageParameters pageParameters)
	{
		String error = null;
		String errorDesc = null;
		AuthCodeData data = null;
		if (pageParameters.get("grant_type") == null
				|| pageParameters.get("code") == null) {
			error = "invalid_request";
			errorDesc = "Grant type or code missing";
		}
		else if (!pageParameters.get("grant_type").toString().equals("authorization_code")) {
			error = "unsupported_grant_type";
			errorDesc = "grant type: " + pageParameters.get("grant_type").toString();
		}
		else {
			String code = pageParameters.get("code").toString();
			if (authCodeData == null || !authCodeData.containsKey(code)) {
				error = "invalid_grant";
				errorDesc = "Code " + code + " is not valid";
			}
			else {
				data = authCodeData.get(code);
				if (data.getProvidedRedirectURI() != null
						&& (pageParameters.get("redirect_uri") == null || !pageParameters
								.get("redirect_uri").toString().equals(
									data.getProvidedRedirectURI()))) {
					error = "invalid_grant";
					errorDesc = "Redirect URI is not valid";
				}
			}
		}
		if (error != null) {
			json = String.format("{\"error\": \"%s\", \"error_description\": \"%s\"}", error, errorDesc);
			status = 400;
		}
		else {
			try {
				String token = DlibraService.getAccessToken(data.getUserId(),
					data.getClientId());
				json = String.format(
					"{\"access_token\": \"%s\", \"token\": \"bearer\"}", token);
				status = 200;
			}
			catch (Exception e) {
				json = e.getMessage();
				status = 500;
			}
		}

	}
}
