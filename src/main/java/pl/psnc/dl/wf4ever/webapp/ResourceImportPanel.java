/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp;

import java.util.Arrays;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel.ImportType;
import pl.psnc.dl.wf4ever.webapp.model.MyExpResource;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ResourceImportPanel
	extends Panel
{

	private static final long serialVersionUID = -3775797988389365540L;


	@SuppressWarnings({ "serial", "deprecation"})
	public ResourceImportPanel(String id, final ImportModel model)
	{
		super(id);

		add(new Label("title", model.getResourceName()));
		add(new Label("msgResourceName", model.getResourceName().toLowerCase()));

		RadioChoice<ImportType> importType = new RadioChoice<ImportType>(
				"importType",
				new PropertyModel<ImportType>(model, "importType"),
				Arrays.asList(ImportType.values()),
				new ImportTypeChoiceRenderer(model.getResourceName()));

		add(importType);

		final WebMarkupContainer fileList = new WebMarkupContainer(
				"resourceList");
		fileList.setOutputMarkupId(true);
		fileList.add(new AttributeModifier("style", true,
				new PropertyModel<String>(model, "resourceListDisplayStyle")));
		add(fileList);

		fileList.add(new ListView<MyExpResource>("resourceListView", model
				.getResources()) {

			protected void populateItem(ListItem<MyExpResource> item)
			{
				MyExpResource file = (MyExpResource) item.getModelObject();
				item.add(new Label("title", file.getTitle()));
				item.add(new Label("uri", file.getUri()));
			}
		});

		AjaxFallbackLink<String> link = new AjaxFallbackLink<String>(
				"resourceListLink") {

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				model.setResourceListVisible(!model.isResourceListVisible());
				target.add(this);
				target.add(fileList);
			}
		};
		link.add(new Label("resourceListLinkLabel", new PropertyModel<String>(
				model, "hideShowResourceListLabel")));
		add(link);
	}

	private class ImportTypeChoiceRenderer
		implements IChoiceRenderer<ImportType>
	{

		private static final long serialVersionUID = 1558067857104879971L;

		private String resourceName;


		public ImportTypeChoiceRenderer(String resourceName)
		{
			super();
			this.resourceName = resourceName.toLowerCase();
		}


		@Override
		public Object getDisplayValue(ImportType type)
		{
			switch (type) {
				case ALL_AS_1_RO:
					return String.format(
						"Import all %s, together with other resources",
						resourceName);
				case ALL_AS_MANY_ROS:
					return String.format(
						"Import all %s as separate Research Objects",
						resourceName);
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
