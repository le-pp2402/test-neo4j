package services;

import dbconnect.MySqlConnection;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Slf4j
public class MySqlService implements BaseService {
    public static final Logger logger = LoggerFactory.getLogger(MySqlService.class);
    @Override
    public boolean createUser(User user) {
        return true;
    }

    @Override
    public boolean createFriendship(int idUser1, int idUser2) {
        String sql = """
                        INSERT INTO friends(user_id_1, user_id_2)
                        VALUES(?, ?)
                    """;

        try {
            var stm = MySqlConnection.getConnection().prepareStatement(sql);
            stm.setInt(1, idUser1);
            stm.setInt(2, idUser2);
            return stm.execute();
        } catch (SQLException e) {
            log.error(e.getMessage());
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
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public int countFriendOfUser(int id) {
        String sql = """
                       SELECT count(*) FROM friends
                       WHERE user_id_1 = ? OR user_id_2 = ?
                    """;
        try {
            var stm = MySqlConnection.getConnection().prepareStatement(sql);
            stm.setInt(1, id);
            stm.setInt(2, id);
            var rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            log.error(e.getMessage());
            return 0;
        }
    }

    @Override
    public int countFriendOfFriendOfUser(int id) {
        String sql = """
                        SELECT count(*) FROM 
                        (
                            SELECT DISTINCT u.ii 
                            FROM
                            (
                                (
                                    SELECT user_id_2 AS 'ii' 
                                    FROM friends 
                                    WHERE user_id_1 = ?
                                )
                                UNION
                                (
                                    SELECT user_id_1 AS 'ii' 
                                    FROM friends 
                                    WHERE user_id_2 = ?
                                )
                             ) AS u
                         ) AS v 
                         INNER JOIN friends fr ON ii = fr.user_id_1 or ii = fr.user_id_2 
                    """;
        try {
            var stm = MySqlConnection.getConnection().prepareStatement(sql);
            stm.setInt(1, id);
            stm.setInt(2, id);
            var rs = stm.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            log.error(e.getMessage());
            return 0;
        }
    }
}
