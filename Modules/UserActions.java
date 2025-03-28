package Modules;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;

import Modules.*;
import StaticData.*;

public class UserActions {

    public static boolean SellMenu() {
        Define.Category category = null;
        Define.Condition condition = null;
        String description;
        char choice;
        int buyItNowPrice = 0;
        LocalDateTime bidEndDateTime = null;
        boolean flag_catg = true, flag_cond = true;

        do{
            System.out.println(
                    "----< Sell Item >\n" +
                            "---- Choose a category.\n" +
                            "    1. Electronics\n" +
                            "    2. Books\n" +
                            "    3. Home\n" +
                            "    4. Clothing\n" +
                            "    5. Sporting Goods\n" +
                            "    6. Other Categories\n" +
                            "    P. Go Back to Previous Menu"
            );

            try {
                choice = Auction.scanner.next().charAt(0);;
            }catch (InputMismatchException e) {
                System.out.println("Error: Invalid input is entered. Try again.");
                continue;
            }

            flag_catg = true;

            switch ((int) choice){
                case '1':
                    category = Define.Category.ELECTRONICS;
                    continue;
                case '2':
                    category = Define.Category.BOOKS;
                    continue;
                case '3':
                    category = Define.Category.HOME;
                    continue;
                case '4':
                    category = Define.Category.CLOTHING;
                    continue;
                case '5':
                    category = Define.Category.SPORTINGGOODS;
                    continue;
                case '6':
                    category = Define.Category.OTHERS;
                    continue;
                case 'p':
                case 'P':
                    return false;
                default:
                    System.out.println("Error: Invalid input is entered. Try again.");
                    flag_catg = false;
                    continue;
            }
        }while(!flag_catg);

        do{
            System.out.println(
                    "---- Select the condition of the item to sell.\n" +
                            "   1. New\n" +
                            "   2. Like-new\n" +
                            "   3. Used (Good)\n" +
                            "   4. Used (Acceptable)\n" +
                            "   P. Go Back to Previous Menu"
            );

            try {
                choice = Auction.scanner.next().charAt(0);;
                Auction.scanner.nextLine();
            }catch (InputMismatchException e) {
                System.out.println("Error: Invalid input is entered. Try again.");
                continue;
            }

            flag_cond = true;

            switch (choice) {
                case '1':
                    condition = Define.Condition.NEW;
                    break;
                case '2':
                    condition = Define.Condition.LIKE_NEW;
                    break;
                case '3':
                    condition = Define.Condition.GOOD;
                    break;
                case '4':
                    condition = Define.Condition.ACCEPTABLE;
                    break;
                case 'p':
                case 'P':
                    return false;
                default:
                    System.out.println("Error: Invalid input is entered. Try again.");
                    flag_cond = false;
                    continue;
            }
        }while(!flag_cond);

        try {
            System.out.println("---- Description of the item (one line): ");
            description = Auction.scanner.nextLine();
            System.out.println("---- Buy-It-Now price: ");

            while (!Auction.scanner.hasNextInt()) {
                Auction.scanner.next();
                System.out.println("Invalid input is entered. Please enter Buy-It-Now price: ");
            }

            buyItNowPrice = Auction.scanner.nextInt();
            Auction.scanner.nextLine();

            System.out.print("---- Bid closing date and time (YYYY-MM-DD HH:MM): ");
            // you may assume users always enter valid date/time
            String date = Auction.scanner.nextLine();  /* "2023-03-04 11:30"; */
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            bidEndDateTime = LocalDateTime.parse(date, formatter);
        }catch (Exception e) {
            System.out.println("Error: Invalid input is entered. Going back to the previous menu.");
            return false;
        }

        /* TODO: Your code should come here to store the user inputs in your database */

        try (PreparedStatement stmtAddItem = Auction.conn.prepareStatement(Query.QUERY_USER_SELL_ITEM_REGISTER,
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmtAddItem.setString(1, description); // item_desc
            stmtAddItem.setString(2, Auction.username); // seller_id
            stmtAddItem.setString(3, category.name());
            stmtAddItem.setString(4, condition.name());

            if (stmtAddItem.executeUpdate() != 1) {
                /* If Fails */
                System.out.println("Error: Failed to add item.");
                return false;
            }
            try (ResultSet rs = stmtAddItem.getGeneratedKeys()) {
                if (rs.next()) {
                    int itemId = rs.getInt(1);
                    try (PreparedStatement stmtAddAuction = Auction.conn.prepareStatement(Query.QUERY_USER_SELL_AUCTION_REGISTER)) {
                        stmtAddAuction.setString(1, String.valueOf(itemId));
                        stmtAddAuction.setString(2, String.valueOf(buyItNowPrice));
                        stmtAddAuction.setString(3, String.valueOf(bidEndDateTime));
                        if (stmtAddAuction.executeUpdate() != 1) {
                            System.out.println("Error: Failed to add auction.");
                            return false;
                        }
                    }
                } else {
                    System.out.println("Error: Failed to add auction.");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to sell due to some error. Sorry.");
            e.printStackTrace();
            return false;
        }

        System.out.println("Your item has been successfully listed.\n");
        return true;
    }

    public static void CheckSellStatus(){
        /* TODO: Check the status of the item the current user is selling */

        System.out.println("item listed in Auction | latest bidder (buyer ID) | latest bidding price | latest bidding date/time \n");
        System.out.println("-------------------------------------------------------------------------------\n");
        try (PreparedStatement pstmt = Auction.conn.prepareStatement(Query.QUERY_USER_CHECK_SELLS)) {
            pstmt.setString(1, Auction.username);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                System.out.println(rs.getString(1) + "\t"   // item (desc?)
                        + rs.getString(2) + "\t"         // bidder id
                        + rs.getBigDecimal(3) + "\t"            // bid price
                        + rs.getTimestamp(5) + "\t");     // bid time
            }

        } catch (Exception e) {
            System.out.println("Failed to check seller balance due to some error. Sorry.");
            e.printStackTrace();
        }
    }
    /// 아이템 구매
    /// 아이템을 조건별 검색 이후 구매까지 구현해야함.
    public static boolean BuyItem(){
        Define.Category category = Define.Category.ANY;
        Define.Condition condition = Define.Condition.ANY;
        char choice;
        int price = 0;
        String keyword = "", seller = "", datePosted = "";
        boolean flag_catg = true, flag_cond = true;

        do {

            System.out.println( "----< Select category > : \n" +
                    "    1. Electronics\n"+
                    "    2. Books\n" +
                    "    3. Home\n" +
                    "    4. Clothing\n" +
                    "    5. Sporting Goods\n" +
                    "    6. Other categories\n" +
                    "    7. Any category\n" +
                    "    P. Go Back to Previous Menu"
            );

            try {
                choice = Auction.scanner.next().charAt(0);;
                Auction.scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input is entered. Try again.");
                return false;
            }

            flag_catg = true;

            switch (choice) {
                case '1':
                    category = Define.Category.ELECTRONICS;
                    break;
                case '2':
                    category = Define.Category.BOOKS;
                    break;
                case '3':
                    category = Define.Category.HOME;
                    break;
                case '4':
                    category = Define.Category.CLOTHING;
                    break;
                case '5':
                    category = Define.Category.SPORTINGGOODS;
                    break;
                case '6':
                    category = Define.Category.OTHERS;
                    break;
                case '7':
                    break;
                case 'p':
                case 'P':
                    return false;
                default:
                    System.out.println("Error: Invalid input is entered. Try again.");
                    flag_catg = false;
                    continue;
            }
        } while(!flag_catg);

        do {

            System.out.println(
                    "----< Select the condition > \n" +
                            "   1. New\n" +
                            "   2. Like-new\n" +
                            "   3. Used (Good)\n" +
                            "   4. Used (Acceptable)\n" + // TODO: ANY 추가
                            "   P. Go Back to Previous Menu"
            );
            try {
                choice = Auction.scanner.next().charAt(0);;
                Auction.scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input is entered. Try again.");
                return false;
            }

            flag_cond = true;

            switch (choice) {
                case '1':
                    condition = Define.Condition.NEW;
                    break;
                case '2':
                    condition = Define.Condition.LIKE_NEW;
                    break;
                case '3':
                    condition = Define.Condition.GOOD;
                    break;
                case '4':
                    condition = Define.Condition.ACCEPTABLE;
                    break;
                case 'p':
                case 'P':
                    return false;
                default:
                    System.out.println("Error: Invalid input is entered. Try again.");
                    flag_cond = false;
            }
        } while(!flag_cond);

        try {
            System.out.println("---- Enter keyword to search the description : ");
            keyword = Auction.scanner.next();
            Auction.scanner.nextLine();

            System.out.println("---- Enter Seller ID to search : ");
            System.out.println(" ** Enter 'any' if you want to see items from any seller. ");
            seller = Auction.scanner.next();
            Auction.scanner.nextLine();

            System.out.println("---- Enter date posted (YYYY-MM-DD): ");
            System.out.println(" ** This will search items that have been posted after the designated date." +
                    "\n**Enter 'any' if you want to see items from any seller. ");
            datePosted = Auction.scanner.next();
            Auction.scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input is entered. Try again.");
            return false;
        }

        // 가지고 있는 변수들:
        // category, condition, keyword, seller, datePosted가 있음
        boolean hasCondition = false;
        List<String> searchConditions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "select " +
                "item_id, item_desc, item_condition, seller_id, buy_it_now_price, bidder_id, bid_end_time - now(), bid_end_time" +
                "from items join auctions " +
                "using (auction_id) left join " + // 입찰자 정보 없어도 상품은 표시
                "(select auction_id, bidder_id, max(bid_price) from bids group by auction_id, bidder_id) " +
                "using (auction_id) " +
                "order by bid_end_time");
        if (category != Define.Category.ANY) {
            sql.append(hasCondition ? " and " : " where");
            sql.append(" item_category = ?");
            searchConditions.add(category.name());
            hasCondition = true;
        }
        if (condition != Define.Condition.ANY) {
            sql.append(hasCondition ? " and " : " where");
            sql.append(" item_condition = ?");
            searchConditions.add(condition.name());
            hasCondition = true;
        }
        if (!Objects.equals(keyword, "")) {
            sql.append(hasCondition ? " and " : " where");
            sql.append(" item_desc like ?");
            searchConditions.add("%" + keyword + "%");
            hasCondition = true;
        }
        if (!Objects.equals(seller, "")) {
            sql.append(hasCondition ? " and " : " where");
            sql.append(" seller_id = ?");
            searchConditions.add(seller);
            hasCondition = true;
        }
        if (!Objects.equals(datePosted, "")) {
            sql.append(hasCondition ? " and " : " where");
            sql.append(" post_date >= ?"); // TODO: timestamp 형식에 맞게 표시하기
            searchConditions.add(seller);
            hasCondition = true;
        }

        System.out.println("Item ID | Item description | Condition | Seller | Buy-It-Now | Current Bid | highest bidder | Time left | bid close");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        try (PreparedStatement stmtSearch = Auction.conn.prepareStatement(sql.toString())) {
            int col = 1;
            for (String item: searchConditions) {
                stmtSearch.setString(col++, item);
            }

            ResultSet rs = stmtSearch.executeQuery();
            if (rs.next()) {
                int itemId = rs.getInt(1);
                try (PreparedStatement stmtAddAuction = Auction.conn.prepareStatement(Query.QUERY_USER_SELL_AUCTION_REGISTER)) {
                    stmtAddAuction.setString(1, String.valueOf(itemId));
                    stmtAddAuction.setString(2, String.valueOf(datePosted));
                    if (stmtAddAuction.executeUpdate() != 1) {
                        System.out.println("Error: Failed to print selling items.");
                        return false;
                    }
                }
            } else {
                System.out.println("Error: Failed to print selling items.");
                return false;
            }
            while(rs.next()) {
                System.out.println(rs.getInt(1) + "\t"   // id
                        + rs.getString(2) + "\t"
                        + rs.getString(3) + "\t"
                        + rs.getString(4) + "\t"
                        + rs.getBigDecimal(5) + "\t"
                        + rs.getBigDecimal(6) + "\t"
                        + rs.getString(7) + "\t"
                        + rs.getTimestamp(8) + "\t" // TODO: 시간 차를 timestamp로 표시?
                        + rs.getTimestamp(9)
                );
            }

        } catch (Exception e) {
            System.out.println("Failed to print selling items due to some error. Sorry.");
            e.printStackTrace();
            return false;
        }
        /* TODO: Query condition: item category */
        /* TODO: Query condition: item condition */
        /* TODO: Query condition: items whose description match the keyword (use LIKE operator) */
        /* TODO: Query condition: items from a particular seller */
        /* TODO: Query condition: posted date of item */

        /* TODO: List all items that match the query condition */
        /*
		   while(rset.next()){
		   }
		 */

        System.out.println("---- Select Item ID to buy or bid: ");

        try {
            choice = Auction.scanner.next().charAt(0);;
            Auction.scanner.nextLine();
            System.out.println("     Price: ");
            price = Auction.scanner.nextInt();
            Auction.scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input is entered. Try again.");
            return false;
        }
        int item_id = choice;

        /* TODO: Buy-it-now or bid: If the entered price is higher or equal to Buy-It-Now price, the bid ends and the following needs to be printed. */
        try (PreparedStatement stmtBuy = Auction.conn.prepareStatement("select buy_item(?, ?, ?)")) {
            stmtBuy.setInt(1, item_id);
            stmtBuy.setString(2, Auction.username);
            stmtBuy.setBigDecimal(3, BigDecimal.valueOf(price)); // int -> BigDecimal 변환
            ResultSet rs = stmtBuy.executeQuery();

            if (rs.next()) {
                String result = rs.getString(1); // purchased , bid-ok , failed
                switch (result) {
                    case "purchased":
                        /* Even if the bid price is higher than the Buy-It-Now price, the buyer pays the B-I-N price. */
                        System.out.println("Thank you for the purchase.\n");
                        break;
                    case "bid-ok":
                        /* Otherwise, print the following */
                        System.out.println("Congratulations, you are the highest bidder.\n");
                        break;
                    default:
                        /* If the entered price is lower than the current highest price, print out the following. */
                        System.out.println("You must bid higher than the current price. \n");
                }
            } else {
                System.out.println("Failed to buy item.");
            }

        } catch (Exception e) {
            System.out.println("Failed to buy due to some error. Sorry.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void CheckBuyStatus(){
        /* TODO: Check the status of the item the current buyer is bidding on */
        /* Even if you are outbidded or the bid closing date has passed, all the items this user has bidded on must be displayed */

        System.out.println("item ID   | item description   | highest bidder | highest bidding price | your bidding price | bid closing date/time");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
		/*
		   while(rset.next(){
		   System.out.println();
		   }
		 */
    }

    public static void CheckAccount(){
        /* TODO: Check the balance of the current user.  */

        System.out.println("[Sold Items] \n");
        System.out.println("item category  | item ID   | sold date | sold price  | buyer ID | commission  ");
        System.out.println("------------------------------------------------------------------------------");
        try (PreparedStatement stmtCheckSold = Auction.conn.prepareStatement(Query.QUERY_USER_CHECK_ACCOUNT_SOLD)) {
            stmtCheckSold.setString(1, Auction.username);
            ResultSet rs = stmtCheckSold.executeQuery();

		   while(rs.next()) {
		    System.out.println(rs.getString(1) + "\t"   // item category
                    + rs.getInt(2) + "\t"               // item id
                    + rs.getTimestamp(3) + "\t"         // sold date
                    + rs.getBigDecimal(4) + "\t"        // sold price
                    + rs.getString(5) + "\t"            // buyer ID
                    + rs.getBigDecimal(6));             // commission
		   }

        } catch (Exception e) {
            System.out.println("Failed to check sold items due to some error. Sorry.");
            e.printStackTrace();
            return;
        }


        System.out.println("[Purchased Items] \n");
        System.out.println("item category | item ID   | purchased date | purchased price  | seller ID ");
        System.out.println("--------------------------------------------------------------------------");
        try (PreparedStatement stmtCheckBought = Auction.conn.prepareStatement(Query.QUERY_USER_CHECK_ACCOUNT_SOLD)) {
            stmtCheckBought.setString(1, Auction.username);
            ResultSet rs = stmtCheckBought.executeQuery();

            while(rs.next()) {
                System.out.println(rs.getString(1) + "\t"   // item category
                        + rs.getInt(2) + "\t"               // item id
                        + rs.getTimestamp(3) + "\t"         // purchased date
                        + rs.getBigDecimal(4) + "\t"        // purchased price
                        + rs.getString(5) + "\t"            // buyer ID
                        + rs.getBigDecimal(6));             // commission
            }

        } catch (Exception e) {
            System.out.println("Failed to check bought items due to some error. Sorry.");
            e.printStackTrace();
        }
    }
}
