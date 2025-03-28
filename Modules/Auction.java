package Modules;

import java.sql.*;
import java.util. *;
import StaticData. *;

public class Auction {
	public static Scanner scanner = new Scanner(System.in);
	public static String username;
	public static Connection conn;

	public static void main(String[] args) throws SQLException {
		char choice;
		boolean ret;

//		if(args.length<2){
//			System.out.println("Usage: java Modules.Auction postgres_id password");
//			System.exit(1);
//		}


		try{
            //    	conn = DriverManager.getConnection("jdbc:postgresql://localhost/"+args[0], args[0], args[1]); 
            conn = DriverManager.getConnection(Define.jdbcUrl, Define.jdbcUsername, Define.jdbcPassword);
		}
		catch(SQLException e){
			System.out.println("SQLException : " + e);	
			System.exit(1);
		} catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(conn);

		do {
			username = null;
			System.out.println(
					"----< Login menu >\n" + 
					"----(1) Login\n" +
					"----(2) Sign up\n" +
					"----(3) Login as Administrator\n" +
					"----(Q) Quit"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			try {
				switch ((int) choice) {
					case '1':
						ret = SystemActions.LoginMenu();
						if(!ret) continue;
						break;
					case '2':
						ret = SystemActions.SignupMenu();
						if(!ret) continue;
						break;
					case '3':
						ret = SystemActions.AdminMenu();
						if(!ret) continue;
					case 'q':
					case 'Q':
						System.out.println("Good Bye");
						/* TODO: close the connection and clean up everything here */
						conn.close();
						System.exit(0);
					default:
						System.out.println("Error: Invalid input is entered. Try again.");
				}
			} catch (SQLException e) {
				System.out.println("SQLException : " + e);	
			}
		} while (username==null || username.equalsIgnoreCase("back"));  

		// logged in as a normal user 
		do {
			System.out.println(
					"---< Main menu > :\n" +
					"----(1) Sell Item\n" +
					"----(2) Check Status of Your Listed Item \n" +
					"----(3) Buy Item\n" +
					"----(4) Check Status of your Bid \n" +
					"----(5) Check your Account \n" +
					"----(Q) Quit"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			try{
				switch (choice) {
					case '1':
						ret = UserActions.SellMenu();
						if(!ret) continue;
						break;
					case '2':
						UserActions.CheckSellStatus();
						break;
					case '3':
						ret = UserActions.BuyItem();
						if(!ret) continue;
						break;
					case '4':
						UserActions.CheckBuyStatus();
						break;
					case '5':
						UserActions.CheckAccount();
						break;
					case 'q':
					case 'Q':
						System.out.println("Good Bye");
						/* TODO: close the connection and clean up everything here */
						conn.close();
						System.exit(0);
				}
			} catch (SQLException e) {
				System.out.println("SQLException : " + e);	
				System.exit(1);
			}
		} while(true);
	} // End of main 
} // End of class


