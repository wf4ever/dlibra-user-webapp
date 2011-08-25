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
import pl.psnc.dl.wf4ever.webapp.model.ImportModel.ImportStatus;
import pl.psnc.dl.wf4ever.webapp.model.MyExpFile;
import pl.psnc.dl.wf4ever.webapp.model.MyExpWorkflow;
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
		model.setStatus(ImportStatus.RUNNING);
		for (NewResearchObjectModel ro : model.getResearchObjects()) {
			try {
				importRO(model, ro, myExpAccessToken, dLibraUser);
			}
			catch (Exception e) {
				log.error("Error during import", e);
				model.setMessage(e.getMessage());
			}
		}
		model.setMessage("Finished");
		model.setStatus(ImportStatus.FINISHED);
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
		createRO(model, ro, dLibraUser);
		importFiles(model, ro, token, dLibraUser);
		importWorkflows(model, ro, token, dLibraUser);
	}


	/**
	 * @param model
	 * @param ro
	 * @param token
	 * @param dLibraUser
	 * @throws JAXBException
	 * @throws Exception
	 */
	private static void importFiles(ImportModel model,
			NewResearchObjectModel ro, Token token, DlibraUserModel dLibraUser)
		throws JAXBException, Exception
	{
		for (MyExpFile file : ro.getFiles()) {
			OAuthRequest request = new OAuthRequest(Verb.GET, createUrl(file));
			service.signRequest(token, request);
			Response response = request.send();
			MyExpFile f = createMyExpFile(response.getBody());
			model.setMessage(String.format("Importing file \"%s\"",
				f.getFilename()));

			DlibraService.sendResource(f.getFilename(), ro.getName(),
				f.getContentDecoded(), f.getContentType(), dLibraUser);
		}
	}


	/**
	 * @param model
	 * @param ro
	 * @param token
	 * @param dLibraUser
	 * @throws JAXBException
	 * @throws Exception
	 */
	private static void importWorkflows(ImportModel model,
			NewResearchObjectModel ro, Token token, DlibraUserModel dLibraUser)
		throws JAXBException, Exception
	{
		for (MyExpWorkflow workflow : ro.getWorkflows()) {
			OAuthRequest request = new OAuthRequest(Verb.GET,
					createUrl(workflow));
			service.signRequest(token, request);
			Response response = request.send();
			MyExpWorkflow w = createMyExpWorkflow(response.getBody());
			model.setMessage(String.format("Importing workflow \"%s\"",
				w.getFilename()));

			DlibraService.sendResource(w.getFilename(), ro.getName(),
				w.getContentDecoded(), w.getContentType(), dLibraUser);
		}
	}


	/**
	 * @param model
	 * @param ro
	 * @param dLibraUser
	 * @throws Exception
	 */
	private static void createRO(ImportModel model, NewResearchObjectModel ro,
			DlibraUserModel dLibraUser)
		throws Exception
	{
		model.setMessage(String.format("Creating a Research Object \"%s\"",
			ro.getName()));
		if (!DlibraService.createResearchObjectAndVersion(ro.getName(),
			dLibraUser, model.isMergeROs())) {
			model.setMessage("Merged with an existing Research Object");
		}
	}


	private static String createUrl(MyExpFile file)
	{
		return file.getUri() + "&elements=filename,content,content-type";
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


	private static String createUrl(MyExpWorkflow workflow)
	{
		return workflow.getUri() + "&elements=content,content-uri,content-type";
	}


	private static MyExpWorkflow createMyExpWorkflow(String xml)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(MyExpWorkflow.class);

		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return (MyExpWorkflow) u.unmarshal(new StreamSource(new StringReader(
				xmlStr.toString())));
	}

}
