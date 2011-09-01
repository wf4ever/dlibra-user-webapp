package pl.psnc.dl.wf4ever.webapp.wizard;

import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;

import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.pages.DlibraRegistrationPage;

public class ImportWizard
	extends Wizard
{

	private static final long serialVersionUID = -8520850154339581229L;


	@SuppressWarnings("serial")
	public ImportWizard(String id, ImportModel model)
	{
		super(id, false);
		DynamicWizardModel wizardModel = new DynamicWizardModel(
				new StartImportStep(model)) {

			@Override
			public void finish()
			{
				String home = urlFor(DlibraRegistrationPage.class, null).toString();
				getRequestCycle().scheduleRequestHandlerAfterCurrent(
					new RedirectRequestHandler(home));
			}
		};
		wizardModel.setCancelVisible(false);
		init(wizardModel);
	}

}