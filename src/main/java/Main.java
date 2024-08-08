import org.neo4j.driver.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
public class Main {
    public static Driver driver = DBHelper.getDriver();
    public static String database = "neo4j";
    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // gen
        gen();
        System.out.println("Nhập id người mà bạn muốn đếm số bạn: ");
        int id1 = sc.nextInt();
        var lstFriend1 = driver.executableQuery("""
                                    MATCH (a {id: $id1})-[r:IS_FRIEND]-(b)
                                    RETURN b
                                    """)
                .withParameters(Map.of("id1", String.valueOf(id1)))
                    .withConfig(QueryConfig.builder().withDatabase(database).build())
                    .execute();
        var lst1 = lstFriend1.records();
        int cnt = 0;
        for (var elem: lst1) {
            cnt++;
        }
        System.out.println(cnt);
    }

    public static void gen() {
        if (!clearDB()) {
            return;
        }
        int num;
        System.out.println("Bạn muốn tạo bao nhiêu người: ");
        num = sc.nextInt();
        for (int i = 1; i <= num; i++) {
            User user = new User(i);
            Map<String, String> properties = new HashMap<>();
            properties.put("name", user.getUsername());
            properties.put("id", user.getId().toString());
            driver.executableQuery("create($user)")
                .withParameters(Map.of("user", properties))
                    .withConfig(QueryConfig.builder().withDatabase(database).build())
                    .execute();
        }

        for (int i = 1; i <= num * 2; i++) {
            int user_1 = (int) (Math.random() * num);
            if (i % num != user_1) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("id1", String.valueOf(i % num + 1));
                params.put("id2", String.valueOf(user_1 + 1));
                var rs = driver.executableQuery("""
                            MATCH (a {id: $id1}), (b {id: $id2})
                            CREATE (a)-[r:IS_FRIEND]->(b)
                            """)
                        .withParameters(params)
                        .withConfig(QueryConfig.builder().withDatabase(database).build())
                        .execute();
            }
        }
    }
    public static boolean clearDB() {
        try {
            driver.executableQuery("MATCH(a) DETACH DELETE a").withConfig(
                    QueryConfig.builder().withDatabase(database).build()).execute();
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}