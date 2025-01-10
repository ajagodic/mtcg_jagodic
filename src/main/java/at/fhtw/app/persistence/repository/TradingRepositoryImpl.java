package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.Trade;
import at.fhtw.app.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TradingRepositoryImpl implements TradingRepository {
    private final UnitOfWork unitOfWork;

    public TradingRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public void createTradingOffer(Trade offer) throws Exception {
        String sql = "INSERT INTO trades (id, card, owner, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, offer.getId());
            stmt.setString(2, offer.getCardToTrade());
            stmt.setString(3, offer.getOwner());
            stmt.setString(4, String.valueOf(offer.getStatus()));
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new Exception("Error creating trading offer", e);
        }
    }

    public List<Trade> getAllTradingOffers() throws Exception {
        String sql = "SELECT * FROM trades";
        List<Trade> offers = new ArrayList<>();
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                offers.add(new Trade(
                        rs.getString("id"),
                        rs.getString("card"),
                        rs.getString("owner"),
                        rs.getInt("status")
                ));
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching trading offers", e);
        }
        return offers;
    }

    public void deleteTradingOffer(String offerId) throws Exception {
        String sql = "DELETE FROM trades WHERE id = ?";
        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, offerId);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new Exception("Error deleting trading offer", e);
        }
    }
}
