package pl.psnc.dl.wf4ever.webapp.pages;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.OddEvenListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.webapp.model.AccessToken;
import pl.psnc.dl.wf4ever.webapp.model.OpenIdUser;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;
import pl.psnc.dl.wf4ever.webapp.services.OAuthException;

/**
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class AccessTokensPage extends TemplatePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(AccessTokensPage.class);

	/**
	 * Default Constructor
	 */
	public AccessTokensPage() {
		this(new PageParameters());
	}

	@SuppressWarnings("serial")
	public AccessTokensPage(PageParameters pageParameters) {
		super(pageParameters);
		if (willBeRedirected)
			return;

		final OpenIdUser user = getOpenIdUserModel();
		
		List<AccessToken> accessTokens = null;
		try {
			accessTokens = DlibraService.getAccessTokens(user.getOpenId());
		} catch (OAuthException e) {
			log.error(e.getMessage());
			error(e.getMessage());
		} catch (JAXBException e) {
			log.error(e.getMessage());
			error(e.getMessage());
		}

		Form<?> form = new Form<Void>("form");
		content.add(form);
		ListView<AccessToken> list = new ListView<AccessToken>(
				"tokensListView", accessTokens) {

			@Override
			protected ListItem<AccessToken> newItem(int index,
					IModel<AccessToken> itemModel) {
				return new OddEvenListItem<AccessToken>(index, itemModel);
			};

			protected void populateItem(ListItem<AccessToken> item) {
				AccessToken token = (AccessToken) item
						.getModelObject();
				item.add(new Label("clientName", token.getClient().getName()));
				item.add(new Label("created", "tbd"));
				item.add(new Label("lastUsed", "tbd"));
			}
		};
		list.setReuseItems(true);
		form.add(list);
	}

}
