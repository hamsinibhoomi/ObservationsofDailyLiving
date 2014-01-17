package org.ncsu.cs.edu.test;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.ncsu.cs.edu.utilities.DBUtility;
 
public class ConnectionTest {
 
	public static void main(String[] argv) {
 
		System.out.println("-------- Oracle JDBC Connection Testing ------");
 
		try {
 
			Class.forName("oracle.jdbc.driver.OracleDriver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return;
 
		}
 
		System.out.println("Oracle JDBC Driver Registered!");
 
		Connection connection = null;
 
		try {
 
			connection = DriverManager.getConnection(
					 "jdbc:oracle:thin:@localhost:1521:xe", "dbms", "dbms");
			Statement stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM observations");
			int count = 0;
			while (rs.next()) {
				count++;
			}
			
			System.out.println("count->"+count);
			
			// DBUtility.getConnection();
			// DBUtility.getTypeId("diet", "1");
			
			stmt = connection.createStatement();
			String name = "PAIN";
			String qrr = "select column_name from all_tab_columns where owner = 'DBMS' and table_name = '"+name+"'";
			System.out.println(qrr);
			rs = stmt.executeQuery(qrr);
			while (rs.next()) {
				System.out.println(rs.getString("column_name"));
			}
		} catch (SQLException e) {
 
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
 
		}
 
		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}
 
}