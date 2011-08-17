/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import pl.psnc.dl.wf4ever.webapp.services.DerbyService;

/**
 * @author piotrhol
 *
 */
public class ContextListener
	implements ServletContextListener
{

	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		DerbyService.loadDriver();
		DerbyService.initDB(false);
	}


	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		DerbyService.shutdownDB();
	}

}
