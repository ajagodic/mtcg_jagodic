package at.fhtw.app.persistence;

import at.fhtw.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private final List<User> userData;

    public UserDao() {
        // Simulierte Datenbank mit einigen Testnutzern
        this.userData = new ArrayList<>();
    }

    // Benutzer anhand des Benutzernamens finden
    public User findByUsername(String username) {
        return userData.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // Benutzer speichern
    public void saveUser(User user) {
        userData.add(user);
    }

    // Benutzer löschen
    public boolean deleteUser(String username) {
        return userData.removeIf(user -> user.getUsername().equals(username));
    }

    // Alle Benutzer zurückgeben (z. B. für Debugging oder Statistiken)
    public List<User> getAllUsers() {
        return new ArrayList<>(userData);
    }
}
