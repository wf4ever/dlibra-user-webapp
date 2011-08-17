/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.services.Constants;
import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;
import pl.psnc.dl.wf4ever.webapp.utils.WicketUtils;

/**
 * @author piotrhol
 *
 */
public class MyExpImportPage
	extends TemplatePage
{

	private static final long serialVersionUID = 4637256013660809942L;

	private static final String OAUTH_VERIFIER = "oauth_token";


	public MyExpImportPage(PageParameters pageParameters)
	{
		super(pageParameters);

		if (pageParameters.get(OAUTH_VERIFIER) == null) {
			String home = urlFor(AuthenticationPage.class, null).toString();
			getRequestCycle().scheduleRequestHandlerAfterCurrent(
				new RedirectRequestHandler(home));

		}
		Verifier verifier = new Verifier(pageParameters.get(OAUTH_VERIFIER)
				.toString());
		Token requestToken = (Token) getSession().getAttribute(
			Constants.SESSION_REQUEST_TOKEN);

		OAuthService service = MyExpApi.getOAuthService(WicketUtils
				.getCompleteUrl(this, MyExpImportPage.class, true));
		Token accessToken = service.getAccessToken(requestToken, verifier);
		getSession().setAttribute(Constants.SESSION_ACCESS_TOKEN, accessToken);

		add(new Label("accessToken", new Model<String>(accessToken.toString())));
	}

}
