package org.ncsu.cs.edu.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.ncsu.cs.edu.models.Patient;
import org.ncsu.cs.edu.models.Type;

public class DBUtility {
	
	private static Connection connection = null;
	
	private static ArrayList<Type> types = null;
	
	public static Connection getConnection(){
		
		if(connection!=null)
			return connection;
		
		try {
			 
			Class.forName("oracle.jdbc.driver.OracleDriver");
 
		} catch (ClassNotFoundException e) {
 
			//System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return null;
 
		}
 
		//System.out.println("Oracle JDBC Driver Registered!");
 
		try {
			 
			connection = DriverManager.getConnection(
					 "jdbc:oracle:thin:@localhost:1521:xe", "dbms", "dbms");
 
		} catch (SQLException e) {
 
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
 
		}
		
		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		
		return connection;
	}
	
	public static int getIdNumber(String tableName){
		try {
			 
			Statement stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM "+tableName);
			int count = 0;
			while (rs.next()) {
				count++;
			}
			
			return count+1;
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return -1;
		}
	}
	
	public static String getDate(){
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH.mm.ss.SSSSSS a");
		Calendar cal = Calendar.getInstance();
		String currDate = dateFormat.format(cal.getTime());
		// return currDate;
		return "11-NOV-13 07.21.00.000000 PM";
	}
	
	public static String getDateOnly(){
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
		Calendar cal = Calendar.getInstance();
		String currDate = dateFormat.format(cal.getTime());
		// return currDate;
		return "11-NOV-13";
	}

	public static String getTypeId(String typeName) {
		
		try {
			
			Statement stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT typeid FROM type where sname='"+typeName
							+"'");
			String typeId = null;
			while (rs.next()) {
				typeId = rs.getString("typeid");
			}
			
			//System.out.println("typeId->"+typeId);
			
			return typeId;
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}
	}
	
	public static ArrayList<Type> getTypes(String pid){
		try {
			types = new ArrayList<Type>();
			Statement stmt = connection.createStatement();
			
			String typeQuery="select * from type where typeid in ((select typeid from type minus (select stypeid from diseasespecifictype)) union (select distinct stypeid from diseasespecifictype d, patient_disease p where d.diseaseid in (select diseaseid from patient_disease where pid="+pid+")))";
			// System.out.println("type query->"+typeQuery);
			ResultSet rs = stmt.executeQuery(typeQuery);
			while (rs.next()) {
				if(rs.getString("sname") == null)
					continue;
				Type type = new Type();
				type.setTypeid(rs.getString("typeid"));
				type.setSname(rs.getString("sname").replace(" ", ""));
				type.setCatId(rs.getString("catid"));
				
				type = getTypeAttributes(type);
				
				types.add(type);
			}
			
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return types;
		}
		return types;
	}
	
	public static ArrayList<Type> getTypesForPhysician() throws SQLException {
		ArrayList<Type> physicianTypes = new ArrayList<Type>();
		Statement stmt = connection.createStatement();

		String typeQuery = "select * from type";
		// System.out.println("type query->"+typeQuery);
		ResultSet rs = stmt.executeQuery(typeQuery);
		while (rs.next()) {
			if (rs.getString("sname") == null)
				continue;
			Type type = new Type();
			type.setTypeid(rs.getString("typeid"));
			type.setSname(rs.getString("sname").replace(" ", ""));
			type.setCatId(rs.getString("catid"));

			type = getTypeAttributes(type);

			physicianTypes.add(type);
		}
		return physicianTypes;
	}
	
	public static Type getTypeAttributes(Type type){
		ArrayList<String> attr = new ArrayList<String>();
		ArrayList<String> attrTypes = new ArrayList<String>();
		try {
			Statement stmt = connection.createStatement();
			String columnQuery = "select column_name,data_type from all_tab_columns where owner = 'DBMS' and table_name = '"+type.getSname().toUpperCase()+"'";
			ResultSet rs = stmt.executeQuery(columnQuery);
			//System.out.println("column query->"+columnQuery);
			while (rs.next()) {
				attr.add(rs.getString("column_name"));
				attrTypes.add(rs.getString("data_type").toLowerCase());
			}
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		type.setAttributes(attr);
		type.setAttributeTypes(attrTypes);
		return type;
	}

	public static ArrayList<String> getObservations(Type type, String pid) throws SQLException {
		Statement stmt = connection.createStatement();
		String query = "select oid from observations where typeid="+type.getTypeid()+" and pid="+pid;
		ResultSet rs = stmt.executeQuery(query);
		System.out.println("observation query->"+query);
		ArrayList<String> oids = new ArrayList<String>();
		while (rs.next()) {
			oids.add(rs.getString("oid"));
		}
		
		int size = oids.size();
		ArrayList<String> observations = new ArrayList<String>();
		
		ArrayList<String> attrs = type.getAttributes();
		int attrsSize = attrs.size();
		StringBuilder header = new StringBuilder();
		// ignore table id and oid
		for(int j=1; j<attrsSize-1; j++){
			header.append(attrs.get(j)+" ");
		}
		observations.add(header.toString());
		//System.out.println(header.toString());
		
		for(int i=0; i<size; i++){
			query = "select * from "+type.getSname()+" where oid="+oids.get(i);
			rs = stmt.executeQuery(query);
			//System.out.println("observation type query->"+query);
			StringBuilder observation = new StringBuilder();
			while (rs.next()) {
				// ignore table id and oid
				for(int j=1; j<attrsSize-1; j++){
					String temp = rs.getString(attrs.get(j)) != null ? rs.getString(attrs.get(j)).trim() : "";
					observation.append(temp+" ");
				}
				observations.add(observation.toString());
			}
		}
		
		return observations;
	}
	
	public static ArrayList<String> getHealthFriends(String pid) throws SQLException{
		ArrayList<String> names = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		String query = "select p.pname from Patient p, Addhealthfriend a where a.pid="+pid+" and a.hid = p.pid";
		ResultSet rs = stmt.executeQuery(query);
		//System.out.println("healthfriend query->"+query);
		while (rs.next()) {
			names.add(rs.getString("pname"));
		}
		return names;
	}
	
	public static ArrayList<String>[] findNewHealthFriend(String pid) throws SQLException{
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> ids = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		String query = "select p.pname,p.pid from patient p where p.status = 'Y' and p.pid not in (select a.hid from addhealthfriend a where a.hid = p.pid and a.pid ="+pid+")  and p.pid != "+pid;
		ResultSet rs = stmt.executeQuery(query);
		//System.out.println("healthfriend query->"+query);
		while (rs.next()) {
			names.add(rs.getString("pname"));
			ids.add(rs.getString("pid"));
		}
		ArrayList<String>[] output = new ArrayList [2];
		output[0] = names;
		output[1] = ids;
		return output;
	}
	
	public static ArrayList<String>[] findNewHealthFriendAtRisk(String pid) throws SQLException{
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> ids = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		String query = "select distinct pid from alert where pid in (select hid from addhealthfriend where pid="+pid+")";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			ids.add(rs.getString("pid"));
			names.add(getPatientName(rs.getString("pid")));
		}
		ArrayList<String>[] output = new ArrayList [2];
		output[0] = names;
		output[1] = ids;
		return output;
	}
	
	public static ArrayList<String>[] getIllnesses() throws SQLException{
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> ids = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		String query = "select * from disease";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			names.add(rs.getString("dname"));
			ids.add(rs.getString("diseaseid"));
		}
		ArrayList<String>[] output = new ArrayList [2];
		output[0] = names;
		output[1] = ids;
		return output;
	}
	
	public static ArrayList<String> getPatients(Type type) throws SQLException{
		ArrayList<String> patients = new ArrayList<String>();
		Connection conn = DBUtility.getConnection();
		Statement stmt = conn.createStatement();
		String tableQuery = "select pname from patient where pid in (select pid from diseasespecifictype d, patient_disease p where d.diseaseid = p.diseaseid and d.stypeid = "+type.getTypeid()+")";
		ResultSet rs = stmt.executeQuery(tableQuery);
		while (rs.next()) {
			patients.add(rs.getString("pname"));
		}
		
		if(patients.size() == 0){
			// selected type is a general type
			tableQuery = "select pname from patient";
			rs = stmt.executeQuery(tableQuery);
			while (rs.next()) {
				patients.add(rs.getString("pname"));
			}
		}
		
		return patients;
	}
	
	public static Patient getPatient(String patientName) throws SQLException{
		Connection conn = DBUtility.getConnection();
		Statement stmt = conn.createStatement();
		String tableQuery = "select * from patient where pname='"+patientName+"'";
		ResultSet rs = stmt.executeQuery(tableQuery);
		Patient patient = null;
		while (rs.next()) {
			patient = new Patient();
			patient.setPid(rs.getString("pid"));
			patient.setPname(rs.getString("pname"));
			patient.setAge(rs.getString("age"));
			patient.setSex(rs.getString("sex"));
			patient.setStatus(rs.getString("status"));
			patient.setAddress(rs.getString("address"));
			patient.setCity(rs.getString("city"));
			patient.setState(rs.getString("state"));
			patient.setZip(rs.getString("zip"));
			patient.setUsername(rs.getString("username"));
			patient.setPassword(rs.getString("password"));
		}
		return patient;
	}
	
	public static void setTypeAttribute(Type type) {
		types.add(type);
	}

	public static ArrayList<String>[] getAlerts(Patient patient) throws SQLException {
		// alert_id : you have an alert in getTypeName() on timestamp
		ArrayList<String> alertMessages = new ArrayList<String>();
		ArrayList<String> alertIds = new ArrayList<String>();
		
		Connection conn = DBUtility.getConnection();
		Statement stmt = conn.createStatement();
		String alertQuery = "select * from alert where pid="+patient.getPid();
		ResultSet rs = stmt.executeQuery(alertQuery);
		while (rs.next()) {
			String typeName = getTypeName(rs.getString("typeid"));
			String alertOn = rs.getString("alert_timestamp");
			alertMessages.add("You have an alert in "+typeName+" which was created on "+alertOn);
			alertIds.add(rs.getString("alert_id"));
		}
		
		ArrayList<String>[] output = new ArrayList[2];
		output[0] = alertMessages;
		output[1] = alertIds;
		
		return output;
	}
	
	public static String getTypeName(String typeId) throws SQLException{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select sname from type where typeid="+typeId);
		String typeName = null;
		while (rs.next()) {
			typeName = rs.getString("sname");
		}
		return typeName != null ? typeName.trim() : typeName;
	}
	
	public static void clearAlerts(ArrayList<String> alertIds) throws SQLException {
		Statement stmt = connection.createStatement();
		int size = alertIds.size();
		for(int i=0; i<size; i++){
			stmt.executeUpdate("delete from alert where alert_id="+alertIds.get(i));
			System.out.println("alert with id "+alertIds.get(i)+" has been cleared.");
		}
	}

	public static ArrayList<String>[] getAlertsOfHealthFriends(Patient patient) throws SQLException {
		// alert_id : you have an alert in getTypeName() on timestamp
				ArrayList<String> alertMessages = new ArrayList<String>();
				ArrayList<String> alertPids = new ArrayList<String>();
				
				Connection conn = DBUtility.getConnection();
				Statement stmt = conn.createStatement();
				String alertQuery = "select * from alerthealthfriend where hid="+patient.getPid();
				ResultSet rs = stmt.executeQuery(alertQuery);
				while (rs.next()) {
					String typeName = getTypeName(rs.getString("typeid"));
					String toName = getPatientName(rs.getString("pid"));
					alertMessages.add("Your friend "+toName+" has an alert in "+typeName);
					alertPids.add(rs.getString("pid"));
				}
				
				ArrayList<String>[] output = new ArrayList[2];
				output[0] = alertMessages;
				output[1] = alertPids;
				
				return output;
	}

	public static String getPatientName(String pid) throws SQLException {
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select pname from patient where pid="+pid);
		String patientName = null;
		while (rs.next()) {
			patientName = rs.getString("pname");
		}
		return patientName != null ? patientName.trim() : patientName;
	}

	public static void clearHealthFriendAlerts(String healthFriendId, String pid) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("delete from alerthealthfriend where pid="+healthFriendId+" and hid="+pid);
		System.out.println("alert with pid="+getPatientName(healthFriendId)+" and hid="+getPatientName(pid)+" has been cleared.");
	}

	public static void sendMessageToHealthFriend(String fromId, String toId,
			String message) throws SQLException {
		Statement stmt = connection.createStatement();
		int messageId = getIdNumber("message");
		String messageQuery = "insert into message values("+fromId+","+toId+",'"+message+"',"+messageId+")"; 
		stmt.executeUpdate(messageQuery);
		//System.out.println("message query->"+messageQuery);
		System.out.println("Message inserted for "+getPatientName(toId)+" from "+getPatientName(toId));
	}

	public static ArrayList<String> getMessages(String pid) throws SQLException {
		ArrayList<String> messages = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		String messageQuery = "select from_pid,message from message where to_pid="+pid;
		//System.out.println("message query->"+messageQuery);
		ResultSet rs = stmt.executeQuery(messageQuery);
		while (rs.next()) {
			String fromName = getPatientName(rs.getString("from_pid"));
			String message = rs.getString("message");
			messages.add("from : "+fromName+" , message : "+message);
		}
		return messages;
	}
	
	public static ArrayList<String>[] getAllPatients() throws SQLException{
		ArrayList<String> patients = new ArrayList<String>();
		ArrayList<String> pids = new ArrayList<String>();
		Statement stmt = connection.createStatement();
		String patientQuery = "select pname,pid from patient";
		//System.out.println("message query->"+messageQuery);
		ResultSet rs = stmt.executeQuery(patientQuery);
		while (rs.next()) {
			patients.add(rs.getString("pname"));
			pids.add(rs.getString("pid"));
		}
		
		ArrayList<String>[] output = new ArrayList[2];
		output[0] = patients;
		output[1] = pids;
		return output;
	}
}
