/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.wizard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

/**
 * @author Piotr Hołubowicz
 *
 */
public class RONamesValidator
	extends AbstractFormValidator
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1337197226405912697L;

	private List<FormComponent<String>> components;

	private Map<String, Integer> names = new HashMap<String, Integer>();


	public RONamesValidator(List<FormComponent<String>> components)
	{
		this.components = components;
	}


	/* (non-Javadoc)
	 * @see org.apache.wicket.markup.html.form.validation.IFormValidator#getDependentFormComponents()
	 */
	@Override
	public FormComponent< ? >[] getDependentFormComponents()
	{
		return this.components.toArray(new FormComponent[components.size()]);
	}


	/* (non-Javadoc)
	 * @see org.apache.wicket.markup.html.form.validation.IFormValidator#validate(org.apache.wicket.markup.html.form.Form)
	 */
	@Override
	public void validate(Form< ? > form)
	{
		names.clear();
		for (FormComponent<String> component : components) {
			String input = component.getInput();
			int cnt = names.containsKey(input) ? names.get(input) : 0;
			names.put(input, cnt);
		}
		for (Map.Entry<String, Integer> entry : names.entrySet()) {
			if (entry.getValue() > 1) {
				form.getPage().warn(
					String.format("Value '%s' is used %d times",
						entry.getKey(), entry.getValue()));
			}
		}
	}

}
