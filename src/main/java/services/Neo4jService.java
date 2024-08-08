package services;

import dbconnect.Neo4jConnection;
import lombok.extern.slf4j.Slf4j;
import models.User;
import org.neo4j.driver.AccessMode;
import org.neo4j.driver.Driver;
import org.neo4j.driver.SessionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


@Slf4j
public class Neo4jService implements BaseService {
    public static Logger logger = LoggerFactory.getLogger(BaseService.class);
    public static Driver driver = Neo4jConnection.getDriver();
    public static String database = System.getenv("NEO4J_DATABASE");

    @Override
    public boolean createUser(User user) {
        // if no set the default database will be used
        var sessionCfg = SessionConfig.builder().withDatabase(database).withDefaultAccessMode(AccessMode.WRITE).build();

        // prepare stm
        var queryStm = """
                       CREATE (p:Person {username: $username, user_id: $user_id})
                       """;
        var params = new HashMap<String, Object>();
                params.put("username", user.getUsername());
                params.put("user_id", user.getId());

        // using try to auto close session
        try (var session = driver.session(sessionCfg)) {
            session.writeTransaction(transaction -> transaction.run(queryStm, params).consume());
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
            e.printStackTrace();
            return false;
        }

//        System.out.println("created user with user id = " + user.getId());
        return true;
    }

    @Override
    public boolean createFriendship(int idUser1, int idUser2) {
        var sessionCfg = SessionConfig.builder().withDatabase(database).withDefaultAccessMode(AccessMode.WRITE).build();

        var queryStm = """
                            MATCH (a {user_id: $id1}), (b {user_id: $id2})
                            CREATE (a)-[r:IS_FRIEND]->(b)
                       """;

        var params = new HashMap<String, Object>();
        params.put("id1", idUser1);
        params.put("id2", idUser2);

        try (var session = driver.session(sessionCfg)) {
            session.writeTransaction(transaction -> transaction.run(queryStm, params).consume());
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
            e.printStackTrace();
            return false;
        }
//        System.out.println("created relationship with user id1 = " + idUser1 + " and user id2 = " + idUser2);
        return true;
    }

    @Override
    public boolean clearDB() {
        var sessionCfg = SessionConfig.builder().withDatabase(database).withDefaultAccessMode(AccessMode.WRITE).build();

        var queryStm = """
                            MATCH(a) DETACH DELETE a
                       """;

        try (var session = driver.session(sessionCfg)) {
            session.writeTransaction(transaction -> transaction.run(queryStm).consume());
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public int countFriendOfUser(int id) {
        var sessionCfg = SessionConfig.builder().withDatabase(database).withDefaultAccessMode(AccessMode.READ).build();

        var queryStm = """
                           MATCH (a {user_id: $id})-[r:IS_FRIEND]-(b)
                           RETURN b
                       """;

        var params = new HashMap<String, Object>();
        params.put("id", id);

        int count = 0;
        try (var session = driver.session(sessionCfg)) {
            var resultSet = session.readTransaction(transaction -> transaction.run(queryStm, params).list(e -> e.get("id")));
            for (var res: resultSet) {
                logger.info(res.toString());
                ++count;
            }
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
            e.printStackTrace();
        }
        return count;
    }


}


 // When u intend to read data, u should execute a Read transaction.
    // ---------------- write ----------------------- Write ----------: auto rollback if got error
    // for manually
    /*
            var session = ...
            var tx = session.beginTransaction();
            try {
                tx.run();

                tx.commit();
            } catch (Exception e) {

                tx.rollback();
            }
            session.close() // when u didn't user try block
     */
