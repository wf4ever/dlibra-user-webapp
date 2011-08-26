/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.scribe.model.Token;

/**
 * @author Piotr Hołubowicz
 *
 */
public class DerbyService
{

	private static final Logger log = Logger.getLogger(DerbyService.class);

	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private static final String protocol = "jdbc:derby:";

	private static final String dbName = "rosrs_users"; // the name of the database


	public static String getUsername(String openID)
	{
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(protocol + dbName + ";");
			PreparedStatement ps = conn
					.prepareStatement("select username from users where openID = ?");
			ps.setString(1, openID);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new IllegalArgumentException("OpenID not found");
			}
			return rs.getString(1);
		}
		catch (SQLException e) {
			log.error(e);
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (SQLException e) {
				log.error(e);
			}
		}
		return null;
	}


	public static String getPassword(String openID)
	{
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(protocol + dbName + ";");
			PreparedStatement ps = conn
					.prepareStatement("select password from users where openID = ?");
			ps.setString(1, openID);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				throw new IllegalArgumentException("OpenID not found");
			}
			return rs.getString(1);
		}
		catch (SQLException e) {
			log.error(e);
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (SQLException e) {
				log.error(e);
			}
		}
		return null;
	}


	public static boolean userExists(String openID)
	{
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(protocol + dbName + ";");
			PreparedStatement ps = conn
					.prepareStatement("select username from users where openID = ?");
			ps.setString(1, openID);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		}
		catch (SQLException e) {
			log.error(e);
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (SQLException e) {
				log.error(e);
			}
		}
		return false;
	}


	public static Token getAccessToken(String openID)
	{
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(protocol + dbName + ";");
			PreparedStatement ps = conn
					.prepareStatement("select username, password from users where openID = ?");
			ps.setString(1, openID);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				return null;
			}
			else {
				return DlibraService.generateAccessToken(rs.getString(1),
					rs.getString(2));
			}
		}
		catch (SQLException e) {
			log.error(e);
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (SQLException e) {
				log.error(e);
			}
		}
		return null;
	}


	public static void insertUser(String openID, String username,
			String password)
		throws SQLException
	{
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(protocol + dbName + ";");
			PreparedStatement psInsert = conn
					.prepareStatement("insert into users values (?, ?, ?)");

			psInsert.setString(1, openID);
			psInsert.setString(2, username);
			psInsert.setString(3, password);
			psInsert.executeUpdate();
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}


	public static void deleteUser(String openID)
		throws SQLException
	{
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(protocol + dbName + ";");
			PreparedStatement ps = conn
					.prepareStatement("delete from users where openID = ?");
			ps.setString(1, openID);
			ps.executeUpdate();
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}


	/**
	 * Loads the appropriate JDBC driver for this environment/framework. For
	 * example, if we are in an embedded environment, we load Derby's
	 * embedded Driver, <code>org.apache.derby.jdbc.EmbeddedDriver</code>.
	 */
	public static void loadDriver()
	{
		/*
		 *  The JDBC driver is loaded by loading its class.
		 *  If you are using JDBC 4.0 (Java SE 6) or newer, JDBC drivers may
		 *  be automatically loaded, making this code optional.
		 *
		 *  In an embedded environment, this will also start up the Derby
		 *  engine (though not any databases), since it is not already
		 *  running. In a client environment, the Derby engine is being run
		 *  by the network server framework.
		 *
		 *  In an embedded environment, any static Derby system properties
		 *  must be set before loading the driver to take effect.
		 */
		try {
			Class.forName(driver).newInstance();
			log.info("Loaded the appropriate driver");
		}
		catch (ClassNotFoundException cnfe) {
			log.error("Unable to load the JDBC driver " + driver);
			log.error("Please check your CLASSPATH.", cnfe);
		}
		catch (InstantiationException ie) {
			log.error("\nUnable to instantiate the JDBC driver " + driver, ie);
		}
		catch (IllegalAccessException iae) {
			log.error("\nNot allowed to access the JDBC driver " + driver, iae);
		}
	}


	public static void initDB(boolean recreate)
	{
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(protocol + dbName
					+ ";create=true");
			log.debug("Connected to and created database " + dbName);

			conn.setAutoCommit(true);

			/* Creating a statement object that we can use for running various
			 * SQL statements commands against the database.*/
			Statement s = conn.createStatement();
			if (recreate) {
				s.execute("drop table users");
			}

			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getTables(null, null, "users", null);
			if (!rs.next()) {

				// We create a table...
				s.execute("create table users(openID varchar(200), username varchar(30), password varchar(30))");
				log.debug("Created table users");
			}
		}
		catch (SQLException e) {
			log.error("Error when initializing the Derby database", e);
		}

		try {
			if (conn != null) {
				conn.close();
			}
		}
		catch (SQLException e) {
			log.error("Error when closing connection", e);
		}

	}


	public static void shutdownDB()
	{
		try {
			DriverManager.getConnection(protocol + ";shutdown=true");
			log.debug("Database shut down");
		}
		catch (SQLException e) {
			// It is expected
			log.debug("Exception when shutting down derby: " + e.getMessage());
		}
	}

}
