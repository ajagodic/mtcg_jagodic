package at.fhtw.app.service;
import at.fhtw.app.controller.UserController;
import at.fhtw.app.model.User;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.HttpMethod;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {



    private final UserService userService = new UserService();
    private final UserController userController = new UserController(userService);

    // 1. Doppelregistrierung
    @Test
    void testRegisterDuplicateUser() {
        boolean bool = userService.registerUser(new User("kienboec", "password123"));
        assertFalse(bool, "User sollte nicht registriert werden.");
    }

    // 2. Login erfolgreich
    @Test
    void testLoginSuccess() {
        userService.registerUser(new User("kienboec", "password123"));
        String token = userService.loginUser("kienboec", "password123");
        assertNotNull(token, "Login sollte ein Token zurückgeben.");
    }

    // 3. Login mit falschem Passwort
    @Test
    void testLoginInvalidCredentials() {
        userService.registerUser(new User("kienboec", "password123"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser("kienboec", "wrongPassword");
        });
        assertEquals("Invalid credentials", exception.getMessage());
    }

    // 4. Login nicht existierender Benutzer
    @Test
    void testLoginNonExistentUser() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser("unknownUser", "password123");
        });
        assertEquals("User not found", exception.getMessage());
    }

    // 5. Token-Generierung nach Login
    @Test
    void testTokenGenerationAfterLogin() {
        userService.registerUser(new User("kienboec", "password123"));
        String token = userService.loginUser("kienboec", "password123");
        assertEquals("kienboec-mtcgToken", token, "Der generierte Token sollte korrekt sein.");
    }


    // 6. Benutzer mit leerem Passwort registrieren
    @Test
    void testRegisterUserWithEmptyPassword() {
        boolean bool = userService.registerUser(new User("kienboec", ""));

        assertFalse(bool, "Password leer");
    }

    // 7. Benutzerprofil bearbeiten
    @Test
    void testEditUserProfileSuccess() {
        User user = new User("kienboec", "password123");
        userService.registerUser(user);

        user.setPassword("newPassword");
        boolean result = userService.editUser(user);
        assertTrue(result, "Das Benutzerprofil sollte erfolgreich bearbeitet werden.");
    }

    // 8. Benutzerprofil bearbeiten - Fehler bei null-User
    @Test
    void testEditUserProfileWithNullUser() {
        boolean result = userService.editUser(null);
        assertFalse(result, "Das Bearbeiten eines null-Users sollte fehlschlagen.");
    }

    // 9. Benutzerstatistiken anzeigen - Erfolgreich
    @Test
    void testDisplayStatsSuccess() {
        User user = new User("kienboec", "password123");
        userService.registerUser(user);

        String stats = userService.displayStats("kienboec");
        assertNotNull(stats, "Statistiken sollten für existierende Benutzer angezeigt werden.");
    }

    // 10. Benutzerstatistiken anzeigen - Benutzer existiert nicht
    @Test
    void testDisplayStatsForNonExistentUser() {
        String stats = userService.displayStats("unknownUser");
        assertEquals("false", stats, "Statistiken sollten für nicht existierende Benutzer nicht verfügbar sein.");
    }

    // 11. Benutzer suchen - Erfolgreich
    @Test
    void testFindUserByUsernameSuccess() {
        User user = new User("kienboec", "password123");
        userService.registerUser(user);

        User foundUser = userService.findUserbyUsername("kienboec");
        assertNotNull(foundUser, "Benutzer sollte gefunden werden.");
        assertEquals("kienboec", foundUser.getUsername(), "Benutzername sollte korrekt sein.");
    }

    // 12. Benutzer suchen - Benutzer existiert nicht
    @Test
    void testFindUserByUsernameNonExistent() {
        User foundUser = userService.findUserbyUsername("unknownUser");
        assertNull(foundUser, "Nicht existierende Benutzer sollten null zurückgeben.");
    }

    // 13. Benutzerdaten abrufen - Erfolgreich
    @Test
    void testGetUserDataSuccess() {
        User user = new User("kienboec", "password123");
        userService.registerUser(user);

        String userData = userService.getUSerData("kienboec");
        assertNotNull(userData, "Benutzerdaten sollten für existierende Benutzer verfügbar sein.");
    }

    // 14. Benutzerdaten abrufen - Benutzer existiert nicht
    @Test
    void testGetUserDataForNonExistentUser() {
        String userData = userService.getUSerData("unknownUser");
        assertEquals("false", userData, "Für nicht existierende Benutzer sollten keine Daten zurückgegeben werden.");
    }

    // 15. Benutzerdaten aktualisieren - Erfolgreich
    @Test
    void testUpdateUserDataSuccess() {
        User user = new User("kienboec", "password123");
        userService.registerUser(user);

        user.setPassword("newPassword");
        boolean result = userService.updateUserData(user);
        assertTrue(result, "Benutzerdaten sollten erfolgreich aktualisiert werden.");
    }

    // 16. Benutzerdaten aktualisieren - Benutzer existiert nicht
    @Test
    void testUpdateUserDataForNonExistentUser() {
        User user = new User("unknownUser", "password123");
        boolean result = userService.updateUserData(user);
        assertFalse(result, "Für nicht existierende Benutzer sollten keine Daten aktualisiert werden.");
    }

    // 17. Scoreboard anzeigen - Erfolgreich
    @Test
    void testShowScoreboardSuccess() {
        // Mock-Daten für das Scoreboard
        String scoreboard = userService.showScoreboard();
        assertNotNull(scoreboard, "Das Scoreboard sollte nicht null sein.");
    }

    // 18. Token-Authentifizierung - Erfolgreich
    @Test
    void testCheckAuthSuccess() {
        boolean result = UserService.checkAuth("kienboec", "kienboec-mtcgToken");
        assertTrue(result, "Die Token-Authentifizierung sollte erfolgreich sein.");
    }

    // 19. Token-Authentifizierung - Fehlerhaft
    @Test
    void testCheckAuthFailure() {
        boolean result = UserService.checkAuth("kienboec", "invalidToken");
        assertFalse(result, "Die Token-Authentifizierung sollte fehlschlagen.");
    }

    // 20. Token-Authentifizierung - Null-Token
    @Test
    void testCheckAuthWithNullToken() {
        boolean result = UserService.checkAuth("kienboec", null);
        assertFalse(result, "Ein null-Token sollte als ungültig erkannt werden.");
    }

}

