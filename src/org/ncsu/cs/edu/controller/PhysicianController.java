package org.ncsu.cs.edu.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.ncsu.cs.edu.models.Patient;
import org.ncsu.cs.edu.models.Physician;
import org.ncsu.cs.edu.models.Type;
import org.ncsu.cs.edu.utilities.DBUtility;
import org.ncsu.cs.edu.utilities.DisplayUtility;
import org.ncsu.cs.edu.utilities.LoginUtility;

public class PhysicianController {

	public void inputPhysicianHandler(BufferedReader reader, Physician physician) throws SQLException {
		DisplayUtility.printInNewLines(new String[]{
				"1. Add a New Observation Type",
				"2. Add an Association between Observation Type and Illness",
				"3. Add an Association between Patient and Illness",
				"4. View Patients",
				"5. Back"
				});
		
		try {
			while (true) {
				String line = reader.readLine();
				boolean canExit = false;
				
				switch (line) {
				case "1":
					System.out.println("Add a new Observation Type");
					addObservationType(reader,physician);
					break;
				case "2":
					System.out.println("Add an Association between Observation Type and Illness");
					createAssociationForObservationType(reader,physician);
					break;
				case "3":
					System.out.println("Add an Association between Patient and Illness");
					createAssociationForPatient(reader,physician);
					break;
				case "4":
					System.out.println("View Patients");
					viewPatients(reader,physician);
					break;
				case "5":
					System.out.println("Back<logout>");
					LoginUtility.inputLoginHandler(reader);
					canExit = true;
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

	private void createAssociationForPatient(BufferedReader reader,
			Physician physician) throws IOException, SQLException {
		
		// user needs to select a patient
		ArrayList<String>[] output = DBUtility.getAllPatients();
		ArrayList<String> patients = output[0];
		ArrayList<String> pids = output[1];
		int patientSize = patients.size();
		for (int i = 0; i < patientSize; i++) {
			System.out.println((i + 1) + "." + patients.get(i));
		}
		System.out.println(patientSize + 1 + ".Back");
		
		System.out.println("Select a patient from the above list.");
		String selectedPatientId = null;
		
		while (true) {
			String innerLine = reader.readLine();
			int innerLin = Integer.parseInt(innerLine);
			if (innerLin == patientSize + 1) {
				inputPhysicianHandler(reader, physician);
			} else if (innerLin <= patientSize ){
				selectedPatientId = pids.get(Integer.parseInt(innerLine) - 1);
				break;
			}
			else{
				System.out.println("Please enter a valid input");
			}
		}
		
		System.out.println("Listing Disease Types");
		// list the illnesses for the user to select
		output = DBUtility.getIllnesses();
		ArrayList<String> names = output[0];
		ArrayList<String> ids = output[1];

		int size = names.size();
		for (int i = 0; i < size; i++) {
			System.out.println((i + 1) + "." + names.get(i));
		}
		System.out.println((size + 1) + ".Back");
		while (true) {
			String innerLine = reader.readLine();
			int innerLin = Integer.parseInt(innerLine);
			if (innerLin == size + 1) {
				inputPhysicianHandler(reader, physician);
			} else {
				insertIntoPatientDiseaseTable(selectedPatientId, ids.get(innerLin - 1));
				break;
			}
		}
		// TODO:user needs to select an illness
		
		// TODO:create an entry in the patient_disease table
	}
	
	private void insertIntoPatientDiseaseTable(String patientId,
			String diseaseId) {
		System.out.println("patientid->"+patientId+" diseaseId->"+diseaseId);
		Connection conn = DBUtility.getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("INSERT into patient_disease values("+patientId+","+diseaseId+")");
			System.out.println("patient disease association created");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createAssociationForObservationType(BufferedReader reader,
			Physician physician) throws IOException, SQLException {
		ArrayList<Type> types = DBUtility.getTypes(physician.getPid());
		DisplayUtility.printTypes(physician.getPid());
		int typeSize = types.size();
		System.out.println((typeSize + 1) + ".Back");
		while (true) {
			String line = reader.readLine();
			int lin = Integer.parseInt(line);
			if (lin == types.size() + 1) {
				inputPhysicianHandler(reader, physician);
			} else {
				System.out.println("Listing Disease Types");
				// list the illnesses for the user to select
				ArrayList<String>[] output = DBUtility.getIllnesses();
				ArrayList<String> names = output[0];
				ArrayList<String> ids = output[1];

				int size = names.size();
				for (int i = 0; i < size; i++) {
					System.out.println((i + 1) + "." + names.get(i));
				}
				System.out.println((size + 1) + ".Back");
				while (true) {
					String innerLine = reader.readLine();
					int innerLin = Integer.parseInt(innerLine);
					if (innerLin == size + 1) {
						inputPhysicianHandler(reader, physician);
					} else {
						insertIntoSpecificTypeTable(Integer.parseInt(types.get(lin-1).getTypeid()), ids.get(innerLin - 1));
					}
				}
			}
		}
	}

	private void viewPatients(BufferedReader reader, Physician physician) throws SQLException {
		DisplayUtility.printInNewLines(new String[]{
				"1. View by Observation Type",
				"2. View by Patient Name",
				"3. Back"
				});
		
		try {
			while (true) {
				String line = reader.readLine();
				boolean canExit = false;
				
				switch (line) {
				case "1":
					System.out.println("View Patients by Observation Type");
					viewPatientsByObservationType(reader,physician);
					break;
				case "2":
					System.out.println("View Patients by Patient Name");
					viewPatientsByPatientName(reader, physician);
					break;
				case "3":
					System.out.println("Back<logout>");
					inputPhysicianHandler(reader, physician);
					canExit = true;
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

	private void viewPatientsByPatientName(BufferedReader reader,
			Physician physician) throws IOException, SQLException {
		System.out.println("Enter patient name");
		String patientName = reader.readLine();
		Patient patient = DBUtility.getPatient(patientName);
		if(patient!=null){
			DisplayUtility.printPatient(patient);
		}
		else{
			System.out.println("Patient with name "+patientName+" not exist");
		}
	}

	private void viewPatientsByObservationType(BufferedReader reader,
		Physician physician) throws IOException, SQLException {
		ArrayList<Type> types = DBUtility.getTypesForPhysician();
		DisplayUtility.printTypesForPhysician();
		int size = types.size();
		System.out.println((size+1)+".Back");
		while(true){
			String line = reader.readLine();
			int lin = Integer.parseInt(line);
			if(lin == types.size()+1){
				inputPhysicianHandler(reader, physician);
			}
			else{
				showPatientsByObsType(types.get(lin-1),reader, physician);
			}
		}
	}

	private void showPatientsByObsType(Type type, BufferedReader reader,
			Physician physician) throws SQLException, IOException {
		ArrayList<String> patients = DBUtility.getPatients(type);
		int size = patients.size();
		for(int i=0; i<size; i++){
			System.out.println((i+1)+"."+patients.get(i));
		}
		System.out.println("Enter -1 to get back to the previous menu");
		String input = reader.readLine();
		if(input.equals("-1")){
			viewPatients(reader, physician);
		}
	}

	private void addObservationType(BufferedReader reader, Physician physician) throws IOException, SQLException {
		System.out.println("Enter Observation Type name");
		String obName = reader.readLine();
		DisplayUtility.printInNewLines(new String[]{
				"Enter Observation category",
				"1 for behavioral",
				"2 for physological",
				"3 for psychological"
				});
		String obCat = reader.readLine();
		StringBuilder attrs = new StringBuilder();
		ArrayList<String> typeAttrs = new ArrayList<String>();
		ArrayList<String> typeAttrDataTypes = new ArrayList<String>();
		while(true){
			System.out.println("Enter Attribute name (enter -1 to finish)");
			String line = reader.readLine();
			if(line.equals("-1"))
				break;
			typeAttrs.add(line);
			typeAttrDataTypes.add("CHAR");
			attrs.append(line+" CHAR(100),");
		}
		
		// insert into type table
		Connection conn = DBUtility.getConnection();
		Statement stmt = conn.createStatement();
		int typeId = DBUtility.getIdNumber("type");
		String tableQuery = "insert into type values("+typeId+",'"+obName+"',"+obCat+")";
		stmt.executeUpdate(tableQuery);

		// create the observation table
		String createTableQuery = "CREATE TABLE " +obName+
				   " ("+attrs.substring(0,attrs.length()-1)+")";
		System.out.println("create table query->"+createTableQuery);
		stmt.execute(createTableQuery);
		System.out.println("Observation type table created!");
		
		// create association between observation type and illness
		createAssociationTypeIllness(reader, physician, typeId);
	}

	private void createAssociationTypeIllness(BufferedReader reader,
			Physician physician, int typeId) throws IOException, SQLException {
		DisplayUtility.printInNewLines(new String[]{
				"Is observation type general or specific",
				"1 for General",
				"2 for specific"
				});
		String obsType = reader.readLine();
		if(obsType.equals("2")){
			// list the illnesses for the user to select
			ArrayList<String>[] output = DBUtility.getIllnesses();
			ArrayList<String> names = output[0];
			ArrayList<String> ids = output[1];
			
			int size = names.size();
			for(int i=0; i<size; i++){
				System.out.println((i+1)+"."+names.get(i));
			}
			System.out.println((size+1)+".Back");
			while(true){
				String line = reader.readLine();
				int lin = Integer.parseInt(line);
				if(lin == size+1){
					inputPhysicianHandler(reader, physician);
				}
				else{
					insertIntoSpecificTypeTable(typeId,ids.get(lin-1));
				}
			}
		}
	}

	private void insertIntoSpecificTypeTable(int typeId, String diseaseId) {
		System.out.println("typeid->"+typeId+" diseaseId->"+diseaseId);
		Connection conn = DBUtility.getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate("INSERT into diseasespecifictype values("+typeId+","+diseaseId+")");
			System.out.println("type disease association created");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}