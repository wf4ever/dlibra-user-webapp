/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.wizard;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;
import org.scribe.model.Token;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel.ImportStatus;
import pl.psnc.dl.wf4ever.webapp.pages.TemplatePage;
import pl.psnc.dl.wf4ever.webapp.services.MyExpImportService;
import pl.psnc.dl.wf4ever.webapp.utils.Constants;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ImportDataStep
	extends AbstractStep
{

	private static final long serialVersionUID = -2632389547400514998L;

	private static final double INTERVAL = 500;


	@SuppressWarnings("serial")
	public ImportDataStep(IDynamicWizardStep previousStep,
			final ImportModel model)
	{
		super(previousStep, "Import data", model);
		setOutputMarkupId(true);
		final TextArea<String> importStatus = new TextArea<String>("messages",
				new PropertyModel<String>(model, "messages"));
		importStatus.setOutputMarkupId(true);
		add(importStatus);

		final AjaxSelfUpdatingTimerBehavior updater = new AjaxSelfUpdatingTimerBehavior(
				Duration.milliseconds(INTERVAL)) {

			@Override
			protected void onPostProcessTarget(AjaxRequestTarget target)
			{
				super.onPostProcessTarget(target);
				if (model.getStatus() == ImportStatus.FINISHED) {
					stop();
					importStatus.remove(this);
					getSession().info("Import complete.");
					getRequestCycle().setResponsePage(getPage());
				}
			}
		};
		add(new AjaxButton("go") {

			@Override
			protected void onError(AjaxRequestTarget target, Form< ? > form)
			{
			}


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				if (model.getStatus() == ImportStatus.NOT_STARTED) {
					Token accessToken = ((TemplatePage) getPage())
							.getDlibraUserModel().getMyExpAccessToken();
					DlibraUser userModel = (DlibraUser) getSession()
							.getAttribute(Constants.SESSION_USER_MODEL);
					MyExpImportService.startImport(model, accessToken,
						userModel);
					importStatus.add(updater);
					target.add(importStatus);

					this.setEnabled(false);
					target.add(this);
					getRequestCycle().setResponsePage(getPage());
				}
			}
		}).setEnabled(model.getStatus() == ImportStatus.NOT_STARTED)
				.setOutputMarkupId(true);
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
		return new SummaryStep(this, (ImportModel) this.getDefaultModelObject());
	}
	
	@Override
	public boolean isPreviousAvailable()
	{
		ImportModel model = (ImportModel) getDefaultModelObject();
		return model.getStatus() == ImportStatus.NOT_STARTED;
	}

	@Override
	public boolean isNextAvailable()
	{
		ImportModel model = (ImportModel) getDefaultModelObject();
		return model.getStatus() == ImportStatus.FINISHED;
	}

}
