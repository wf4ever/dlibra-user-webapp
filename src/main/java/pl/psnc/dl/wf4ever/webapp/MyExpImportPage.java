/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;

/**
 * @author piotrhol
 *
 */
public class MyExpImportPage
	extends WebPage
{

	private static final long serialVersionUID = 4637256013660809942L;

	private static final String OAUTH_VERIFIER = "oauth_verifier";

	private static final String ACCESS_TOKEN = "accessToken";


	public MyExpImportPage(PageParameters pageParameters)
	{
		if (pageParameters.get("is_return") != null) {
			if (pageParameters.get(OAUTH_VERIFIER) == null) {
				String home = urlFor(AuthenticationPage.class, null)
						.toString();
				getRequestCycle().scheduleRequestHandlerAfterCurrent(
					new RedirectRequestHandler(home));

			}
			Verifier verifier = new Verifier(pageParameters.get(OAUTH_VERIFIER)
					.toString());
			Token requestToken = (Token) getSession().getAttribute(
				DlibraRegistrationPage.REQUEST_TOKEN);

			OAuthService service = MyExpApi.getOAuthService(WicketUtils
					.getMyExpImportCallbackUrl(this));
			Token accessToken = service.getAccessToken(requestToken, verifier);
			getSession().setAttribute(ACCESS_TOKEN, accessToken);
		}
	}

}
