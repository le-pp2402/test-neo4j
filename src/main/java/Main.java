import lombok.extern.slf4j.Slf4j;
import services.BaseService;
import services.MySqlService;
import services.Neo4jService;
import utils.UserGenerator;

@Slf4j
public class Main {

    public static void main(String[] args) {
        BaseService neo4jService = new Neo4jService();
        BaseService mySqlService = new MySqlService();

//        UserGenerator.gen();
//
//        {
//            mySqlService.clearDB();
//            for (var fs: UserGenerator.friendship) {
//                mySqlService.createFriendship(fs.getFirst(), fs.getSecond());
//            }
//        }
//
//        {
//            neo4jService.clearDB();
//            for (var user : UserGenerator.users) {
//                neo4jService.createUser(user);
//            }
//            for (var fs : UserGenerator.friendship) {
//                neo4jService.createFriendship(fs.getFirst(), fs.getSecond());
//            }
//        }

        System.out.println("FRIEND OF FRIEND OF USER: ");
        Long start = System.currentTimeMillis();
        System.out.println(neo4jService.countFriendOfFriendOfUser(4));
        Long end = System.currentTimeMillis();
        System.out.println("Neo4j: " + (end - start) + " ms");


        Long start1 = System.currentTimeMillis();
        System.out.println(mySqlService.countFriendOfFriendOfUser(4));
        Long end1 = System.currentTimeMillis();
        System.out.println("MySQL: " + (end1 - start1) + " ms");

    }

}