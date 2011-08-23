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
public class SummaryStep
	extends DynamicWizardStep
{

	private static final long serialVersionUID = -4003286657493791544L;


	public SummaryStep(IDynamicWizardStep previousStep,
			ImportModel model)
	{
		super(previousStep, "Summary", "", new Model<ImportModel>(model));
	}


	/* (non-Javadoc)
	 * @see org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep#isLastStep()
	 */
	@Override
	public boolean isLastStep()
	{
		return true;
	}


	/* (non-Javadoc)
	 * @see org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep#next()
	 */
	@Override
	public IDynamicWizardStep next()
	{
		return null;
	}

}
