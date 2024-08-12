package services;

import models.User;

public interface BaseService<T> {
    boolean createUser(T user);
    boolean createFriendship(int idUser1, int idUser2);
    boolean clearDB();
    int countFriendOfUser(int id);
    int countFriendOfFriendOfUser(int id);
    int countFriendOfFriendDepth4(int id);
}
