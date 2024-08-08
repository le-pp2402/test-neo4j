package models;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class User {
    private Integer id;
    private String username;

    public User(int id) {
        this.id = id;
        username = UUID.randomUUID().toString().replace("-", "");
    }
}
