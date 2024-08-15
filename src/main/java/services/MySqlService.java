package services;

import dbconnect.MySqlConnection;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MySqlService implements BaseService {
    public static final Logger logger = LoggerFactory.getLogger(MySqlService.class);

    @Override
    public boolean loadData() {
        String sql = """
                       LOAD DATA INFILE ?
                       INTO TABLE t_user
                       COLUMNS TERMINATED BY ','
                       OPTIONALLY ENCLOSED BY '"'
                       ESCAPED BY '"'
                       LINES TERMINATED BY '\\n'
                       IGNORE 1 LINES;
                       """;
        String sql1 = """
                        LOAD DATA INFILE ?
                        INTO TABLE t_user_friend
                        COLUMNS TERMINATED BY ','
                        OPTIONALLY ENCLOSED BY '"'
                        ESCAPED BY '"'
                        LINES TERMINATED BY '\\n'
                        IGNORE 1 LINES;
                       """;
        try {
            var pp = MySqlConnection.getConnection().prepareStatement(sql);
            pp.setString(1, Generator.temDir.resolve(Generator.USERS_CSV).toString());
            pp.execute();

            pp = MySqlConnection.getConnection().prepareStatement(sql1);
            pp.setString(1, Generator.temDir.resolve(Generator.FRIENDSHIPS_CSV).toString());
            pp.execute();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean clearDB() {
        String sql  ="""
                        TRUNCATE TABLE t_user;
                     """;
        String sql1 ="""
                        TRUNCATE TABLE t_user_friend;
                     """;
        try {
            MySqlConnection.getConnection().prepareStatement(sql1).execute();
            MySqlConnection.getConnection().prepareStatement(sql).execute();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    public int count(int id) {
        String sql = """
                    SELECT count(DISTINCT uf3.user_2)
                    FROM            t_user_friend uf1
                          INNER JOIN 	t_user_friend uf2 ON uf2.user_1 = uf1.user_2
                          INNER JOIN 	t_user_friend uf3 ON uf3.user_1 = uf2.user_2
                    WHERE uf1.user_1 = ?
                """;
        try {
            var ps = MySqlConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    @Override
    public int countRelationshipLength4(int id) {
        String sql = """
                    SELECT
                       COUNT(*)
                    FROM            t_user_friend uf1
                          INNER JOIN 	t_user_friend uf2 ON uf2.user_1 = uf1.user_2
                          INNER JOIN 	t_user_friend uf3 ON uf3.user_1 = uf2.user_2
                    WHERE uf1.user_1 = ?
                            AND uf1.user_1 != uf2.user_2 AND uf1.user_1 != uf3.user_2
                        AND uf2.user_1 != uf3.user_2
                    ;
                """;
        try {
            var ps = MySqlConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    @Override
    public int countRelationshipLength5(int id) {
        return 0;
    }

    @Override
    public int countRelationshipLength6(int id) {
        return 0;
    }

    @Override
    public int countRelationshipLength7(int id) {
        return 0;
    }


}
