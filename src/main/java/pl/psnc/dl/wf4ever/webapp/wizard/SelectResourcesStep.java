/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.wizard;

import java.util.Arrays;

import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.ResourceSelectionModel;
import pl.psnc.dl.wf4ever.webapp.model.ResourceSelectionModel.ImportType;

/**
 * @author Piotr Hołubowicz
 *
 */
public class SelectResourcesStep
	extends DynamicWizardStep
{

	private static final long serialVersionUID = -7984392838783804920L;

	private ResourceSelectionModel selectionModel;


	public SelectResourcesStep(IDynamicWizardStep previousStep,
			ImportModel model)
	{
		super(previousStep, "c", "D", new Model<ImportModel>(model));

		selectionModel = new ResourceSelectionModel();

		RadioChoice<ImportType> importType = new RadioChoice<ImportType>(
				"importType", new PropertyModel<ImportType>(selectionModel,
						"importType"), Arrays.asList(ImportType.values()),
				new ImportTypeChoiceRenderer());

		add(importType);

		if (model.getMyExpUser().getFiles().isEmpty()) {
			add(createUnvisibileDiv("filesDiv"));
		}
		else {
			add(new ResourceListPanel("filesDiv", "Files", model.getMyExpUser()
					.getFiles(), selectionModel.getSelectedFiles()));
		}
		if (model.getMyExpUser().getWorkflows().isEmpty()) {
			add(createUnvisibileDiv("workflowsDiv"));
		}
		else {
			add(new ResourceListPanel("workflowsDiv", "Workflows", model
					.getMyExpUser().getWorkflows(),
					selectionModel.getSelectedWorkflows()));
		}
		if (model.getMyExpUser().getPacks().isEmpty()) {
			add(createUnvisibileDiv("packsDiv"));
		}
		else {
			add(new ResourceListPanel("packsDiv", "Packs", model.getMyExpUser()
					.getPacks(), selectionModel.getSelectedPacks()));
		}
	}


	/**
	 * @return
	 */
	private WebMarkupContainer createUnvisibileDiv(String id)
	{
		WebMarkupContainer div = new WebMarkupContainer(id);
		div.setVisible(false);
		return div;
	}


	@Override
	public boolean isLastStep()
	{
		return false;
	}


	@Override
	public IDynamicWizardStep next()
	{
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public boolean isComplete()
	{
		return selectionModel.isValid();
	}


	@Override
	public void applyState()
	{
		super.applyState();
		ImportModel model = (ImportModel) this.getDefaultModelObject();
		model.getResearchObjects().addAll(selectionModel.createResearchObjects());
	}

	private class ImportTypeChoiceRenderer
		implements IChoiceRenderer<ImportType>
	{

		private static final long serialVersionUID = 1558067857104879971L;


		@Override
		public Object getDisplayValue(ImportType type)
		{
			switch (type) {
				case ALL_AS_ONE:
					return "Import all selected resources as 1 Research Object";
				case EACH_SEPARATELY:
					return "Import each selected resource as a separate Research Object";
			}
			return null;
		}


		@Override
		public String getIdValue(ImportType type, int idx)
		{
			return type.toString();
		}

	}

}