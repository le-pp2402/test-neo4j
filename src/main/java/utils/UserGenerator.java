package utils;

import models.User;

import java.util.ArrayList;
import java.util.List;


public class UserGenerator {
    public static List<User> users = new ArrayList<>();
    public static List<Pair<Integer, Integer>> friendship = new ArrayList<>();
    public static int numberOfUser = 5000;
    public static int friendPerUser = 100;
    private static final int MAX_SIZE = 10000;

    public static void genUser() {
        users.clear();
        for (var i = 1; i <= numberOfUser; i++) {
            users.add(new User(i));
        }
    }

    public static void genFriendship() {
        friendship.clear();
        for (var i = 1; i <= numberOfUser; i++) {
            for (int j = i + 1; j <= Math.min(numberOfUser, i + 1 + friendPerUser) && friendship.size() <= MAX_SIZE; j++) {
                friendship.add(new Pair<>(i, j));
            }
        }
    }

    public static void gen() {
        genUser();
        genFriendship();
    }
}
