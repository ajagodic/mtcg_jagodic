package at.fhtw.app.persistence.repository;

import at.fhtw.app.model.Trade;

import java.util.List;

public interface TradingRepository {
    void createTradingOffer(Trade offer) throws Exception;
    List<Trade> getAllTradingOffers() throws Exception;
    void deleteTradingOffer(String id) throws Exception;
}
