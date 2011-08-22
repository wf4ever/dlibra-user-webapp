/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.FileImportModel;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel.ImportType;
import pl.psnc.dl.wf4ever.webapp.model.MyExpUser;
import pl.psnc.dl.wf4ever.webapp.model.PackImportModel;
import pl.psnc.dl.wf4ever.webapp.model.WorkflowImportModel;
import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;
import pl.psnc.dl.wf4ever.webapp.utils.Constants;
import pl.psnc.dl.wf4ever.webapp.utils.WicketUtils;

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

		WebMarkupContainer body = new WebMarkupContainer("body");
		add(body);

		if (pageParameters.get(OAUTH_VERIFIER) == null) {
			String home = urlFor(AuthenticationPage.class, null).toString();
			getRequestCycle().scheduleRequestHandlerAfterCurrent(
				new RedirectRequestHandler(home));
			body.setVisible(false);
			return;
		}

		Verifier verifier = new Verifier(pageParameters.get(OAUTH_VERIFIER)
				.toString());
		Token requestToken = (Token) getSession().getAttribute(
			Constants.SESSION_REQUEST_TOKEN);

		OAuthService service = MyExpApi.getOAuthService(WicketUtils
				.getCompleteUrl(this, MyExpImportPage.class, true));
		Token accessToken = service.getAccessToken(requestToken, verifier);
		getSession().setAttribute(Constants.SESSION_ACCESS_TOKEN, accessToken);

		MyExpUser myExpUser = null;
		try {
			OAuthRequest request = new OAuthRequest(Verb.GET, WHOAMI_URL);
			service.signRequest(accessToken, request);
			Response response = request.send();
			myExpUser = createMyExpUserModel(response.getBody());

			request = new OAuthRequest(Verb.GET, String.format(GET_USER_URL, myExpUser.getId()));
			service.signRequest(accessToken, request);
			response = request.send();
			myExpUser = createMyExpUserModel(response.getBody());
		}
		catch (JAXBException e) {
			String page = urlFor(ErrorPage.class, null).toString()
					+ "?message=" + e.getMessage();
			getRequestCycle().scheduleRequestHandlerAfterCurrent(
				new RedirectRequestHandler(page));
			body.setVisible(false);
			return;
		}

		body.add(new Label("userName", new Model<String>(myExpUser
				.getName())));
		body.add(new Label("packsCnt", new Model<Integer>(myExpUser
				.getPacks().size())));
		body.add(new Label("workflowsCnt", new Model<Integer>(myExpUser
				.getWorkflows().size())));
		body.add(new Label("filesCnt", new Model<Integer>(myExpUser
				.getFiles().size())));
		
		ImportModel fileImportModel = null;
		if (myExpUser.getFiles().isEmpty()) {
			body.add(createUnvisibileDiv("filesDiv"));
		} else {
			fileImportModel = new FileImportModel(ImportType.ALL_AS_1_RO, myExpUser.getFiles());
			body.add(new ResourceImportPanel("filesDiv", fileImportModel));
		}

		ImportModel workflowImportModel = null;
		if (myExpUser.getFiles().isEmpty()) {
			body.add(createUnvisibileDiv("workflowsDiv"));
		} else {
			workflowImportModel = new WorkflowImportModel(ImportType.ALL_AS_1_RO, myExpUser.getWorkflows());
			body.add(new ResourceImportPanel("workflowsDiv", workflowImportModel));
		}
		
		ImportModel packImportModel = null;
		if (myExpUser.getFiles().isEmpty()) {
			body.add(createUnvisibileDiv("packsDiv"));
		} else {
			packImportModel = new PackImportModel(ImportType.ALL_AS_MANY_ROS, myExpUser.getPacks());
			body.add(new ResourceImportPanel("packsDiv", packImportModel));
		}

		WebMarkupContainer startDiv = new WebMarkupContainer("startDiv");
		body.add(startDiv);
		if (myExpUser.getFiles().isEmpty() && myExpUser.getWorkflows().isEmpty() && myExpUser.getPacks().isEmpty()) {
			startDiv.setVisible(false);
		} else {
			Form<?> startImportForm = new Form<Void>("startImportForm");
			startDiv.add(startImportForm);
			Button startImport = new Button("startImport");
			startImportForm.add(startImport);
		}
}


	/**
	 * @return
	 */
	private WebMarkupContainer createUnvisibileDiv(String id)
	{
		WebMarkupContainer filesDiv = new WebMarkupContainer(id);
		filesDiv.setVisible(false);
		return filesDiv;
	}


	private MyExpUser createMyExpUserModel(String xml)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(MyExpUser.class);

		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return (MyExpUser) u.unmarshal(new StreamSource(new StringReader(xmlStr
				.toString())));
	}

}
