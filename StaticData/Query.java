package StaticData;

public class Query {
    public static final String QUERY_LOGIN = "select count(*) from users where user_id = ? and password = ?";
    public static final String QUERY_REGISTER = "insert into users values (?, ?, ?)";
    public static final String QUERY_CHECKUSER_EXISTS = "select user_id from users where user_id = ?";
    // Admin Only Queries
    public static final String QUERY_ADMIN_LOGIN = "select count(*) from users where user_id = ? and password = ? and is_privileged = true";
    public static final String QUERY_ADMIN_PRINT_SOLD_ITEMS_PER_CATEGORY = "select * from auctions natural join items where auctions.auction_status = 'sold' and items.item_category like ?";
    public static final String QUERY_ADMIN_ACCOUNT_BALANCE_FOR_SELLER = "select (item_id, [Sold Date], buyer_id, [Price], [Commissions]) from billing where seller_id = ? and payment_status = 'sold'";
    public static final String QUERY_ADMIN_SELLER_RANKING = "select * from billing where seller_id = ?";
}
