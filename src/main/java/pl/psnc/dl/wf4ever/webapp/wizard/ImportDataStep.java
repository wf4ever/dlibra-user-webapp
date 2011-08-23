/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.wizard;

import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.model.Model;

import pl.psnc.dl.wf4ever.webapp.model.ImportModel;


/**
 * @author Piotr Hołubowicz
 *
 */
public class ImportDataStep
	extends DynamicWizardStep
{

	private static final long serialVersionUID = -2632389547400514998L;


	public ImportDataStep(IDynamicWizardStep previousStep,
			ImportModel model)
	{
		super(previousStep, "Import data", "", new Model<ImportModel>(model));
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
		return new SummaryStep(this,
			(ImportModel) this.getDefaultModelObject());
	}

}
