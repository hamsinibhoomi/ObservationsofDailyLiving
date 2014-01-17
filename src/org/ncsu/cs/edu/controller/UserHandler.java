package org.ncsu.cs.edu.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.ncsu.cs.edu.utilities.DBUtility;
import org.ncsu.cs.edu.utilities.DisplayUtility;

public class UserHandler {
	
	public static void createUser(BufferedReader reader) throws SQLException{
		DisplayUtility.printInNewLines(new String[]{"Which type of user do you want"
				+ "to create? Enter","1. Patient","2. Physician","3. Back"});
		
		try {
			while (true) {
				String line = reader.readLine();
				boolean canExit = false;
				
				switch (line) {
				case "1":
					System.out.println("Creating Patient");
					createPatient(reader);
					break;
				case "2":
					System.out.println("Creating Physician");
					createPhysician(reader);
					break;
				case "3":
					System.out.println("Back");
					Controller controller = new Controller();
					controller.inputHandlerMain();
					break;
				default:
					System.out.println("Please enter a valid input");
					break;
				}
				
				if(canExit)
					break;
			}
		} catch (IOException e) {
			System.err.println(e);
		}

	}

	private static void createPhysician(BufferedReader reader) throws IOException, SQLException {
		
		Connection connection = DBUtility.getConnection(); 

		int phyId = DBUtility.getIdNumber("healthsupporters");
		System.out.println();
		String phyName = takeInputFromConsole(reader,"Enter physician name");
		String clinic = takeInputFromConsole(reader,"Enter physician clinic");
		String username = takeInputFromConsole(reader,"Enter physician user name");
		String password = takeInputFromConsole(reader,"Enter physician password");
		
		String inputQuery = "insert into healthsupporters values("+phyId+",'"+phyName+"','"+
							clinic+"','"+username+"','"+password+"')";
		
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(inputQuery);
		System.out.println("Physician created");
		createUser(reader);
	}

	private static void createPatient(BufferedReader reader) throws IOException, SQLException {

		Connection connection = DBUtility.getConnection(); 
		
		int pid = DBUtility.getIdNumber("patient");
		String patientName = takeInputFromConsole(reader,"Enter patient name");
		String age = takeInputFromConsole(reader,"Enter patient age");
		String sex = takeInputFromConsole(reader,"Enter patient sex");
		String status = takeInputFromConsole(reader,"Enter patient status");
		String username = takeInputFromConsole(reader,"Enter patient username");
		String password = takeInputFromConsole(reader,"Enter patient password");
		String address = takeInputFromConsole(reader,"Enter patient street address");
		String city = takeInputFromConsole(reader,"Enter patient city");
		String state = takeInputFromConsole(reader,"Enter patient state");
		String zip = takeInputFromConsole(reader,"Enter patient zip");
		
		String inputQuery = "insert into patient values("
							+pid
							+",'"+patientName+"',"
							+age
							+",'"+sex+"',"
							+"'"+address+"',"
							+"'"+status+"',"
							+"'"+username+"',"
							+"'"+password+"',"
							+"'"+city+"',"
							+"'"+state+"',"
							+"'"+zip+"'"
							+")";
		
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(inputQuery);
		System.out.println("Patient created");
		createUser(reader);
	}

	private static String takeInputFromConsole(BufferedReader reader, String message)
			throws IOException {
		System.out.println(message);
		return reader.readLine();
	}

}
