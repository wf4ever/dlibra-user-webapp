/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.pages;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.OAuthRequest;
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
import pl.psnc.dl.wf4ever.webapp.utils.Constants;
import pl.psnc.dl.wf4ever.webapp.utils.WicketUtils;
import pl.psnc.dl.wf4ever.webapp.wizard.StartImportStep;

/**
 * @author Piotr Hołubowicz
 *
 */
public class MyExpImportPage
	extends TemplatePage
{

	private static final String WHOAMI_URL = "http://www.myexperiment.org/whoami.xml";

	private static final String GET_USER_URL = "http://www.myexperiment.org/user.xml?id=%d&elements=id,name,email,city,country,website,packs,workflows,files";

	private static final long serialVersionUID = 4637256013660809942L;

	private static final String OAUTH_VERIFIER = "oauth_token";


	public MyExpImportPage(PageParameters pageParameters)
	{
		super(pageParameters);
		if (willBeRedirected)
			return;

		DlibraUser user = getDlibraUserModel();
		OAuthService service = MyExpApi.getOAuthService(WicketUtils
				.getCompleteUrl(this, MyExpImportPage.class, true));

		if (user.getMyExpAccessToken() == null
				&& pageParameters.get(OAUTH_VERIFIER) != null) {
			Verifier verifier = new Verifier(pageParameters.get(OAUTH_VERIFIER)
					.toString());
			Token requestToken = (Token) getSession().getAttribute(
				Constants.SESSION_REQUEST_TOKEN);
			user.setMyExpAccessToken(service.getAccessToken(requestToken,
				verifier));
			HibernateService.storeUser(user);
		}

		if (user.getMyExpAccessToken() == null) {
			String home = urlFor(AuthenticationPage.class, null).toString();
			getRequestCycle().scheduleRequestHandlerAfterCurrent(
				new RedirectRequestHandler(home));
			content.setVisible(false);
			return;
		}

		User myExpUser = null;
		try {
			OAuthRequest request = new OAuthRequest(Verb.GET, WHOAMI_URL);
			service.signRequest(user.getMyExpAccessToken(), request);
			Response response = request.send();
			myExpUser = createMyExpUserModel(response.getBody());

			request = new OAuthRequest(Verb.GET, String.format(GET_USER_URL,
				myExpUser.getId()));
			service.signRequest(user.getMyExpAccessToken(), request);
			response = request.send();
			myExpUser = createMyExpUserModel(response.getBody());
		}
		catch (JAXBException e) {
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

	class ImportWizard
		extends Wizard
	{

		private static final long serialVersionUID = -8520850154339581229L;


		public ImportWizard(String id, ImportModel model)
		{
			super(id, new DynamicWizardModel(new StartImportStep(model)), false);
		}

	}

}
