/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel.ImportStatus;
import pl.psnc.dl.wf4ever.webapp.model.ResearchObject;
import pl.psnc.dl.wf4ever.webapp.model.myexp.InternalPackItem;
import pl.psnc.dl.wf4ever.webapp.model.myexp.InternalPackItemHeader;
import pl.psnc.dl.wf4ever.webapp.model.myexp.Pack;
import pl.psnc.dl.wf4ever.webapp.model.myexp.PackHeader;
import pl.psnc.dl.wf4ever.webapp.model.myexp.Resource;
import pl.psnc.dl.wf4ever.webapp.model.myexp.ResourceHeader;
import pl.psnc.dl.wf4ever.webapp.model.myexp.SimpleResource;
import pl.psnc.dl.wf4ever.webapp.model.myexp.SimpleResourceHeader;

/**
 * @author Piotr Hołubowicz
 *
 */
public class MyExpImportService
{

	public static void startImport(ImportModel model, Token myExpAccessToken,
			DlibraUser dLibraUser)
	{
		new ImportThread(model, myExpAccessToken, dLibraUser).start();
	}

	private static class ImportThread
		extends Thread
	{

		private static final Logger log = Logger.getLogger(ImportThread.class);

		private final OAuthService service = MyExpApi.getOAuthService();

		private ImportModel model;

		private Token token;

		private DlibraUser user;


		public ImportThread(ImportModel importModel, Token myExpAccessToken,
				DlibraUser dLibraUser)
		{
			super();
			model = importModel;
			token = myExpAccessToken;
			user = dLibraUser;
		}


		public void run()
		{
			model.setStatus(ImportStatus.RUNNING);
			for (ResearchObject ro : model.getResearchObjects()) {
				try {
					importRO(ro);
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
		private void importRO(ResearchObject ro)
			throws Exception
		{
			createRO(ro);
			importSimpleResources(ro.getFiles(), ro.getName());
			importSimpleResources(ro.getWorkflows(), ro.getName());
			importPacks(ro);
		}


		/**
		 * @param model
		 * @param ro
		 * @param token
		 * @param dLibraUser
		 * @throws JAXBException
		 * @throws Exception
		 */
		private void importSimpleResources(
				List< ? extends SimpleResourceHeader> resourceHeaders,
				String roName)
			throws JAXBException, Exception
		{
			for (SimpleResourceHeader header : resourceHeaders) {
				importSimpleResource(header, roName, "",
					header.getResourceClass());
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
		private void importPacks(ResearchObject ro)
			throws JAXBException, Exception
		{
			for (PackHeader packHeader : ro.getPacks()) {
				Response response = OAuthHelpService.sendRequest(service,
					Verb.GET, packHeader.getResourceUrl(), token);
				Pack pack = (Pack) createMyExpResource(response.getBody(),
					Pack.class);
				model.setMessage(String.format("Importing pack \"%d\"",
					pack.getId()));

				for (InternalPackItemHeader packItemHeader : pack
						.getResources()) {
					importInternalPackItem(ro, pack, packItemHeader);
				}
			}
		}


		/**
		 * @param model
		 * @param ro
		 * @param token
		 * @param user
		 * @param pack
		 * @param r
		 * @throws JAXBException
		 * @throws Exception
		 */
		private void importInternalPackItem(ResearchObject ro, Pack pack,
				InternalPackItemHeader packItemHeader)
			throws JAXBException, Exception
		{
			Response response = OAuthHelpService.sendRequest(service, Verb.GET,
				packItemHeader.getResourceUrl(), token);
			InternalPackItem internalItem = (InternalPackItem) createMyExpResource(
				response.getBody(), InternalPackItem.class);
			SimpleResourceHeader resourceHeader = internalItem.getItem();
			importSimpleResource(resourceHeader, ro.getName(), pack.getId()
					+ "/", resourceHeader.getResourceClass());
		}


		private void importSimpleResource(ResourceHeader res, String roName,
				String path, Class< ? extends SimpleResource> resourceClass)
			throws Exception
		{
			Response response = OAuthHelpService.sendRequest(service, Verb.GET,
				res.getResourceUrl(), token);
			SimpleResource r = (SimpleResource) createMyExpResource(
				response.getBody(), resourceClass);
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
		private void createRO(ResearchObject ro)
			throws Exception
		{
			model.setMessage(String.format("Creating a Research Object \"%s\"",
				ro.getName()));
			if (!DlibraService.createResearchObjectAndVersion(ro.getName(),
				user, model.isMergeROs())) {
				model.setMessage("Merged with an existing Research Object");
			}
		}


		private static Object createMyExpResource(String xml,
				Class< ? extends Resource> resourceClass)
			throws JAXBException
		{
			JAXBContext jc = JAXBContext.newInstance(resourceClass);
			Unmarshaller u = jc.createUnmarshaller();
			StringBuffer xmlStr = new StringBuffer(xml);
			return u.unmarshal(new StreamSource(new StringReader(xmlStr
					.toString())));
		}

	}
}