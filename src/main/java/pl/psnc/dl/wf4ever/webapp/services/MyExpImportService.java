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

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel.ImportStatus;
import pl.psnc.dl.wf4ever.webapp.model.MyExpFile;
import pl.psnc.dl.wf4ever.webapp.model.MyExpPack;
import pl.psnc.dl.wf4ever.webapp.model.MyExpResource;
import pl.psnc.dl.wf4ever.webapp.model.MyExpSimpleResource;
import pl.psnc.dl.wf4ever.webapp.model.MyExpWorkflow;
import pl.psnc.dl.wf4ever.webapp.model.ResearchObject;

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
			DlibraUser dLibraUser)
	{
		model.setStatus(ImportStatus.RUNNING);
		for (ResearchObject ro : model.getResearchObjects()) {
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
	private static void importRO(ImportModel model, ResearchObject ro,
			Token token, DlibraUser dLibraUser)
		throws Exception
	{
		createRO(model, ro, dLibraUser);
		importFiles(model, ro, token, dLibraUser);
		importWorkflows(model, ro, token, dLibraUser);
		importPacks(model, ro, token, dLibraUser);
	}


	/**
	 * @param model
	 * @param ro
	 * @param token
	 * @param dLibraUser
	 * @throws JAXBException
	 * @throws Exception
	 */
	private static void importFiles(ImportModel model, ResearchObject ro,
			Token token, DlibraUser user)
		throws JAXBException, Exception
	{
		for (MyExpFile file : ro.getFiles()) {
			importSimpleResource(model, file, ro.getName(), token, user);
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
	private static void importWorkflows(ImportModel model, ResearchObject ro,
			Token token, DlibraUser user)
		throws JAXBException, Exception
	{
		for (MyExpWorkflow workflow : ro.getWorkflows()) {
			importSimpleResource(model, workflow, ro.getName(), token, user);
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
	private static void importPacks(ImportModel model, ResearchObject ro,
			Token token, DlibraUser user)
		throws JAXBException, Exception
	{
		for (MyExpPack pack : ro.getPacks()) {
			OAuthRequest request = new OAuthRequest(Verb.GET, pack.getFullUrl());
			service.signRequest(token, request);
			Response response = request.send();
			MyExpPack p = (MyExpPack) createMyExpResource(response.getBody(),
				MyExpPack.class);
			model.setMessage(String.format("Importing pack \"%d\"", p.getId()));

			for (MyExpSimpleResource r : p.getResources()) {
				importSimpleResource(model, r, ro.getName(), token, user,
					p.getId() + "/");
			}
		}
	}


	private static void importSimpleResource(ImportModel model,
			MyExpSimpleResource res, String roName, Token token, DlibraUser user)
		throws Exception
	{
		importSimpleResource(model, res, roName, token, user, "");
	}


	private static void importSimpleResource(ImportModel model,
			MyExpSimpleResource res, String roName, Token token,
			DlibraUser user, String path)
		throws Exception
	{
		OAuthRequest request = new OAuthRequest(Verb.GET, res.getFullUrl());
		service.signRequest(token, request);
		Response response = request.send();
		MyExpSimpleResource r = (MyExpSimpleResource) createMyExpResource(
			response.getBody(), res.getClass());
		model.setMessage(String.format("Importing \"%s\"", r.getFilename()));

		DlibraService.sendResource(path + r.getFilename(), roName,
			r.getContentDecoded(), r.getContentType(), user);

	}


	/**
	 * @param model
	 * @param ro
	 * @param dLibraUser
	 * @throws Exception
	 */
	private static void createRO(ImportModel model, ResearchObject ro,
			DlibraUser dLibraUser)
		throws Exception
	{
		model.setMessage(String.format("Creating a Research Object \"%s\"",
			ro.getName()));
		if (!DlibraService.createResearchObjectAndVersion(ro.getName(),
			dLibraUser, model.isMergeROs())) {
			model.setMessage("Merged with an existing Research Object");
		}
	}


	private static Object createMyExpResource(String xml,
			Class< ? extends MyExpResource> resourceClass)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(resourceClass);

		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return u.unmarshal(new StreamSource(new StringReader(xmlStr.toString())));
	}

}
