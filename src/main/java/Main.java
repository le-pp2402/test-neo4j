import lombok.extern.slf4j.Slf4j;
import services.BaseService;
import services.Neo4jService;
import utils.UserGenerator;

@Slf4j
public class Main {

    public static void main(String[] args) {
        BaseService service = new Neo4jService();
        service.clearDB();
        UserGenerator.gen();
        for (var user: UserGenerator.users) {
            service.createUser(user);
        }
        for (var fs: UserGenerator.friendship) {
            service.createFriendship(fs.getFirst(), fs.getSecond());
        }
        Long start = System.currentTimeMillis();
        System.out.println(service.countFriendOfUser(2));
        Long end = System.currentTimeMillis();
        System.out.println(end - start + " ms");
    }

}