package org.ncsu.cs.edu.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.ncsu.cs.edu.utilities.DisplayUtility;
import org.ncsu.cs.edu.utilities.LoginUtility;

public class Controller{

	public static void main(String[] args) throws SQLException {
		Controller controller = new Controller();
		controller.inputHandlerMain();
	}

	public void inputHandlerMain() throws SQLException {
		DisplayUtility.printInNewLines(new String[]{"1. Login","2. Create User","3. Exit"});
		
		BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in));

		try {
			while (true) {
				String line = reader.readLine();
				boolean canExit = false;
				
				switch (line) {
				case "1":
					System.out.println("Login");
					LoginUtility.inputLoginHandler(reader);
					break;
				case "2":
					System.out.println("Create User");
					UserHandler.createUser(reader);
					break;
				case "3":
					System.out.println("Exit");
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
}
