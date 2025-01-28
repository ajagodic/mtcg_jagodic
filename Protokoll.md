# Anwendungsdokumentation

## App-Design

### Designentscheidungen

Die App verwendet eine modulare Struktur mit klarer Trennung der Verantwortlichkeiten. Wichtige Designentscheidungen umfassen:

- **Controller**: Bearbeiten HTTP-Anfragen und leiten sie an Service-Methoden weiter.
- **Services**: Enthalten Geschäftslogik und interagieren mit Repositories für Datenpersistenz.
- **Repositories**: Verwalten Datenbankoperationen und setzen das DAO-Muster um.
- **Modelle**: Repräsentieren die Kerneinheiten der Anwendung (z. B. `User`, `Card`, `Deck`).

### Struktur der Anwendung

Die Anwendung ist in folgende Pakete unterteilt:

- \`\`: Enthält Kernmodelle wie `User`, `Card`, `Deck` und `Trade`.
- \`\`: Stellt Services wie `UserService`, `DeckService` und `TradingService` bereit.
- \`\`: Verarbeitet Datenzugriff über Repositories, einschließlich `UserRepositoryImpl` und `PackageRepositoryImpl`.
- \`\`: Implementiert den HTTP-Server, das Routing von Anfragen und die Bearbeitung von Antworten.

### Klassendiagramm

```plaintext
+------------------+        +------------------+        +--------------------+
|    Controller    |<------>|     Service      |<------>|    Repository      |
+------------------+        +------------------+        +--------------------+
| UserController   |        | UserService      |        | UserRepositoryImpl |
| DeckController   |        | DeckService      |        | PackageRepository  |
| ...              |        | ...              |        | ...                |
+------------------+        +------------------+        +--------------------+
```

## Gelerntes

1. **Fehlerbehandlung**: Die Robustheit wurde durch die Implementierung detaillierter HTTP-Statuscodes für verschiedene Fehlerszenarien verbessert (z. B. `409 Conflict`, `500 Internal Server Error`).
2. **Datenbank-Transaktionen**: Atomarität für kritische Operationen wie die Benutzerregistrierung sichergestellt.
3. **Testgetriebene Entwicklung**: Unit-Tests mit JUnit und Mockito waren essenziell zur Überprüfung der Geschäftslogik und der Datenintegrität.
4. **Herausforderungen mit Docker**: Probleme mit der Docker-basierten Datenbankeinrichtung führten zum Wechsel auf lokale Datenbankkonfigurationen.

## Entscheidungen zum Unit-Testing

- **Verwendete Frameworks**: JUnit 5 und Mockito.
- **Testziele**: Fokus auf die Prüfung der Geschäftslogik in den Services und der Datenintegrität in den Repositories.
- **Mocking**: Mockito wurde verwendet, um Datenbankinteraktionen zu simulieren und die Abhängigkeit von der tatsächlichen Datenbank während der Tests zu vermeiden.
- **Beispiel-Testfälle**:
    - Szenarien für gültiges und ungültiges Benutzer-Login.
    - Logik für das Erstellen von Decks und die Zuordnung von Karten.
    - Operationen des Trading-Services.

## Einzigartiges Feature

Die App implementiert ein **tokenbasiertes Authentifizierungssystem**, das Sitzungstokens während des Logins generiert. Diese Tokens werden bei jeder Anfrage validiert, um einen sicheren Zugriff auf benutzerspezifische Ressourcen zu gewährleisten.

### Beispiel:

- Login generiert ein Token: `{username}-mtcgToken`.
- Tokens werden im `Authorization`-Header für nachfolgende Anfragen gesendet.

## Aufgewendete Zeit

| Aufgabe                               | Zeitaufwand    |
| ------------------------------------- | -------------- |
| Initiales Projektsetup                | 4 Stunden      |
| Datenbankdesign und Implementierung   | 6 Stunden      |
| API-Entwicklung (Registrierung/Login) | 8 Stunden      |
| Unit-Testing                          | 5 Stunden      |
| Debugging und Fehlerbehebung          | 4 Stunden      |
| Dokumentation                         | 3 Stunden      |
| **Gesamt**                            | **30 Stunden** |

