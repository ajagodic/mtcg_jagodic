package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.User;
import at.fhtw.app.model.Package;

// UserRepository.java
public interface UserRepository {
    User findByUsername(String username);
    void saveUser(User user);
    boolean editUserData(User user);
    String showStats(String username);
    int getCoins(String username);
    void updateCoins(String username, int coins);
    void addPackageToUser(String username, Package packageToAdd);
    void updateEloWin(String username);
    void updateEloLoss(String username);
    void updateWin(String username);
    void updateLoss(String username);
    boolean checkUserExists(String username);
    String getUserData(String username);
}

