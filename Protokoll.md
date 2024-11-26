# MTCG

## Protokoll über Implementierung der Anforderung für Intermediate Hand-In 1

Github: https://github.com/ajagodic/mtcg_jagodic<br>
Der SQL Script für das Erstellen von den benötigten Daten innerhalb einer Postgres DB, finden Sie im .sql-File

## Implementierung
### Erstellte Klassen: 
<ul>
<li>UserRepositoryImpl</li>
<li>LoginController</li>
<li>UserController</li>
<li>Card</li>
<li>User</li>
<li>UserService</li>
</ul>

### Erstellte Interfaces:
<li>UserRepository</li>

### Registrierung

Endpoint: POST /users <br>
Der Benutzer übermittelt einen Registrierungspost mit einem eindeutigen Benutzernamen und Passwort.
Überprüfung in der UserRepository, ob der Benutzername bereits existiert.
<br>Wenn ja: Rückgabe eines HTTP 409 (Conflict), Benutzer existiert bereits.
<br>Wenn nein: Erstellung eines neuen Benutzers und Speicherung in der Datenbank (Transaktion über UnitOfWork).
<br>Erfolgreiche Registrierung gibt HTTP 201 (Created) zurück.

### Login
Endpoint POST /sessions <br>
Der Benutzer übermittelt Login-Daten.
Die UserRepository sucht nach einem Benutzer mit übereinstimmendem Benutzernamen.
<br>Wenn Benutzer nicht existiert: Rückgabe eines HTTP 401 (Unauthorized).
<br>Wenn Passwort falsch: Rückgabe eines HTTP 401 (Unauthorized).
<br>Wenn Benutzername und Passwort stimmen: Erzeugung eines Token basierend auf dem Benutzernamen ({username}-mtcgToken).
<br>Erfolgreiches Login gibt HTTP 200 (OK) zurück und sendet den Token im Response-Body.

#### Fehlerbehandlung:
<br>Registrierung:
<br>Fehler bei Datenbankoperationen: HTTP 500 (Internal Server Error).
<br>Fehlende Felder im Payload: HTTP 400 (Bad Request).
<br>Login:
<br>Benutzername oder Passwort fehlen: HTTP 400 (Bad Request).
<br>Datenbankverbindung schlägt fehl: HTTP 500 (Internal Server Error).
