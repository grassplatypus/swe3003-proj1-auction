package StaticData;

public class Define {
    public enum Category {
        ELECTRONICS,
        BOOKS,
        HOME,
        CLOTHING,
        SPORTINGGOODS,
        OTHERS
    }
    public enum Condition {
        NEW,
        LIKE_NEW,
        GOOD,
        ACCEPTABLE
    }

    public static String jdbcUrl = "jdbc:postgresql://localhost:5432/s21312673";
    public static String jdbcUsername = "s21312673";
    public static String jdbcPassword = "changethis";
}