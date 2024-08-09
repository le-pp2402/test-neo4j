package services;

import models.User;

public interface BaseService {
    boolean createUser(User user);
    boolean createFriendship(int idUser1, int idUser2);
    boolean clearDB();
    int countFriendOfUser(int id);
    int countFriendOfFriendOfUser(int id);
}
