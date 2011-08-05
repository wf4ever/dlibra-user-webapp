package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * This application is based on OpenID/Wicket app presented by J Steven Perry, 
 * http://makotoconsulting.com
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
