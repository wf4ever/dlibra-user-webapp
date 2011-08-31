/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.wizard;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.webapp.model.myexp.ResourceHeader;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ResourceListPanel
	extends Panel
{

	private static final long serialVersionUID = -3775797988389365540L;

	private String resourceName;


	@SuppressWarnings({ "serial", "deprecation"})
	public ResourceListPanel(String id, String name,
			List< ? extends ResourceHeader> resources,
			List< ? extends ResourceHeader> selectedResources)
	{
		super(id);
		this.resourceName = name;
		final VisibilityModel visModel = new VisibilityModel(true);

		add(new Label("resourceName", resourceName));

		final WebMarkupContainer resourceList = new WebMarkupContainer(
				"resourceList");
		resourceList.setOutputMarkupId(true);
		resourceList
				.add(new AttributeModifier("style", true,
						new PropertyModel<String>(visModel,
								"resourceListDisplayStyle")));
		add(resourceList);

		Form< ? > form = new Form<Void>("form");
		resourceList.add(form);
		@SuppressWarnings("unchecked")
		CheckGroup<ResourceHeader> group = new CheckGroup<ResourceHeader>(
				"group", (List<ResourceHeader>) selectedResources);
		form.add(group);
		group.add(new CheckGroupSelector("groupselector"));
		ListView<ResourceHeader> list = new ListView<ResourceHeader>(
				"resourceListView", resources) {

			protected void populateItem(ListItem<ResourceHeader> item)
			{
				ResourceHeader resource = (ResourceHeader) item
						.getModelObject();
				item.add(new Check<ResourceHeader>("checkbox", item
						.getModel()));
				ExternalLink link = new ExternalLink("link",
						resource.getResource());
				link.add(new Label("title", resource.getTitle()));
				item.add(link);
			}
		};
		list.setReuseItems(true);
		group.add(list);

		AjaxFallbackLink<String> link = new AjaxFallbackLink<String>(
				"resourceListLink") {

			@Override
			public void onClick(AjaxRequestTarget target)
			{
				visModel.setResourceListVisible(!visModel
						.isResourceListVisible());
				target.add(this);
				target.add(resourceList);
			}
		};
		link.add(new Label("resourceListLinkLabel", new PropertyModel<String>(
				visModel, "hideShowResourceListLabel")));
		add(link);
	}

	class VisibilityModel
		implements Serializable
	{

		private static final long serialVersionUID = 6277636176664446588L;

		private boolean resourceListVisible;


		public VisibilityModel(boolean visible)
		{
			this.resourceListVisible = visible;
		}


		/**
		 * @return the resourceListVisible
		 */
		public boolean isResourceListVisible()
		{
			return resourceListVisible;
		}


		/**
		 * @param resourceListVisible the resourceListVisible to set
		 */
		public void setResourceListVisible(boolean resourceListVisible)
		{
			this.resourceListVisible = resourceListVisible;
		}


		public String getResourceListDisplayStyle()
		{
			if (isResourceListVisible()) {
				return "display:block";
			}
			else {
				return "display:none";
			}
		}


		public String getHideShowResourceListLabel()
		{
			if (isResourceListVisible()) {
				return "Hide " + resourceName.toLowerCase();
			}
			else {
				return "Show " + resourceName.toLowerCase();
			}
		}

	}

}
