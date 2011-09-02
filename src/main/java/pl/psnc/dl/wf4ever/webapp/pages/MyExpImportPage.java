/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.pages;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.myexp.User;
import pl.psnc.dl.wf4ever.webapp.services.HibernateService;
import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;
import pl.psnc.dl.wf4ever.webapp.services.OAuthHelpService;
import pl.psnc.dl.wf4ever.webapp.utils.Constants;
import pl.psnc.dl.wf4ever.webapp.utils.WicketUtils;
import pl.psnc.dl.wf4ever.webapp.wizard.ImportWizard;

/**
 * @author Piotr Hołubowicz
 *
 */
public class MyExpImportPage
	extends TemplatePage
{

	private static final long serialVersionUID = 4637256013660809942L;


	public MyExpImportPage(PageParameters pageParameters)
	{
		super(pageParameters);
		if (willBeRedirected)
			return;

		info("You have successfully authorized Wf4Ever User Management Service to\n"
				+ " import your data from myExperiment.");

		DlibraUser user = getDlibraUserModel();
		OAuthService service = MyExpApi.getOAuthService(WicketUtils
				.getCompleteUrl(this, MyExpImportPage.class, true));

		if (user.getMyExpAccessToken() == null
				&& !pageParameters.get(MyExpApi.OAUTH_VERIFIER).isEmpty()) {
			Verifier verifier = new Verifier(pageParameters.get(MyExpApi.OAUTH_VERIFIER)
					.toString());
			Token requestToken = (Token) getSession().getAttribute(
				Constants.SESSION_REQUEST_TOKEN);
			user.setMyExpAccessToken(service.getAccessToken(requestToken,
				verifier));
			HibernateService.storeUser(user);
		}

		if (user.getMyExpAccessToken() == null) {
			goToPage(DlibraRegistrationPage.class, pageParameters);
			content.setVisible(false);
			return;
		}

		User myExpUser = null;
		try {
			Response response = OAuthHelpService.sendRequest(service, Verb.GET,
				MyExpApi.WHOAMI_URL, user.getMyExpAccessToken());
			myExpUser = createMyExpUserModel(response.getBody());

			response = OAuthHelpService.sendRequest(service, Verb.GET,
				String.format(MyExpApi.GET_USER_URL, myExpUser.getId()),
				user.getMyExpAccessToken());
			myExpUser = createMyExpUserModel(response.getBody());
		}
		catch (Exception e) {
			String page = urlFor(ErrorPage.class, null).toString()
					+ "?message=" + e.getMessage();
			getRequestCycle().scheduleRequestHandlerAfterCurrent(
				new RedirectRequestHandler(page));
			content.setVisible(false);
			return;
		}

		ImportModel model = new ImportModel(myExpUser);
		content.add(new ImportWizard("wizard", model));

	}


	private User createMyExpUserModel(String xml)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(User.class);

		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return (User) u.unmarshal(new StreamSource(new StringReader(xmlStr
				.toString())));
	}

}
