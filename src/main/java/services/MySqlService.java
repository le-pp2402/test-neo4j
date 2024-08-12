package services;

import dbconnect.MySqlConnection;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MySqlService implements BaseService<User> {
    public static final Logger logger = LoggerFactory.getLogger(MySqlService.class);
    public static String createFriendshipSql = """
                                    INSERT INTO friends(user_id_1, user_id_2)
                                    VALUES(?, ?);
                                """;
    public static String createFriendshipSql1 = """
                                    INSERT INTO friends(user_id_1, user_id_2)
                                    VALUES(?, ?);
                                """;
    public static PreparedStatement ppStatement;
    public static PreparedStatement ppStatement1;
    static {
        try {
            ppStatement = MySqlConnection.getConnection().prepareStatement(createFriendshipSql);
            ppStatement1 = MySqlConnection.getConnection().prepareStatement(createFriendshipSql1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean createUser(User user) {
        return true;
    }

    @Override
    public boolean createFriendship(int idUser1, int idUser2) {
        try {
            ppStatement.setInt(1, idUser1);
            ppStatement.setInt(2, idUser2);
            ppStatement.addBatch();

            ppStatement1.setInt(1, idUser2);
            ppStatement1.setInt(2, idUser1);
            ppStatement1.addBatch();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean clearDB() {
        String sql = """
                        DELETE FROM friends WHERE id > 0
                    """;
        try {
            return MySqlConnection.getConnection().prepareStatement(sql).execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public int countFriendOfUser(int id) {
        String sql = """
                       SELECT count(*) FROM friends
                       WHERE user_id_1 = ?
                    """;
        try {
            var stm = MySqlConnection.getConnection().prepareStatement(sql);
            stm.setInt(1, id);
            var rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    @Override
    public int countFriendOfFriendOfUser(int id) {
        String sql = """
                        SELECT count(distinct B.user_id_2)
                        FROM
                        (SELECT user_id_2 FROM friends WHERE user_id_1 = ?) AS A
                        INNER JOIN friends B WHERE user_id_1 = A.user_id_2
                    """;
        try {
            var stm = MySqlConnection.getConnection().prepareStatement(sql);
            stm.setInt(1, id);
            var rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    @Override
    public int countFriendOfFriendDepth4(int id) {
        String sql = """
                       SELECT count(distinct f4.user_id_2) AS cnt
                            FROM friends f1
                            INNER JOIN friends f2
                                on f1.user_id_2 = f2.user_id_1
                            INNER JOIN friends f3
                                on f2.user_id_2 = f3.user_id_1
                            INNER JOIN friends f4
                                on f3.user_id_2 = f4.user_id_1
                            WHERE f1.user_id_1 = ?
                    """;
        try {
            var stm = MySqlConnection.getConnection().prepareStatement(sql);
            stm.setInt(1, id);
            var rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }
}
