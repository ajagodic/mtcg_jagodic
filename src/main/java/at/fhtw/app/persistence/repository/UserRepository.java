package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.User;

// UserRepository.java
public interface UserRepository {
    User findByUsername(String username);
    void saveUser(User user);
}

