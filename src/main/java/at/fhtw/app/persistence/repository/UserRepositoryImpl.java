package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.*;
import at.fhtw.app.model.Package;
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
            statement.setString(1, username + "mtcgToken");
            statement.setString(2, username);
            int rowsUpdated = statement.executeUpdate();
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

    //POST
    @Override
    public boolean editUsername(String username, String newUsername) {
        String sql = "UPDATE users SET username = ? WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, username);
            int rowsUpdated = stmt.executeUpdate();
            unitOfWork.commitTransaction();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error editing username", e);
        }
    }

    //POST
    @Override
    public boolean editPassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            int rowsUpdated = stmt.executeUpdate();
            unitOfWork.commitTransaction();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error editing password", e);
        }
    }

    //GET
    @Override
    public String showStats(String username) {
        String sql = "SELECT elo,token FROM users WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "ELO: " + rs.getInt("elo") + "Token: " + rs.getString("token");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error showing stats", e);
        }
        return "No stats available for user.";
    }

    @Override
    public int getCoins(String username) {
        String sql = "SELECT token FROM users WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("coins");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching coins", e);
        }
        return 0;
    }
    @Override
    public void updateCoins(String username, int newCoinValue) {
        String sql = "UPDATE users SET coins = ? WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setInt(1, newCoinValue);
            stmt.setString(2, username);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error updating coins for user: " + username, e);
        }
    }
    @Override
    public void addPackageToUser(String username, Package packageToAdd) {
        String sql = "INSERT INTO user_packages (username, package_id) VALUES (?, ?)";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username); // Benutzername setzen
            stmt.setString(2, packageToAdd.getId());// Paket-ID setzen
            stmt.executeUpdate(); // Eintrag in der Datenbank erstellen
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error assigning package to user: " + username, e);
        }
    }


}
