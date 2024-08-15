package services;

import dbconnect.Neo4jConnection;
import lombok.extern.slf4j.Slf4j;
import models.User;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@Slf4j
public class Neo4jService implements BaseService {
    public static Logger logger = LoggerFactory.getLogger(Neo4jService.class);
    public static Driver driver = Neo4jConnection.getDriver();
    public static String database = System.getenv("NEO4J_DATABASE");

    public static final Integer BATCH_SIZE = 100;

    @Override
    public boolean loadData() {
        var sessionCfg = SessionConfig.builder().withDatabase(database).withDefaultAccessMode(AccessMode.WRITE).build();
        var userPath = Generator.temDir.resolve(Generator.USERS_CSV).toString();
        var frsPath = Generator.temDir.resolve(Generator.FRIENDSHIPS_CSV).toString();

        var addUserStm = """
                        LOAD CSV WITH HEADERS FROM $filepath AS row
                        MERGE(a: Person {id:toInteger(row.id), name:row.name})
                        RETURN a LIMIT 1
                        """;

        var param = new HashMap<>();
        param.put("filepath", "file:///" + userPath);

        try (var session = driver.session(sessionCfg)) {
            session.executeWrite(transaction -> transaction.run(addUserStm, Values.value(param)).consume());
        } catch (Exception e) {
            System.err.println("Load people fail check file " + userPath);
            System.err.println(e.getMessage());
            return false;
        }

        var addFrsStm = """
                        LOAD CSV WITH HEADERS FROM $filepath AS row
                        MATCH (p1:Person {id: toInteger(row.user_1)})
                        WITH p1, row
                        MATCH (p2:Person {id: toInteger(row.user_2)})
                        MERGE (p1)-[:FRIENDS]->(p2);
                        """;

        var param1 = new HashMap<>();
        param1.put("filepath", "file:///" + frsPath);

        try (var session = driver.session(sessionCfg)) {
            session.executeWrite(transaction -> transaction.run(addFrsStm, Values.value(param1)).consume());
        } catch (Exception e) {
            System.err.println("Load friendships fail check file " + frsPath);
            System.err.println(e.getMessage());
            return false;
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
            session.executeWrite(transaction -> transaction.run(queryStm).consume());
        } catch (Exception e) {
            log.error(e.getMessage());
            System.err.println(e.getMessage());
        }
        return true;
    }

    @Override
    public int countRelationshipLength4(int id) {
        String stm = """
                    MATCH (p1:Person{id: $id})
                        -[:FRIENDS]->(p2:Person)
                        -[:FRIENDS]->(p3:Person)
                        -[:FRIENDS]->(p4:Person)
                    WHERE 1=1
                        AND p1.id <> p3.id AND p1.id <> p4.id
                        AND p2.id <> p4.id
                    RETURN COUNT({n1:p1.name, n2:p2.name, n3:p3.name, n4:p4.name}) AS ans
                    """;
        var param = new HashMap<String, Object>();
        param.put("id", id);
        return ExecuteRead(stm, param);
    }

    public int count(int id) {
        String stm = """
                    MATCH (p1:Person{id: $id})
                        -[:FRIENDS]->(p2:Person)
                        -[:FRIENDS]->(p3:Person)
                        -[:FRIENDS]->(p4:Person)
                    RETURN COUNT(distinct p4.id) AS ans
                    """;
        var param = new HashMap<String, Object>();
        param.put("id", id);
        return ExecuteRead(stm, param);
    }

    @Override
    public int countRelationshipLength5(int id) {
        String stm = """
                    MATCH (p1:Person{id: $id})
                        -[:FRIENDS]->(p2:Person)
                        -[:FRIENDS]->(p3:Person)
                        -[:FRIENDS]->(p4:Person)
                        -[:FRIENDS]->(p5:Person)
                    WHERE 1=1
                        AND p1.id <> p3.id AND p1.id <> p4.id AND p1.id <> p5.id
                        AND p2.id <> p4.id AND p2.id <> p5.id
                        AND p3.id <> p5.id
                    RETURN COUNT({n1:p1.name, n2:p2.name, n3:p3.name, n4:p4.name, n5:p5.name}) as ans
                    """;
        var param = new HashMap<String, Object>();
        param.put("id", id);
        return ExecuteRead(stm, param);
    }

    @Override
    public int countRelationshipLength6(int id) {
        String stm = """
                    MATCH (p1:Person{id: $id})
                            -[:FRIENDS]->(p2:Person)
                            -[:FRIENDS]->(p3:Person)
                            -[:FRIENDS]->(p4:Person)
                            -[:FRIENDS]->(p5:Person)
                            -[:FRIENDS]->(p6:Person)
                        WHERE 1=1
                            AND p1.id <> p3.id AND p1.id <> p4.id AND p1.id <> p5.id AND p1.id <> p6.id
                            AND p2.id <> p4.id AND p2.id <> p5.id AND p2.id <> p6.id
                            AND p3.id <> p5.id AND p3.id <> p6.id
                            AND p4.id <> p6.id
                        RETURN COUNT({n1:p1.name, n2:p2.name, n3:p3.name, n4:p4.name, n5:p5.name, n6:p6.name}) AS ans
                    """;
        var param = new HashMap<String, Object>();
        param.put("id", id);
        return ExecuteRead(stm, param);
    }

    @Override
    public int countRelationshipLength7(int id) {
        String stm = """
                    MATCH (p1:Person{id: $id})
                            -[:FRIENDS]->(p2:Person)
                            -[:FRIENDS]->(p3:Person)
                            -[:FRIENDS]->(p4:Person)
                            -[:FRIENDS]->(p5:Person)
                            -[:FRIENDS]->(p6:Person)
                            -[:FRIENDS]->(p7:Person)
                        WHERE   NOT (p1 IN [p2, p3, p4, p5, p6, p7])
                            AND NOT (p2 IN [p4, p5, p6, p7])
                            AND NOT (p3 IN [p5, p6, p7])
                            AND NOT (p4 IN [p6, p7])
                            AND NOT (p5 IN [p7])
                        RETURN COUNT({n1:p1.id, n2:p2.id, n3:p3.id, n4:p4.id, n5:p5.id, n6:p6.id, n7: p7.id}) AS ans
                    """;
        var param = new HashMap<String, Object>();
        param.put("id", id);
        return ExecuteRead(stm, param);
    }

    public int ExecuteRead(String stm, HashMap<String, Object> param) {
        var sessionCfg = SessionConfig.builder().withDatabase(database).withDefaultAccessMode(AccessMode.WRITE).build();
        try (var session = driver.session(sessionCfg)) {
            return session.executeWrite(transaction -> transaction.run(stm, Values.value(param)).single()).get("ans").asInt();
        } catch (Exception e) {
            log.error(e.getMessage());
            System.err.println(e.getMessage());
        }
        return 0;
    }

}
