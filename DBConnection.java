import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    static String url =
            "jdbc:mysql://bcuj3tifqslst5ph9al7-mysql.services.clever-cloud.com:3306/bcuj3tifqslst5ph9al7";

    static String username = "uuuqoqfq2gxovy2w";

    static String password = "cssd38SKSMZvbUiinWjE";

    public static Connection getConnection() throws Exception {

        return DriverManager.getConnection(
                url,
                username,
                password
        );
    }
}
