/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.wizard;

import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.NewResearchObjectModel;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ConfirmRONamesStep
	extends DynamicWizardStep
{

	private static final long serialVersionUID = -3238571883021517707L;


	public ConfirmRONamesStep(IDynamicWizardStep previousStep, ImportModel model)
	{
		super(previousStep, "Confirm Research Object names", "",
				new Model<ImportModel>(model));

		Form< ? > form = new Form<Void>("form");
		add(form);
		@SuppressWarnings("serial")
		ListView<NewResearchObjectModel> list = new ListView<NewResearchObjectModel>(
				"resourceListView", model.getResearchObjectsProcessed()) {

			protected void populateItem(ListItem<NewResearchObjectModel> item)
			{
				NewResearchObjectModel ro = (NewResearchObjectModel) item
						.getModelObject();
				ro.setDefaultName();
				item.add(new RequiredTextField<String>("name",
						new PropertyModel<String>(ro, "name"), String.class));
				item.add(new Label("content", ro.getContentDesc()));
				
			}
		};
		list.setReuseItems(true);
		form.add(list);
	}


	@Override
	public void applyState()
	{
		super.applyState();
		ImportModel model = (ImportModel) this.getDefaultModelObject();
		model.finishProcessingResearchObjects();
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep#isLastStep()
	 */
	@Override
	public boolean isLastStep()
	{
		return false;
	}


	/* (non-Javadoc)
	 * @see org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep#next()
	 */
	@Override
	public IDynamicWizardStep next()
	{
		return new RODefinedStep(this, (ImportModel) this.getDefaultModelObject());
	}

}
