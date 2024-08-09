package dbconnect;

import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jConnection {
    public String URI = System.getenv("NEO4J_URI");
    public static String username = System.getenv("NEO4J_USERNAME");
    public static String password = System.getenv("NEO4J_PASSWORD");

    private static Driver driver = null;
    public static Neo4jConnection instance;

    public Neo4jConnection() {
        try {
            AuthToken auth = AuthTokens.basic(username, password);
            driver = GraphDatabase.driver(URI, auth);
            driver.verifyConnectivity();
            System.out.println("Neo4j connection established.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Driver getDriver() {
        if (driver == null) instance = new Neo4jConnection();
        return driver;
    }
}
