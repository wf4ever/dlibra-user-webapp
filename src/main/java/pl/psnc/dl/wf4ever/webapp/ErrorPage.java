/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ErrorPage
	extends TemplatePage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3233388849667095897L;

	public static final String MESSAGE = "message";


	public ErrorPage(PageParameters pageParameters)
	{
		super(pageParameters);

		if (pageParameters.get(MESSAGE) != null) {
			add(new Label("message", new Model<String>(pageParameters.get(
				MESSAGE).toString())));
		}
		else {
			add(new Label("message"));
		}
	}
}
