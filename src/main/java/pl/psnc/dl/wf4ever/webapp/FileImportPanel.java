/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp;

import java.util.Arrays;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.webapp.model.FileImportModel;
import pl.psnc.dl.wf4ever.webapp.model.FileImportModel.ImportType;

/**
 * @author Piotr Hołubowicz
 *
 */
public class FileImportPanel
	extends Panel
{

	private static final long serialVersionUID = -3775797988389365540L;


	public FileImportPanel(String id, FileImportModel model)
	{
		super(id);

		RadioChoice<ImportType> importType = new RadioChoice<ImportType>(
				"fileImportType", new PropertyModel<ImportType>(model,
						"importType"), Arrays.asList(ImportType.values()),
				new ImportTypeChoiceRenderer());

		add(importType);
	}

	private class ImportTypeChoiceRenderer
		implements IChoiceRenderer<ImportType>
	{

		private static final long serialVersionUID = 1558067857104879971L;


		@Override
		public Object getDisplayValue(ImportType type)
		{
			switch (type) {
				case ALL_AS_1_RO:
					return "Import all files, together with workflows and packs";
				case ALL_AS_MANY_ROS:
					return "Import all files, as separate Research Objects";
				case NOTHING:
					return "Don't import anything";
				case CUSTOM:
					return "Custom import";
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
