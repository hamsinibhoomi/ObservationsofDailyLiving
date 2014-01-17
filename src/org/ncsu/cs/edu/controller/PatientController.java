package org.ncsu.cs.edu.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.ncsu.cs.edu.models.Patient;
import org.ncsu.cs.edu.models.Type;
import org.ncsu.cs.edu.utilities.DBUtility;
import org.ncsu.cs.edu.utilities.DisplayUtility;
import org.ncsu.cs.edu.utilities.LoginUtility;

public class PatientController {

	public void inputPatientHandler(BufferedReader reader, Patient patient)
			throws SQLException {
		DisplayUtility.printInNewLines(new String[] { "1. Enter Observations",
				"2. View Observations", "3. Add a New Observation Type",
				"4. View MyAlerts", "5. View Alerts of Health Friends",
				"6. View MyMessages", "7. Manage HealthFriends", "8. Back" });

		try {
			while (true) {
				String line = reader.readLine();
				boolean canExit = false;

				switch (line) {
				case "1":
					System.out.println("Enter Observations");
					enterObservations(reader, patient);
					break;
				case "2":
					System.out.println("View Observations");
					viewObservations(reader, patient);
					break;
				case "3":
					System.out.println("Add a new Observation Type");
					addObservationType(reader, patient);
					break;
				case "4":
					System.out.println("View Alerts");
					viewAlerts(reader, patient);
					break;
				case "5":
					System.out.println("View Alerts of Health Friends");
					viewAlertsOfHealthFriends(reader, patient);
					break;
				case "6":
					System.out.println("View MyMessages");
					viewMessages(reader, patient);
					break;
				case "7":
					System.out.println("Manage HealthFriends");
					manageHealthFriends(reader, patient);
					break;
				case "8":
					System.out.println("Back<logout>");
					LoginUtility.inputLoginHandler(reader);
					canExit = true;
					break;
				default:
					System.out.println("Please enter a valid input");
					break;
				}

				if (canExit)
					break;
			}
		} catch (IOException e) {
			System.err.println(e);
		}

	}

	private void viewMessages(BufferedReader reader, Patient patient)
			throws SQLException, IOException {
		ArrayList<String> messages = DBUtility.getMessages(patient.getPid());
		int size = messages.size();
		for (int i = 0; i < size; i++) {
			System.out.println(messages.get(i));
		}
		while (true) {
			System.out.println("Enter -1 to go back to the previous menu.");
			String line = reader.readLine();
			if ("-1".equals(line)) {
				inputPatientHandler(reader, patient);
			}
		}
	}

	private void viewAlertsOfHealthFriends(BufferedReader reader,
			Patient patient) throws SQLException, IOException {
		ArrayList<String>[] output = DBUtility
				.getAlertsOfHealthFriends(patient);
		ArrayList<String> alertMessages = output[0];
		ArrayList<String> alertPids = output[1];

		int size = alertMessages.size();
		if (size == 0) {
			System.out
					.println("No new alerts. Going back to the previous menu.");
			inputPatientHandler(reader, patient);
		}
		for (int i = 0; i < size; i++) {
			System.out.println((i + 1) + "." + alertMessages.get(i));
		}
		while (true) {
			System.out
					.println("Enter alert number to send a message. Enter -1 to go to the previous menu.");
			String input = reader.readLine();
			if ("-1".equals(input)) {
				inputPatientHandler(reader, patient);
			} else {
				int lin = Integer.parseInt(input);
				System.out.println("Enter your message for "
						+ DBUtility.getPatientName(alertPids.get(lin - 1))
						+ ":");
				String message = reader.readLine();
				DBUtility.sendMessageToHealthFriend(patient.getPid(),
						alertPids.get(lin - 1), message);
				DBUtility.clearHealthFriendAlerts(alertPids.get(lin - 1),
						patient.getPid());
			}
		}
	}

	private void viewAlerts(BufferedReader reader, Patient patient)
			throws SQLException, IOException {
		ArrayList<String>[] output = DBUtility.getAlerts(patient);
		ArrayList<String> alertMessages = output[0];
		ArrayList<String> alertIds = output[1];

		int size = alertMessages.size();
		if (size == 0) {
			System.out
					.println("No new alerts. Going back to the previous menu.");
			inputPatientHandler(reader, patient);
		}
		for (int i = 0; i < size; i++) {
			System.out.println((i + 1) + "." + alertMessages.get(i));
		}
		while (true) {
			System.out
					.println("Enter -1 to clear messages and go the previous menu.");
			String input = reader.readLine();
			if ("-1".equals(input)) {
				DBUtility.clearAlerts(alertIds);
				inputPatientHandler(reader, patient);
			}
		}
	}

	private void manageHealthFriends(BufferedReader reader, Patient patient)
			throws SQLException {
		DisplayUtility.printInNewLines(new String[] {
				"1. View existing HealthFriends", "2. Find a new HealthFriend",
				"3. Find a HealthFriend at Risk", "4. Back" });
		try {
			while (true) {
				String line = reader.readLine();
				boolean canExit = false;

				switch (line) {
				case "1":
					System.out.println("View existing HealthFriends");
					viewExistingHealthFriends(reader, patient);
					break;
				case "2":
					System.out.println("Find a new HealthFriend");
					findNewHealthFriend(reader, patient);
					break;
				case "3":
					System.out.println("Find a HealthFriend at Risk");
					findHealthFriendAtRisk(reader, patient);
					break;
				case "4":
					System.out.println("Back");
					inputPatientHandler(reader, patient);
					break;
				default:
					System.out.println("Please enter a valid input");
					break;
				}

				if (canExit)
					break;
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	private void findHealthFriendAtRisk(BufferedReader reader, Patient patient)
			throws SQLException, IOException {
		ArrayList<String> friendsAtRisk;
		ArrayList<String>[] output = DBUtility
				.findNewHealthFriendAtRisk(patient.getPid());
		friendsAtRisk = output[0];
		int size = friendsAtRisk.size();
		if (size == 0) {
			System.out.println("No health friends at risk.");
		} else {
			for (int i = 0; i < size; i++) {
				System.out.println((i + 1) + "." + friendsAtRisk.get(i));
			}
		}
		while (true) {
			System.out.println((size + 1)
					+ ".Back (navigation action not a name)");
			String line = reader.readLine();
			int lin = Integer.parseInt(line);
			if (lin == size + 1) {
				manageHealthFriends(reader, patient);
			}
		}
	}

	private void findNewHealthFriend(BufferedReader reader, Patient patient)
			throws SQLException, IOException {
		ArrayList<String> friends = null, ids = null;
		ArrayList<String>[] output = DBUtility.findNewHealthFriend(patient
				.getPid());
		friends = output[0];
		ids = output[1];
		int size = friends.size();
		if (size == 0) {
			System.out.println("No health friends to add.");
		} else {
			for (int i = 0; i < size; i++) {
				System.out.println((i + 1) + "." + friends.get(i));
			}
			System.out
					.println("Please select a number associated with a name to add him as a health friend");
		}
		System.out.println((size + 1) + ".Back (navigation action not a name)");
		while (true) {
			String line = reader.readLine();
			int lin = Integer.parseInt(line);
			if (lin == size + 1) {
				manageHealthFriends(reader, patient);
			} else {
				insertIntoAddHealthFriendTable(ids.get(lin - 1), reader,
						patient);
			}
		}
	}

	private void insertIntoAddHealthFriendTable(String index,
			BufferedReader reader, Patient patient) {
		int nextEntryId = DBUtility.getIdNumber("addhealthfriend");
		//System.out.println("nextEntryId->" + nextEntryId);
		String insertQuery = "INSERT into addhealthfriend values("
				+ nextEntryId + "," + patient.getPid() + ",'"
				+ DBUtility.getDateOnly() + "'," + index + ")";
		System.out.println("adding health friend query->" + insertQuery);
		Connection conn = DBUtility.getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(insertQuery);
			System.out.println("health friend created");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void viewExistingHealthFriends(BufferedReader reader,
			Patient patient) throws SQLException {
		ArrayList<String> friends = DBUtility
				.getHealthFriends(patient.getPid());
		int size = friends.size();
		for (int i = 0; i < size; i++) {
			System.out.println("->" + friends.get(i));
		}
	}

	private void addObservationType(BufferedReader reader, Patient patient)
			throws IOException, SQLException {
		System.out.println("Enter Observation Type name");
		String obName = reader.readLine();
		DisplayUtility.printInNewLines(new String[] {
				"Enter Observation category", "1 for behavioral",
				"2 for physological", "3 for psychological" });
		String obCat = reader.readLine();
		StringBuilder attrs = new StringBuilder();
		ArrayList<String> typeAttrs = new ArrayList<String>();
		ArrayList<String> typeAttrDataTypes = new ArrayList<String>();
		while (true) {
			System.out.println("Enter Attribute name (enter -1 to finish)");
			String line = reader.readLine();
			if (line.equals("-1"))
				break;
			typeAttrs.add(line);
			typeAttrDataTypes.add("CHAR");
			attrs.append(line + " CHAR(100),");
		}

		// insert into type table
		Connection conn = DBUtility.getConnection();
		Statement stmt = conn.createStatement();
		int typeId = DBUtility.getIdNumber("type");
		String tableQuery = "insert into type values(" + typeId + ",'" + obName
				+ "'," + obCat + ")";
		stmt.executeUpdate(tableQuery);

		// create the observation table
		String createTableQuery = "CREATE TABLE " + obName + " ( oid NUMBER,"
				+ attrs.substring(0, attrs.length() - 1) + ")";
		System.out.println("create table query->" + createTableQuery);
		stmt.execute(createTableQuery);
		System.out.println("Observation type table created!");
	}

	private void viewObservations(BufferedReader reader, Patient patient)
			throws IOException, SQLException {
		ArrayList<Type> types = DBUtility.getTypes(patient.getPid());
		DisplayUtility.printTypes(patient.getPid());
		int size = types.size();
		System.out.println((size + 1) + ".Back");
		while (true) {
			String line = reader.readLine();
			int lin = Integer.parseInt(line);
			if (lin == types.size() + 1) {
				inputPatientHandler(reader, patient);
			} else {
				showObservations(types.get(lin - 1), reader, patient);
			}
		}
	}

	private void enterObservations(BufferedReader reader, Patient patient)
			throws IOException, SQLException {
		ArrayList<Type> types = DBUtility.getTypes(patient.getPid());
		DisplayUtility.printTypes(patient.getPid());
		int size = types.size();
		System.out.println((size + 1) + ".Back");
		while (true) {
			String line = reader.readLine();
			int lin = Integer.parseInt(line);
			if (lin == types.size() + 1) {
				inputPatientHandler(reader, patient);
			} else {
				insertIntoTypeTable(types.get(lin - 1), reader, patient);
			}
		}
	}

	private void showObservations(Type type, BufferedReader reader,
			Patient patient) throws SQLException, IOException {

		ArrayList<String> observations = DBUtility.getObservations(type,
				patient.getPid());
		int size = observations.size();
		for (int i = 0; i < size; i++) {
			System.out.println("->" + observations.get(i));
		}
		// viewObservations(reader, patient);
	}

	private void insertIntoTypeTable(Type type, BufferedReader reader,
			Patient patient) throws IOException {
		ArrayList<String> attr = type.getAttributes();
		ArrayList<String> attrTypes = type.getAttributeTypes();
		int size = attr.size();
		String[] input = new String[size];
		int nextEntryId = DBUtility.getIdNumber(type.getSname());
		System.out.println("primary key for the type table to use->" + nextEntryId);
		int obsId = DBUtility.getIdNumber("observations");
		System.out.println("observationId to use->" + obsId);
		String insertQuery = "INSERT into " + type.getSname() + " values(";

		if (type.getSname().equalsIgnoreCase("diet")) {
			String dietInsertQuery = "INSERT ALL ";
			while (true) {
				System.out.println("FOOD CONSUMED");
				String foodConsumed = reader.readLine();
				System.out.println("AMOUNT");
				String amount = reader.readLine();
				dietInsertQuery += "into " + type.getSname() + " values("
						+ (nextEntryId++) + ",'" + foodConsumed + "'," + amount
						+ "," + obsId + ") ";
				System.out
						.println("Enter 1 to add another Food item or Enter -1 to finish adding the attributes.");
				System.out
						.println("Diet Id and Observation Id are automatically calculated. So no need to enter by the user in case of DIET table.");
				String temp = reader.readLine();
				if ("-1".equals(temp)) {
					break;
				}
			}
			dietInsertQuery += "SELECT * FROM dual";
			insertQuery = dietInsertQuery;
			//System.out.println("insert query->" + insertQuery);
		} else {
			for (int i = 0; i < size; i++) {
				System.out.println(attr.get(i));
				input[i] = reader.readLine();
				if (attrTypes.get(i).contains("char")) {
					insertQuery += "'" + input[i] + "',";
				} else {
					insertQuery += input[i] + ",";
				}
			}
			insertQuery = insertQuery.substring(0, insertQuery.length() - 1);
			insertQuery +=  ")";
			//System.out.println("insert query->" + insertQuery);
		}
		Connection conn = DBUtility.getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			// diet type id = 7 and how to get cat id
			String typeName = type.getSname();
			String typeId = DBUtility.getTypeId(typeName);

			String date = DBUtility.getDate();
			// creating an observation
			stmt.executeUpdate("INSERT into observations values(" + obsId + ","
					+ patient.getPid() + "," + typeId + ",'" + date + "','"
					+ date + "')");
			// creating an diet entry
			stmt.executeUpdate(insertQuery);
			System.out.println("type created");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
