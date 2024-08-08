import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.io.Console;

public class DBHelper {
    public static String URI = "bolt://localhost:7687";
    public static String username = "neo4j";
    public static String password = "123456789";
    private static Driver driver = null;
    public static DBHelper instance;

    DBHelper() {
        try {
            driver = GraphDatabase.driver(URI, AuthTokens.basic(username, password));
            driver.verifyConnectivity();
            System.out.println("Connection established.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Driver getDriver() {
        if (driver == null) {
            instance = new DBHelper();
        }
        return driver;
    }
}
