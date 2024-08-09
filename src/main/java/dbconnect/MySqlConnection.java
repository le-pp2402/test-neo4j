package dbconnect;

import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.util.Properties;

public class MySqlConnection {
    public String URI = System.getenv("MYSQL_URL");
    public static String username = System.getenv("MYSQL_USERNAME");
    public static String password = System.getenv("MYSQL_PASSWORD");

    private static Connection cn = null;
    private static MySqlConnection instance;
    public MySqlConnection() {
        try {
            Properties info = new Properties();
            info.put("user", username);
            info.put("password", password);
            cn = new Driver().connect(URI, info);
            System.out.println("MySQL connection established");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection() {
        if (cn == null) {
            instance = new MySqlConnection();
        }
        return cn;
    }
}
