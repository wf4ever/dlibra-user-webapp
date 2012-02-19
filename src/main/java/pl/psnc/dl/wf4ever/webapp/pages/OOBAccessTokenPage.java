package pl.psnc.dl.wf4ever.webapp.pages;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.webapp.model.OpenIdUser;
import pl.psnc.dl.wf4ever.webapp.services.DlibraService;

/**
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class OOBAccessTokenPage extends TemplatePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger
			.getLogger(OOBAccessTokenPage.class);

	/**
	 * Default Constructor
	 */
	public OOBAccessTokenPage() {
		this(new PageParameters());
	}

	public OOBAccessTokenPage(PageParameters pageParameters) {
		super(pageParameters);
		if (willBeRedirected)
			return;

		OpenIdUser user = getOpenIdUserModel();
		String clientId = pageParameters.get("clientId").toString();
		String clientName = pageParameters.get("clientName").toString();
		String token;

		try {
			token = DlibraService.createAccessToken(user.getOpenId(), clientId);
		} catch (Exception e) {
			log.error(e.getMessage());
			error(e.getMessage());
			token = "--";
		}

		content.add(new Label("token", token));
		content.add(new Label("clientName", clientName));
		content.add(new BookmarkablePageLink<Void>("back",
				AccessTokensPage.class));
	}

}
