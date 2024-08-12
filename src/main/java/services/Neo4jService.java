package services;

import dbconnect.Neo4jConnection;
import lombok.extern.slf4j.Slf4j;
import models.User;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@Slf4j
public class Neo4jService implements BaseService<List<User>> {
    public static Logger logger = LoggerFactory.getLogger(Neo4jService.class);
    public static Driver driver = Neo4jConnection.getDriver();
    public static String database = System.getenv("NEO4J_DATABASE");
    public static final Integer BATCH_SIZE = 100;
    public static String queryStm = """
                                       CREATE (p:Person {username: $username, user_id: $user_id})
                                    """;


    //

    @Override
    public boolean createUser(List<User> users) {
        var sessionCfg = SessionConfig.builder().withDatabase(database).withDefaultAccessMode(AccessMode.WRITE).build();
        try (var session = driver.session(sessionCfg)) {
            session.executeWrite(tx -> tx.run("UNWIND $batch AS user " +
                            "CREATE (a:Person {user_id: user.user_id, username: user.username})",
                    Values.parameters("batch", users.stream()
                            .map(user -> Values.parameters(
                                    "user_id", user.getId(),
                                    "username", user.getUsername()))
                            .toList())).consume());
        }
        return true;
    }

    List<Pair<Integer, Integer>> lstPair = new ArrayList<>();

    @Override
    public boolean createFriendship(int idUser1, int idUser2) {
        lstPair.add(new Pair<Integer, Integer>(idUser1, idUser2));
        return true;
    }

     public boolean createEdge() {
        var sessionCfg = SessionConfig.builder()
                .withDatabase(database)
                .withDefaultAccessMode(AccessMode.WRITE)
                .build();
        String sql = """
                     UNWIND $batch AS lstId
                     MATCH (a:Person {user_id: lstId[0]}), (b:Person {user_id: lstId[1]})
                     CREATE (a)-[:IS_FRIEND]->(b)
                     """;
        try (var session = driver.session(sessionCfg)) {
            for (int i = 0; i < lstPair.size(); i += BATCH_SIZE) {
                var lst = lstPair.subList(i, Math.min(lstPair.size(), i + BATCH_SIZE));
                session.executeWrite(tx -> tx.run(sql,
                        Values.parameters("batch",
                                lst.stream()
                                        .map(pair -> List.of(pair.getFirst(), pair.getSecond()))
                                        .toList())).consume());
                System.out.println("FINISHED BATCH: " + i);
            }
        }
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
                           RETURN COUNT(DISTINCT b) as ans
                       """;

        var params = new HashMap<String, Object>();
        params.put("id", id);

        int count = 0;
        try (var session = driver.session(sessionCfg)) {
            var resultSet = session.readTransaction(transaction -> transaction.run(queryStm, params).single());
            return resultSet.get("ans").asInt();
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public int countFriendOfFriendOfUser(int id) {
        var sessionCfg = SessionConfig.builder().withDatabase(database).withDefaultAccessMode(AccessMode.READ).build();

        var queryStm = """
                           MATCH (a {user_id: $id})-[r:IS_FRIEND*..2]-(b)
                           RETURN COUNT(DISTINCT b) as ans
                       """;

        var params = new HashMap<String, Object>();
        params.put("id", id);

        int count = 0;
        try (var session = driver.session(sessionCfg)) {
            var resultSet = session.readTransaction(transaction -> transaction.run(queryStm, params).single());
            return resultSet.get("ans").asInt();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public int countFriendOfFriendDepth4(int id) {
        var sessionCfg = SessionConfig.builder().withDatabase(database).withDefaultAccessMode(AccessMode.READ).build();

        var queryStm = """
                           MATCH (a {user_id: $id})-[r:IS_FRIEND*..3]-(b)
                           RETURN COUNT(DISTINCT b) as ans
                       """;

        var params = new HashMap<String, Object>();
        params.put("id", id);

        int count = 0;
        try (var session = driver.session(sessionCfg)) {
            var resultSet = session.readTransaction(transaction -> transaction.run(queryStm, params).single());
            return resultSet.get("ans").asInt();
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
