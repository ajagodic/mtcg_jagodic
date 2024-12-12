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
        String sql = "UPDATE users SET token = ? WHERE username = ?";

        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, username + "mtcgToken");       // Setze den neuen Token-Wert
            statement.setString(2, username);   // Setze den Username zur Übereinstimmung
            int rowsUpdated = statement.executeUpdate(); // Führt das Update aus und gibt die betroffenen Zeilen zurück

            if (rowsUpdated > 0) {
                System.out.println("Token erfolgreich aktualisiert.");
            } else {
                System.out.println("Kein Benutzer mit dem angegebenen Username gefunden.");
            }

            unitOfWork.commitTransaction(); // Transaktion bestätigen
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction(); // Transaktion bei Fehler zurückrollen
            e.printStackTrace();
        }
        return null; // Benutzer nicht gefunden
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

    @Override
    public boolean editUsername(String username, String newUsername) {
        String sql = "UPDATE users SET username = ? WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, username);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            throw new DataAccessException("Error editing username", e);
        }
        return false;
    }

    @Override
    public boolean editPassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            throw new DataAccessException("Error editing password", e);
        }
        return false;
    }

    @Override
    public String showStats(String username) {
        String sql = "SELECT elo FROM users WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            throw new DataAccessException("Error showing stats", e);
        }
        return null;
    }


}
