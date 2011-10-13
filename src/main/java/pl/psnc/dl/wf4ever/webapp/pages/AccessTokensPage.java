package pl.psnc.dl.wf4ever.webapp.pages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.OddEvenListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.webapp.model.AccessToken;
import pl.psnc.dl.wf4ever.webapp.model.OAuthClient;
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

	private OAuthClient oobClient;
	
	private ListView<AccessToken> list;
	
	private List<AccessToken> accessTokens = null;

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
		
		try {
			accessTokens = DlibraService.getAccessTokens(user.getOpenId());
		} catch (OAuthException e) {
			log.error(e.getMessage());
			error(e.getMessage());
		} catch (JAXBException e) {
			log.error(e.getMessage());
			error(e.getMessage());
		}
		
		final Form<?> form = new Form<Void>("form");
		content.add(form);
		list = new ListView<AccessToken>(
				"tokensListView", new PropertyModel<List<AccessToken>>(this, "accessTokens")) {

			@Override
			protected ListItem<AccessToken> newItem(int index,
					IModel<AccessToken> itemModel) {
				return new OddEvenListItem<AccessToken>(index, itemModel);
			};

			protected void populateItem(ListItem<AccessToken> item) {
				final AccessToken token = (AccessToken) item.getModelObject();
				item.add(new Label("clientName", token.getClient().getName()));
				item.add(new Label("created",
						token.getCreated() != null ? token.getCreated()
								.toString() : "--"));
				item.add(new Label("lastUsed",
						token.getLastUsed() != null ? token.getLastUsed()
								.toString() : "--"));
				item.add(new AjaxButton("revoke", form) {
					
					@Override
					protected void onError(AjaxRequestTarget arg0, Form<?> arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
						try {
							DlibraService.deleteAccessToken(token.getToken());
							accessTokens.remove(token);
							target.add(form);
						} catch (Exception e) {
							log.error(e.getMessage());
							error(e.getMessage());
						}
					}
				});
			}
		};
		list.setReuseItems(true);
		list.setOutputMarkupId(true);
		form.add(list);

		List<OAuthClient> oobClients = null;
		try {
			oobClients = DlibraService.getClients();
			Iterator<OAuthClient> i = oobClients.iterator();
			while (i.hasNext()) {
				OAuthClient client = i.next();
				if (!OAuthClient.OOB.equals(client.getRedirectionURI())) {
					i.remove();
				}
			}
		} catch (OAuthException e) {
			log.error(e.getMessage());
			error(e.getMessage());
			oobClients = new ArrayList<OAuthClient>();
		} catch (JAXBException e) {
			log.error(e.getMessage());
			error(e.getMessage());
			oobClients = new ArrayList<OAuthClient>();
		}

		Form<?> form2 = new Form<Void>("form2") {
			@Override
			protected void onSubmit() {
				super.onSubmit();
				PageParameters params = new PageParameters();
				params.add("clientId", oobClient.getClientId());
				params.add("clientName", oobClient.getName());
				goToPage(OOBAccessTokenPage.class, params);
			}
		};
		content.add(form2);
		final DropDownChoice<OAuthClient> oobClientList = new DropDownChoice<OAuthClient>(
				"oobClients",
				new PropertyModel<OAuthClient>(this, "oobClient"), oobClients);
		form2.add(oobClientList);

		if (oobClients.isEmpty()) {
			oobClients.add(new OAuthClient(null,
					"No OOB applications available", null));
			oobClientList.setEnabled(false);
		}
		this.oobClient = oobClients.get(0);
	}

	/**
	 * @return the oobClient
	 */
	public OAuthClient getOobClient() {
		return oobClient;
	}

	/**
	 * @param oobClient
	 *            the oobClient to set
	 */
	public void setOobClient(OAuthClient oobClient) {
		this.oobClient = oobClient;
	}

	/**
	 * @return the accessTokens
	 */
	public List<AccessToken> getAccessTokens() {
		return accessTokens;
	}

	/**
	 * @param accessTokens the accessTokens to set
	 */
	public void setAccessTokens(List<AccessToken> accessTokens) {
		this.accessTokens = accessTokens;
	}
}
