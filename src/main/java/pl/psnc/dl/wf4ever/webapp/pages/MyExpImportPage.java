/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.pages;

import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.myexp.User;
import pl.psnc.dl.wf4ever.webapp.services.HibernateService;
import pl.psnc.dl.wf4ever.webapp.services.MyExpApi;
import pl.psnc.dl.wf4ever.webapp.services.OAuthException;
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

		if (user.getMyExpAccessToken() == null) {
			Token accessToken = retrieveAccessToken(pageParameters, service);
			if (accessToken != null) {
				user.setMyExpAccessToken(accessToken);
				HibernateService.storeUser(user);
			}
			else {
				goToPage(DlibraRegistrationPage.class, pageParameters);
				content.setVisible(false);
				return;
			}
		}

		try {
			User myExpUser = retrieveMyExpUser(user.getMyExpAccessToken(),
				service);
			ImportModel model = new ImportModel(myExpUser);
			content.add(new ImportWizard("wizard", model));
		}
		catch (OAuthException e) {
			if (e.getResponse().getCode() == 401) {
				user.setMyExpAccessToken(null);
				startMyExpAuthorization();
			}
			else {
				String page = urlFor(ErrorPage.class, null).toString()
						+ "?message=" + e.getMessage();
				getRequestCycle().scheduleRequestHandlerAfterCurrent(
					new RedirectRequestHandler(page));
				content.setVisible(false);
				return;
			}
		}
		catch (Exception e) {
			String page = urlFor(ErrorPage.class, null).toString()
					+ "?message=" + e.getMessage();
			getRequestCycle().scheduleRequestHandlerAfterCurrent(
				new RedirectRequestHandler(page));
			content.setVisible(false);
			return;
		}
	}

}
