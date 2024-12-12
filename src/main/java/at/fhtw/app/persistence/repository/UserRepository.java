package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.User;

// UserRepository.java
public interface UserRepository {
    User findByUsername(String username);
    void saveUser(User user);
    boolean editUsername(String username, String newUsername);
    boolean editPassword(String username, String newPassword);
    String showStats(String username);
}

