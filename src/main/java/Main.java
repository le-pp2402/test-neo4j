import dbconnect.MySqlConnection;
import lombok.extern.slf4j.Slf4j;
import models.User;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.cypher.internal.parser.v5.ast.factory.LabelExpressionBuilder;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.driver.types.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.impl.core.NodeEntity;
import services.BaseService;
import services.MySqlService;
import services.Neo4jService;
import utils.UserGenerator;

import java.io.File;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Neo4jService neo4jService = new Neo4jService();
        BaseService<User> mySqlService = new MySqlService();

        UserGenerator.gen();

        {
            try {
                mySqlService.clearDB();
                MySqlConnection.getConnection().setAutoCommit(false);
                for (var fs: UserGenerator.friendship) {
                    mySqlService.createFriendship(fs.getFirst(), fs.getSecond());
                }
                MySqlService.ppStatement.executeBatch();
                MySqlService.ppStatement1.executeBatch();
                MySqlConnection.getConnection().commit();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return;
            }
        }

        {
            neo4jService.clearDB();
            neo4jService.createUser(UserGenerator.users);
            System.out.println("finished create user task");
            for (var fs : UserGenerator.friendship) {
                neo4jService.createFriendship(fs.getFirst(), fs.getSecond());
            }
            System.out.println("finished init user task");
            neo4jService.createEdge();
        }

        System.out.println("FRIEND OF USER: ");
        Long start3 = System.currentTimeMillis();
        System.out.println(neo4jService.countFriendOfUser(2));
        Long end3 = System.currentTimeMillis();
        System.out.println("Neo4j: " + (end3 - start3) + " ms");


        Long start4 = System.currentTimeMillis();
        System.out.println(mySqlService.countFriendOfUser(2));
        Long end4 = System.currentTimeMillis();
        System.out.println("MySQL: " + (end4 - start4) + " ms");

        System.out.println("FRIEND OF FRIEND OF USER: ");
        Long start = System.currentTimeMillis();
        System.out.println(neo4jService.countFriendOfFriendOfUser(4));
        Long end = System.currentTimeMillis();
        System.out.println("Neo4j: " + (end - start) + " ms");


        Long start1 = System.currentTimeMillis();
        System.out.println(mySqlService.countFriendOfFriendOfUser(4));
        Long end1 = System.currentTimeMillis();
        System.out.println("MySQL: " + (end1 - start1) + " ms");

        System.out.println("FRIEND OF FRIEND OF USER dep = 4: ");
        Long start5 = System.currentTimeMillis();
        System.out.println(neo4jService.countFriendOfFriendDepth4(4));
        Long end5 = System.currentTimeMillis();
        System.out.println("Neo4j: " + (end5 - start5) + " ms");


        Long start6 = System.currentTimeMillis();
        System.out.println(mySqlService.countFriendOfFriendDepth4(4));
        Long end6 = System.currentTimeMillis();
        System.out.println("MySQL: " + (end6 - start6) + " ms");
    }

}