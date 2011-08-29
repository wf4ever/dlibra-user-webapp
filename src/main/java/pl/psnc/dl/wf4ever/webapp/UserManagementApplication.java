package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import pl.psnc.dl.wf4ever.webapp.pages.AuthenticationPage;
import pl.psnc.dl.wf4ever.webapp.pages.DlibraRegistrationPage;
import pl.psnc.dl.wf4ever.webapp.pages.ErrorPage;
import pl.psnc.dl.wf4ever.webapp.pages.MyExpImportPage;

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
		mountPage("/import", MyExpImportPage.class);
		mountPage("/error", ErrorPage.class);
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
