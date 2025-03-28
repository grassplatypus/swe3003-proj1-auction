package Modules;

import StaticData.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SystemActions {
    private static boolean AdminMenu() {
        /* 3. Login as Administrator */
        char choice;
        String adminname, adminpass;
        String keyword, seller;
        System.out.print("----< Login as Administrator >\n" +
                " ** To go back, enter 'back' in user ID.\n" +
                "---- admin ID: ");

        try {
            adminname = scanner.next();
            scanner.nextLine();
            if(adminname.equalsIgnoreCase("back")){
                return false;
            }
            System.out.print("---- password: ");
            adminpass = scanner.nextLine();

            try (PreparedStatement pstmt = conn.prepareStatement(Query.QUERY_ADMIN_LOGIN)) {
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
                choice = scanner.next().charAt(0);;
                scanner.nextLine();
            } catch (java.util.InputMismatchException e) {
                System.out.println("Error: Invalid input is entered. Try again.");
                continue;
            }

            if (choice == '1') {
                System.out.println("----Enter Category to search : ");
                keyword = scanner.next();
                scanner.nextLine();
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
                seller = scanner.next();
                scanner.nextLine();
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
}
