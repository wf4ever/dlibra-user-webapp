/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;

/**
 * @author Piotr Hołubowicz
 *
 */
public class HibernateService
{

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(HibernateService.class);

	private static final SessionFactory sessionFactory = buildSessionFactory();


	private static SessionFactory buildSessionFactory()
	{
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			return new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}


	public static SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}


	public static boolean userExists(String openId)
	{
		return loadUser(openId) != null;
	}


	public static void storeUser(DlibraUser user)
	{
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();

		session.saveOrUpdate(user);

		session.getTransaction().commit();
	}


	public static void deleteUser(DlibraUser user)
	{
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();

		session.delete(user);

		session.getTransaction().commit();
	}


	public static DlibraUser loadUser(String openId)
	{
		Session session = getSessionFactory().getCurrentSession();
		session.beginTransaction();

		DlibraUser user = (DlibraUser) session.get(DlibraUser.class, openId);

		session.getTransaction().commit();

		return user;
	}
}
