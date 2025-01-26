package at.fhtw.app.persistence.repository;

public interface BatteRepository {
    void updateEloWin(String username);
    void updateEloLoss(String username);
    void updateWin(String username);
    void updateLoss(String username);
}
