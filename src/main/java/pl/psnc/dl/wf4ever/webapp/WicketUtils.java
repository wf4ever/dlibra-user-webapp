/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author piotrhol
 *
 */
public class WicketUtils
{

	/**
	 * Generates the returnToUrl parameter that is passed to the OP. The
	 * User Agent (i.e., the browser) will be directed to this page following
	 * authentication.
	 * 
	 */
	public static String getOpenIdCallbackUrl(WebPage page)
	{
		PageParameters params = new PageParameters();
		params.add("is_return", "true");
		return RequestCycle
				.get()
				.getUrlRenderer()
				.renderFullUrl(
					Url.parse(page.urlFor(DlibraRegistrationPage.class, params)
							.toString()));
	}


	public static String getMyExpImportCallbackUrl(WebPage page)
	{
		PageParameters params = new PageParameters();
		params.add("is_return", "true");
		return RequestCycle
				.get()
				.getUrlRenderer()
				.renderFullUrl(
					Url.parse(page.urlFor(MyExpImportPage.class, params)
							.toString()));
	}

}
