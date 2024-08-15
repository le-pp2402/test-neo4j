package services;

import models.User;

public interface BaseService {
    boolean loadData();
    boolean clearDB();
    int countRelationshipLength4(int id);
    int countRelationshipLength5(int id);
    int countRelationshipLength6(int id);
    int countRelationshipLength7(int id);
}
