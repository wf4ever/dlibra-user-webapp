/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import pl.psnc.dl.wf4ever.webapp.services.HibernateService;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ContextListener
	implements ServletContextListener
{

	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		HibernateService.getSessionFactory();
	}


	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
	}

}
