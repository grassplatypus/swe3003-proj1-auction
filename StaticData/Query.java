package StaticData;

public class Query {
    public static final String QUERY_LOGIN = "select count(*) from users where user_id = ? and password = ?;";
    public static final String QUERY_REGISTER = "insert into users values (?, ?, ?);";
    public static final String QUERY_CHECKUSER_EXISTS = "select user_id from users where user_id = ?;";
    // Admin Only Queries
    public static final String QUERY_ADMIN_LOGIN = "select count(*) from users where user_id = ? and password = ? and is_privileged = true";
    public static final String QUERY_ADMIN_PRINT_SOLD_ITEMS_PER_CATEGORY = "select item_desc, bid_end_time, seller_id, buyer_id, current_price, current_price*0.05 from sold_items where items.item_category like ?;";
    public static final String QUERY_ADMIN_ACCOUNT_BALANCE_FOR_SELLER = "select item_desc, bid_end_time, buyer_id, current_price, current_price*0.05 from sold_items where seller_id = ?;";
    public static final String QUERY_ADMIN_SELLER_RANKING = "select seller_id, count(1), sum(current_price)*0.95 as total_profit from billing group by seller_id order by total_profit desc;";
    public static final String QUERY_ADMIN_BUYER_RANKING = "select buyer_id, count(1), sum(current_price) as total_spent from billing group by buyer_id order by total_spent desc;";

    // User Queries
    public static final String QUERY_USER_SELL_ITEM_REGISTER = "insert into items (item_desc, seller_id, item_category, item_condition) values (?, ?, ?, ?) RETURNING item_id;";
    public static final String QUERY_USER_SELL_AUCTION_REGISTER = "insert into auctions (item_id, buy_it_now_price, bid_end_time, auction_status) values" +
            "(?, ?, ?, 'listed');";
    public static final String QUERY_USER_CHECK_SELLS = "select item_desc, bidder_id, bid_price, bid_time from selling_items where seller_id = ?;";
    public static final String QUERY_USER_BUY_ITEM = ";";
    // 수수료 금액 책정???
    public static final String QUERY_USER_CHECK_ACCOUNT_SOLD = "select item_category, auctions.item_id, bid_end_time, current_price, buyer_id, current_price*0.05 from sold_items where seller_id = ?;";
    public static final String QUERY_USER_CHECK_ACCOUNT_BOUGHT = "select item_category, auctions.item_id, bid_end_time, current_price, seller_id, current_price*0.05 from sold_items where buyer_id = ?;";


}
