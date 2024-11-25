package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.User;
import at.fhtw.app.persistence.DataAccessException;
import at.fhtw.app.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// UserRepositoryImpl.java
public class UserRepositoryImpl implements UserRepository {
    private UnitOfWork unitOfWork;

    public UserRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
                    statement.setString(1, username);
                    ResultSet rs = statement.executeQuery();
                    unitOfWork.commitTransaction();
                    if (rs.next()) {
                        return new User(rs.getString("username"), rs.getString("password"));
                    }
                } catch (Exception e) {
                    throw new DataAccessException("Error finding user by username", e);
                }
                return null;
    }


    @Override
    public void saveUser(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving user", e);
        }
    }
}
