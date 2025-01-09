package at.fhtw.app.service;

import at.fhtw.app.model.Trade;
import at.fhtw.app.persistence.repository.TradingRepository;

import java.util.List;

public class TradingService {
    private final TradingRepository tradingRepository;

    public TradingService(TradingRepository tradingRepository) {
        this.tradingRepository = tradingRepository;
    }

    public void createTradingOffer(Trade offer) throws Exception {
        tradingRepository.createTradingOffer(offer);
    }

    public List<Trade> getAllTradingOffers() throws Exception {
        return tradingRepository.getAllTradingOffers();
    }

    public void deleteTradingOffer(String offerId, String username) throws Exception {
        // Sicherstellen, dass der Benutzer der Besitzer des Angebots ist
        List<Trade> offers = tradingRepository.getAllTradingOffers();
        Trade offer = offers.stream()
                .filter(o -> o.getId().equals(offerId) && o.getOwner().equals(username))
                .findFirst()
                .orElseThrow(() -> new Exception("Offer not found or not owned by user"));

        tradingRepository.deleteTradingOffer(offerId);
    }

    public void acceptTradingOffer(String offerId, String status) throws Exception {
        List<Trade> offers = tradingRepository.getAllTradingOffers();
        Trade offer = offers.stream()
                .filter(o -> o.getId().equals(offerId))
                .findFirst()
                .orElseThrow(() -> new Exception("Offer not found"));

        if (!status.equals(offer.getStatus())) {
            throw new Exception("Card does not meet the requirements");
        }

        // Tausch durchführen (Karten zwischen Benutzern tauschen)
        // Logik zum Aktualisieren der Kartenbesitz-Zuordnungen
    }
}
