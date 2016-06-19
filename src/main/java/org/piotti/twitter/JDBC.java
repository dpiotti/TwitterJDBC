package org.piotti.twitter;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;

/** returns database connection using JDBC
 *
 * @author Daniel Piotti
 */

public class JDBC {

	public static Connection DatabaseConnect() throws Exception {
		
		FileInputStream fis = null;
		Properties props = new Properties();
		
		System.out.println("Testing JDBC Connection...");

		try {
			fis = new FileInputStream("db.properties");
            props.load(fis);

			Class.forName(props.getProperty("DRIVER_CLASS"));

		} catch (ClassNotFoundException e) {

			System.out.println("Missing PostgreSQL JDBC Driver!");
			e.printStackTrace();
			throw e;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;

		try {

			connection = DriverManager.getConnection(props.getProperty("URL"), 
					props.getProperty("USERNAME"),
					props.getProperty("PASSWORD"));
	
		} catch (SQLException e) {

			
			e.printStackTrace();
			throw e;
		}
				
		return connection;
	}
}
