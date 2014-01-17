package org.ncsu.cs.edu.utilities;

import java.sql.SQLException;
import java.util.ArrayList;

import org.ncsu.cs.edu.models.Patient;
import org.ncsu.cs.edu.models.Type;

public class DisplayUtility {

	public static void printInNewLines(String[] output){
		int len = output.length;
		for(int i=0; i<len; i++){
			System.out.println(output[i]);
		}
	}
	
	public static void printTypes(String pid){
		ArrayList<Type> items = DBUtility.getTypes(pid);
		int len = items.size();
		
		for(int i=0; i<len; i++){
			System.out.println((i+1)+"."+items.get(i).getSname());
		}
	}
	
	public static void printTypesForPhysician() throws SQLException{
		ArrayList<Type> items = DBUtility.getTypesForPhysician();
		int len = items.size();
		for(int i=0; i<len; i++){
			System.out.println((i+1)+"."+items.get(i).getSname());
		}
	}

	public static void printPatient(Patient patient) {
		System.out.println("-------------PATIENT INFORMATION---------------");
		System.out.println("pid-> "+patient.getPid());
		System.out.println("pname-> "+patient.getPname());
		System.out.println("user name-> "+patient.getUsername());
		System.out.println("age-> "+patient.getAge());
		System.out.println("sex-> "+patient.getSex());
		System.out.println("status-> "+patient.getStatus());
		System.out.println("address-> "+patient.getStatus());
		System.out.println("city-> "+patient.getStatus());
		System.out.println("state-> "+patient.getStatus());
		System.out.println("zip-> "+patient.getStatus());
	}
	
}
