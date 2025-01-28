package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.*;
import at.fhtw.app.model.Package;
import at.fhtw.app.persistence.DataAccessException;
import at.fhtw.app.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private UnitOfWork unitOfWork;

    public UserRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public User findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Error: Username cannot be empty or null.");
        }

        // Schritt 1: Token aktualisieren
        String sqlUpdateToken = "UPDATE users SET token = ? WHERE username = ?";
        try (PreparedStatement updateStmt = unitOfWork.prepareStatement(sqlUpdateToken)) {
            updateStmt.setString(1, username + "-mtcgToken");
            updateStmt.setString(2, username);
            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated == 0) {
                unitOfWork.rollbackTransaction(); // Rollback bei Fehler
                return null; // Benutzer nicht gefunden
            }
            unitOfWork.commitTransaction(); // Transaktion bestätigen
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Error updating token for user: " + username, e);
        }

        // Schritt 2: Benutzer abrufen
        String sqlSelectUser = "SELECT username, password FROM users WHERE username = ?";
        try (PreparedStatement selectStmt = unitOfWork.prepareStatement(sqlSelectUser)) {
            selectStmt.setString(1, username);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getString("username"),
                            resultSet.getString("password")
                    );
                }
            }
            unitOfWork.commitTransaction(); // Transaktion bestätigen
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Error fetching user by username: " + username, e);
        }

        return null; // Benutzer nicht gefunden
    }


    @Override
    public boolean checkUserExists(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                System.out.println("User vorhanden");
                return true;
            } else {
                System.out.println("User fehlt");
            }
            unitOfWork.commitTransaction(); // Transaktion bestätigen
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction(); // Transaktion bei Fehler zurückrollen
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getUserData(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement statement = unitOfWork.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return "ELO: " + rs.getInt("elo") + " Wins: " + rs.getString("wins") + " Losses: " + rs.getString("losses") + " Coins: " + rs.getString("coins") + " Bio: " + rs.getString("bio") + " Name: " + rs.getString("name");
                }
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
    //name
    @Override
    public boolean editUserData(User user) {
        String sql = "UPDATE users SET bio = ?, name = ?, image = ? WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, user.getBio());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getImage());
            stmt.setString(4, user.getUsername());

            int rowsUpdated = stmt.executeUpdate();
            unitOfWork.commitTransaction();

            return rowsUpdated > 0; // Erfolgreich, wenn mindestens eine Zeile aktualisiert wurde
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error updating user data for: " + user.getUsername(), e);
        }
    }


    //GET
    @Override
    public String showStats(String username) {
        String sql = "SELECT elo, COALESCE(wins, 0) AS wins, COALESCE(losses, 0) AS losses, coins FROM users WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int elo = rs.getInt("elo");
                    int wins = rs.getInt("wins");
                    int losses = rs.getInt("losses");
                    int coins = rs.getInt("coins");

                    // Debugging-Log (Kann später entfernt werden)
                    System.out.println("Stats for " + username + ": ELO=" + elo + ", Wins=" + wins + ", Losses=" + losses + ", Coins=" + coins);

                    return "{"+username+"\":  Elo\": " + elo + ", \"Wins\": " + wins + ", \"Losses\": " + losses + " } , \"Coins\": " + coins + " } ";
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error showing stats for user: " + username, e);
        }
        return "{ \"message\": \"No stats available for user.\" }";
    }


    @Override
    public int getCoins(String username) {
        String sql = "SELECT coins FROM users WHERE username = ?";
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
            stmt.setInt(2, packageToAdd.getId());// Paket-ID setzen
            stmt.executeUpdate(); // Eintrag in der Datenbank erstellen
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error assigning package to user: " + username, e);
        }
    }

    @Override
    public void updateEloWin(String username) {
        String sql = "UPDATE users SET elo = elo + 3, wins = wins + 1 WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate(); // Eintrag in der Datenbank erstellen
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error assigning package to user: " + username, e);
        }
    }

    @Override
    public void updateEloLoss(String username) {
        String sql = "UPDATE users SET elo = elo - 5, losses = losses + 1 WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate(); // Eintrag in der Datenbank erstellen
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error assigning package to user: " + username, e);
        }
    }
    @Override
    public void uniqueFeature(String winner, String loser) {
        String sqlWin = "UPDATE users SET coins = coins + 5 WHERE username = ?";

        try {

            // Gewinner bekommt +5 Coins
            try (PreparedStatement stmt = unitOfWork.prepareStatement(sqlWin)) {
                stmt.setString(1, winner);
                stmt.executeUpdate();
            }
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            // Falls ein Fehler auftritt, ALLE Änderungen rückgängig machen (Rollback)
            unitOfWork.rollbackTransaction();
            throw new RuntimeException("Error updating coins for winner and loser: " + e.getMessage(), e);
        }
    }
    @Override
    public List<String> displayScoreboard() throws Exception {
        String sql = "SELECT username, elo, coins, wins, losses " +
                "FROM users " +
                "ORDER BY elo DESC";
        List<String> scoreboard = new ArrayList<>();
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    scoreboard.add(String.format(
                            "%s | ELO: %d | Coins: %d | Won: %d | Lost: %d",
                            rs.getString("username"),
                            rs.getInt("elo"),
                            rs.getInt("coins"),
                            rs.getInt("wins"),
                            rs.getInt("losses")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching scoreboard", e);
        }
        return scoreboard;
    }


}
