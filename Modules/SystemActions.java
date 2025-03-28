package Modules;

import StaticData.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util. *;

public class SystemActions {
    public static boolean AdminMenu() {
        /* 3. Login as Administrator */
        char choice;
        String adminname, adminpass;
        String keyword, seller;
        System.out.print("----< Login as Administrator >\n" +
                " ** To go back, enter 'back' in user ID.\n" +
                "---- admin ID: ");

        try {
            adminname = Auction.scanner.next();
            Auction.scanner.nextLine();
            if(adminname.equalsIgnoreCase("back")){
                return false;
            }
            System.out.print("---- password: ");
            adminpass = Auction.scanner.nextLine();

            try (PreparedStatement pstmt = Auction.conn.prepareStatement(Query.QUERY_ADMIN_LOGIN)) {
                pstmt.setString(1, adminname);
                pstmt.setString(2, adminpass);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                int rowCnt = rs.getInt(1);
                if (rowCnt != 1) {
                    /* If Login Fails */
                    System.out.println("Error: Incorrect user name or password");
                    System.out.println(rowCnt);
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Failed to login due to some error. Sorry.");
                e.printStackTrace();
                return false;
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Error: Invalid input is entered. Try again.");
            return false;
        }

        do {
            System.out.println(
                    "----< Admin menu > \n" +
                            "    1. Print Sold Items per Category \n" +
                            "    2. Print Account Balance for Seller \n" +
                            "    3. Print Seller Ranking \n" +
                            "    4. Print Buyer Ranking \n" +
                            "    P. Go Back to Previous Menu"
            );

            try {
                choice = Auction.scanner.next().charAt(0);;
                Auction.scanner.nextLine();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error: Invalid input is entered. Try again.");
                continue;
            }

            if (choice == '1') {
                System.out.println("----Enter Category to search : ");
                keyword = Auction.scanner.next();
                Auction.scanner.nextLine();
                /*TODO: Print Sold Items per Category */
                System.out.println("sold item       | sold date       | seller ID   | buyer ID   | price | commissions");
                System.out.println("----------------------------------------------------------------------------------");
				/*
				   while(rset.next()){
				   }
				 */
                continue;
            } else if (choice == '2') {
                /*TODO: Print Account Balance for Seller */
                System.out.println("---- Enter Seller ID to search : ");
                seller = Auction.scanner.next();
                Auction.scanner.nextLine();
                System.out.println("sold item       | sold date       | buyer ID   | price | commissions");
                System.out.println("--------------------------------------------------------------------");
				/*
				   while(rset.next()){
				   }
				 */
                continue;
            } else if (choice == '3') {
                /*TODO: Print Seller Ranking */
                System.out.println("seller ID   | # of items sold | Total Profit (excluding commissions)");
                System.out.println("--------------------------------------------------------------------");
				/*
				   while(rset.next()){
				   }
				 */
                continue;
            } else if (choice == '4') {
                /*TODO: Print Buyer Ranking */
                System.out.println("buyer ID   | # of items purchased | Total Money Spent ");
                System.out.println("------------------------------------------------------");
				/*
				   while(rset.next()){
				   }
				 */
                continue;
            } else if (choice == 'P' || choice == 'p') {
                return false;
            } else {
                System.out.println("Error: Invalid input is entered. Try again.");
                continue;
            }
        } while(true);
    }


    public static boolean LoginMenu() {
        String userpass, isAdmin;
        System.out.print("----< User Login >\n" +
                " ** To go back, enter 'back' in user ID.\n" +
                "     user ID: ");
        try{
            Auction.username = Auction.scanner.next();
            Auction.scanner.nextLine();

            if(Auction.username.equalsIgnoreCase("back")){
                return false;
            }

            System.out.print("     password: ");
            userpass = Auction.scanner.next();
            Auction.scanner.nextLine();
        }catch (java.util.InputMismatchException e) {
            System.out.println("Error: Invalid input is entered. Try again.");
            Auction.username = null;
            return false;
        }
        try (PreparedStatement pstmt = Auction.conn.prepareStatement(Query.QUERY_LOGIN)) {
            pstmt.setString(1, Auction.username);
            pstmt.setString(2, userpass);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int rowCnt = rs.getInt(1);
            if (rowCnt == 1) { // Only a single row must be shown.
                System.out.println("You are successfully logged in.\n");
                return true;
            } else {
                /* If Login Fails */
                System.out.println("Error: Incorrect user name or password");
                System.out.println(rowCnt);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Failed to login due to some error. Sorry.");
            e.printStackTrace();
            Auction.username = null;
            return false;
        }
    }


    public static boolean SignupMenu() {

        /* 2. Sign Up */
        String new_username, userpass, isAdmin;
        System.out.print("----< Sign Up >\n" +
                " ** To go back, enter 'back' in user ID.\n" +
                "---- user name: ");
        try {
            new_username = Auction.scanner.next();
            Auction.scanner.nextLine();
            if(new_username.equalsIgnoreCase("back")){
                return false;
            }
            System.out.print("---- password: ");
            userpass = Auction.scanner.next(); // TODO: Maybe Hashing this thing?
            Auction.scanner.nextLine();
            System.out.print("---- In this user an administrator? (Y/N): ");
            isAdmin = Auction.scanner.next();
            if (!Objects.equals(isAdmin, "Y") &&  !Objects.equals(isAdmin, "N")) {
                System.out.println("Error: Invalid administrator value input is entered. Try again.");
                return false;
            }
            Auction.scanner.nextLine();
            try (PreparedStatement pstmt = Auction.conn.prepareStatement(Query.QUERY_REGISTER)) {
                pstmt.setString(1, Auction.username);
                pstmt.setString(2, userpass);
                pstmt.setString(3, Objects.equals(isAdmin, "Y") ? "true" : "false");

                if (pstmt.executeUpdate() == 1) { // Only a single row must be updated.
                    System.out.println("Your account has been successfully created.\n");
                    return true;
                } else {
                    /* If Registering Fails */
                    System.out.println("Error: Failed to register. Sorry.");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Failed to login due to some error. Sorry.");
                e.printStackTrace();
                Auction.username = null;
                return false;
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Error: Invalid input is entered. Please select again.");
            return false;
        }
    }
}
