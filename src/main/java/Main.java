import lombok.extern.slf4j.Slf4j;
import models.User;
import services.BaseService;
import services.Generator;
import services.MySqlService;
import services.Neo4jService;

import java.io.FileNotFoundException;

/*
    Mở setting Neo4j comment dòng server.directories.import=import
    Để cho phép import từ các folder khác trong máy
 */
@Slf4j
public class Main {
    private static final Neo4jService neo4jService = new Neo4jService();
    private static final MySqlService mySqlService = new MySqlService();
    public static void main(String[] args) throws FileNotFoundException {

                    Generator.genDatasets(100, 3333);

            neo4jService.clearDB();
            neo4jService.loadData();

            mySqlService.clearDB();
            mySqlService.loadData();


//            long tt = System.currentTimeMillis();
//            System.out.println(mySqlService.countRelationshipLength4(5));
//            long ee = System.currentTimeMillis();
//
//            System.out.println("mysql: " + (ee - tt) + " ms");
//
//
//            long t = System.currentTimeMillis();
//            System.out.println(neo4jService.countRelationshipLength4(5));
//            long e = System.currentTimeMillis();
//
//            System.out.println("neo4j: " + (e - t) + " ms");
        System.out.println("START: ");
        long ttt = System.currentTimeMillis();
        System.out.println(neo4jService.count(23));
        long eee = System.currentTimeMillis();

        System.out.println("neo4j: " + (eee - ttt) + " ms");

        long t4 = System.currentTimeMillis();
        System.out.println(mySqlService.count(23));
        long e4 = System.currentTimeMillis();

        System.out.println("mysql: " + (e4 - t4) + " ms");
    }

}
