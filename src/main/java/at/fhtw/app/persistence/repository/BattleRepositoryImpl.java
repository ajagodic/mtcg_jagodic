package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.*;
import at.fhtw.app.model.Package;
import at.fhtw.app.persistence.DataAccessException;
import at.fhtw.app.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BattleRepositoryImpl implements BatteRepository {

    private UnitOfWork unitOfWork;

    public BattleRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void updateEloWin(String username) {
        String sql = "UPDATE users SET elo = elo + 3 WHERE username = ?";
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
        String sql = "UPDATE users SET elo = elo - 5 WHERE username = ?";
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
    public void updateWin(String username) {
        String sql = "UPDATE users SET wins = wins + 1 WHERE username = ?";
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
    public void updateLoss(String username) {
        String sql = "UPDATE users SET losses = losses + 1 WHERE username = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate(); // Eintrag in der Datenbank erstellen
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error assigning package to user: " + username, e);
        }
    }
}
