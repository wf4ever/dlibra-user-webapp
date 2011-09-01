/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.wizard;

import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.OddEvenListItem;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.PatternValidator;

import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.ResearchObject;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ConfirmRONamesStep
	extends AbstractStep
{

	private static final long serialVersionUID = -3238571883021517707L;

	private boolean addMoreROs = true;


	public ConfirmRONamesStep(IDynamicWizardStep previousStep, ImportModel model)
	{
		super(previousStep, "Confirm names", model);

		Form< ? > form = new Form<Void>("form");
		add(form);
		@SuppressWarnings("serial")
		ListView<ResearchObject> list = new ListView<ResearchObject>(
				"resourceListView", model.getResearchObjectsProcessed()) {

			@Override
			protected ListItem<ResearchObject> newItem(int index,
					IModel<ResearchObject> itemModel)
			{
				return new OddEvenListItem<ResearchObject>(index, itemModel);
			};


			protected void populateItem(ListItem<ResearchObject> item)
			{
				ResearchObject ro = (ResearchObject) item.getModelObject();
				ro.setDefaultName();
				item.add(new RequiredTextField<String>("name",
						new PropertyModel<String>(ro, "name"), String.class).add(new PatternValidator("[\\w]+")));
				item.add(new Label("content", ro.getContentDesc()));

			}
		};
		list.setReuseItems(true);
		form.add(list);

		Form<ConfirmRONamesStep> formAddMore = new Form<ConfirmRONamesStep>(
				"formAddMore", new CompoundPropertyModel<ConfirmRONamesStep>(
						this));
		formAddMore.add(new CheckBox("addMoreROs"));
		add(formAddMore);
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
