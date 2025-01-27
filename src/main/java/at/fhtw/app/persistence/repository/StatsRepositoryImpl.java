package at.fhtw.app.persistence.repository;

import at.fhtw.app.persistence.DataAccessException;
import at.fhtw.app.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatsRepositoryImpl implements StatsRepository {
    private final UnitOfWork unitOfWork;

    public StatsRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void displayStats(String username) throws Exception {
        String sql = "SELECT elo,wins,losses FROM users WHERE username= ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error assigning package to user: " + username, e);
        }
    }
}
