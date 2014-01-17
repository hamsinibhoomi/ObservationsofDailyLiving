package org.ncsu.cs.edu.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Test {
	public static void main(String[] args) {
		//"11-NOV-13 07.21.00.000000 PM"
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH.mm.ss.SSSSSS a");
		Calendar cal = Calendar.getInstance();
		String currDate = dateFormat.format(cal.getTime());
		System.out.println(currDate);
		
		dateFormat = new SimpleDateFormat("dd-MMM-yy");
		cal = Calendar.getInstance();
		currDate = dateFormat.format(cal.getTime());
		System.out.println(currDate);
	}
}
