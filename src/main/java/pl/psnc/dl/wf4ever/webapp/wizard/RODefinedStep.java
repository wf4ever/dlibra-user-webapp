/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.wizard;

import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.NewResearchObjectModel;

/**
 * @author Piotr Hołubowicz
 *
 */
public class RODefinedStep
	extends DynamicWizardStep
{

	private static final long serialVersionUID = 4637256013660809942L;

	private boolean addMoreROs = true;


	@SuppressWarnings("serial")
	public RODefinedStep(IDynamicWizardStep previousStep, ImportModel model)
	{
		super(previousStep, "Defined Research Objects", "",
				new Model<ImportModel>(model));

		add(new ListView<NewResearchObjectModel>("resourceListView",
				model.getResearchObjects()) {

			protected void populateItem(ListItem<NewResearchObjectModel> item)
			{
				NewResearchObjectModel ro = (NewResearchObjectModel) item
						.getModelObject();
				item.add(new Label("name", ro.getName()));

			}
		});

		Form<RODefinedStep> form = new Form<RODefinedStep>("form",
				new CompoundPropertyModel<RODefinedStep>(this));
		add(form);
		form.add(new CheckBox("addMoreROs"));

	}


	@Override
	public boolean isLastStep()
	{
		return false;
	}


	@Override
	public IDynamicWizardStep next()
	{
		if (addMoreROs) {
			return new SelectResourcesStep(this,
					(ImportModel) this.getDefaultModelObject());
		}
		else {
			return new ImportDataStep(this,
				(ImportModel) this.getDefaultModelObject());
		}
	}


	/**
	 * @return the addMoreROs
	 */
	public boolean isAddMoreROs()
	{
		return addMoreROs;
	}


	/**
	 * @param addMoreROs the addMoreROs to set
	 */
	public void setAddMoreROs(boolean addMoreROs)
	{
		this.addMoreROs = addMoreROs;
	}

}
