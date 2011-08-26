/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Piotr Hołubowicz
 *
 */
public class AboutPage
	extends TemplatePage
{

	private static final long serialVersionUID = 2960549491810135112L;


	public AboutPage(PageParameters pageParameters)
	{
		super(pageParameters);
	}


	public AboutPage()
	{
		this(new PageParameters());
	}

}
