/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.MyExpFile;
import pl.psnc.dl.wf4ever.webapp.model.NewResearchObjectModel;

/**
 * @author Piotr Hołubowicz
 *
 */
public class MyExpImportService
{

	private static final Logger log = Logger
			.getLogger(MyExpImportService.class);

	private static final OAuthService service = MyExpApi.getOAuthService();


	public static void startImport(ImportModel model, Token myExpAccessToken,
			DlibraUserModel dLibraUser)
	{
		model.setImportActive(true);
		for (NewResearchObjectModel ro : model.getResearchObjects()) {
			try {
				importRO(model, ro, myExpAccessToken, dLibraUser);
			}
			catch (Exception e) {
				log.error("Error during import", e);
				model.setImportStatus(e.getMessage());
			}
		}
		model.setImportStatus("Finished");
		model.setImportActive(false);
	}


	/**
	 * @param model
	 * @param dLibraUser
	 * @param ro
	 * @throws Exception 
	 */
	private static void importRO(ImportModel model, NewResearchObjectModel ro,
			Token token, DlibraUserModel dLibraUser)
		throws Exception
	{
		model.setImportStatus(String.format(
			"Creating a Research Object \"%s\"", ro.getName()));
		if (!DlibraService.createResearchObjectAndVersion(ro.getName(),
			dLibraUser, model.isMergeROs())) {
			model.setImportStatus("Merged with an existing Research Object");
		}
		for (MyExpFile file : ro.getFiles()) {
			OAuthRequest request = new OAuthRequest(Verb.GET, createUrl(file));
			service.signRequest(token, request);
			Response response = request.send();
			MyExpFile fileWithContent = createMyExpFile(response.getBody());
			model.setImportStatus(String.format("Importing file \"%s\"",
				fileWithContent.getFilename()));

			DlibraService.sendResource(fileWithContent.getFilename(),
				ro.getName(), fileWithContent.getContentDecoded(), dLibraUser);
		}
	}


	private static String createUrl(MyExpFile file)
	{
		return file.getUri() + "&elements=filename,content";
	}


	private static MyExpFile createMyExpFile(String xml)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(MyExpFile.class);

		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return (MyExpFile) u.unmarshal(new StreamSource(new StringReader(xmlStr
				.toString())));
	}

}
