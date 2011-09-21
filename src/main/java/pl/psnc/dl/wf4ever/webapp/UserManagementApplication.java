package pl.psnc.dl.wf4ever.webapp;

import java.util.Locale;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import pl.psnc.dl.wf4ever.webapp.pages.AuthenticationPage;
import pl.psnc.dl.wf4ever.webapp.pages.DlibraRegistrationPage;
import pl.psnc.dl.wf4ever.webapp.pages.ErrorPage;
import pl.psnc.dl.wf4ever.webapp.pages.HelpPage;
import pl.psnc.dl.wf4ever.webapp.pages.OAuthAccessTokenEndpointPage;
import pl.psnc.dl.wf4ever.webapp.pages.OAuthAuthorizationEndpointPage;

/**
 * 
 * @author Piotr Hołubowicz
 *
 */
public class UserManagementApplication
	extends WebApplication
{

	public UserManagementApplication()
	{
		super();
	}


	@Override
	public void init()
	{
		super.init();
		mountPage("/authenticate", AuthenticationPage.class);
		mountPage("/register", DlibraRegistrationPage.class);
		mountPage("/authorize", OAuthAuthorizationEndpointPage.class);
		mountPage("/accesstoken", OAuthAccessTokenEndpointPage.class);
		mountPage("/error", ErrorPage.class);
		mountPage("/help", HelpPage.class);

		Locale.setDefault(Locale.ENGLISH);
	}


	/**
	 * Return the "Home" page used by the application. Wicket will redirect
	 * here if you don't explicitly supply a Page destination.
	 */
	public Class< ? extends WebPage> getHomePage()
	{
		return DlibraRegistrationPage.class;
	}

}
