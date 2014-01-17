package org.ncsu.cs.edu.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.ncsu.cs.edu.controller.PatientController;
import org.ncsu.cs.edu.controller.PhysicianController;
import org.ncsu.cs.edu.models.Patient;
import org.ncsu.cs.edu.models.Physician;

public class LoginUtility {
	
	public static void inputLoginHandler(BufferedReader reader) throws SQLException {
		System.out.println("Please enter login credentials");
		try {
			while(true){
				System.out.print("uid :" );
				String uid = reader.readLine();
				System.out.print("password :");
				String pwd = reader.readLine();
				Patient patient = authenticatePatientUser(uid, pwd); 
				if(patient!=null){
					// get user object here
					// based on user type call appropriate login menu here
					PatientController pc = new PatientController();
					pc.inputPatientHandler(reader,patient);
					break;
				}
				else{
					Physician physician = authenticatePhysicianUser(uid, pwd);
					if(physician!=null){
						// get user object here
						// based on user type call appropriate login menu here
						PhysicianController pc = new PhysicianController();
						pc.inputPhysicianHandler(reader,physician);
						break;
					}
					else{
						System.out.println("Invalid credentials. Please try again");
						continue;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static Physician authenticatePhysicianUser(String uid, String pwd){
		Connection conn = DBUtility.getConnection();
		Statement stmt = null;
        ResultSet rs = null;
        Physician physician = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT hid,hname from healthsupporters where username='"+uid+"' and password='"+pwd+"'");
			String pid = null, pname=null;
			while (rs.next()) {
			    pid = rs.getString("hid");
			    pname = rs.getString("hname");
			   // System.out.println("physician id ->"+pid);
			}
			if(pid!=null && pname!=null){
				physician = new Physician(pid,pname);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(physician!=null){
			System.out.println("Successfully logged in as physician "+physician.getPname());
		}
		return physician;
	}
	
	public static Patient authenticatePatientUser(String uid, String pwd){
		Connection conn = DBUtility.getConnection();
		Statement stmt = null;
        ResultSet rs = null;
        Patient patient = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT pid,pname from patient where username='"+uid+"' and password='"+pwd+"'");
			String pid = null, pname=null;
			while (rs.next()) {
			    pid = rs.getString("pid");
			    pname = rs.getString("pname");
			    //System.out.println("Patient id ->"+pid);
			}
			if(pid!=null && pname!=null){
				patient = new Patient(pid,pname);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(patient!=null){
			System.out.println("Successfully logged in as patient "+patient.getPname());
		}
		return patient;
	}
}
