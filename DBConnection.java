import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    static String url =
            "jdbc:mysql://localhost:3306/library_management";

    static String username = "root";

    static String password = "Kranthi@2007";

    public static Connection getConnection() throws Exception {

        return DriverManager.getConnection(
                url,
                username,
                password
        );
    }
}