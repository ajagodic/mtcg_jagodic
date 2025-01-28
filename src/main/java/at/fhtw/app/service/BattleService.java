package at.fhtw.app.service;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.User;
import at.fhtw.app.persistence.UnitOfWork;
import at.fhtw.app.persistence.repository.UserRepository;
import at.fhtw.app.persistence.repository.UserRepositoryImpl;

import java.util.*;

public class BattleService {
    private static final int MAX_ROUNDS = 100;
    private final UserRepository userRepository;
    private final DeckService deckService; // 🔥 DeckService hinzufügen

    public BattleService(DeckService deckService) {
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
        this.deckService = deckService; // 🔥 DeckService speichern
    }

    public String startBattle(User player1, User player2) {
        // 🔥 Decks mit deckService abrufen
        List<Card> deck1 = deckService.getDeckByUsername(player1.getUsername(),true);
        List<Card> deck2 = deckService.getDeckByUsername(player2.getUsername(),true);

        if (deck1 == null || deck2 == null) {
            return "Error: One or both players have no deck!";
        }
        if (deck1.isEmpty() || deck2.isEmpty()) {
            return "Error: One or both players have an empty deck!";
        }

        StringBuilder battleLog = new StringBuilder();
        battleLog.append("Battle Start: ").append(player1.getUsername()).append(" vs ").append(player2.getUsername()).append(System.lineSeparator());

        int round = 1;
        Random random = new Random();

        while (!deck1.isEmpty() && !deck2.isEmpty() && round <= MAX_ROUNDS) {
            battleLog.append(" Round ").append(round).append(System.lineSeparator());

            Card card1 = deck1.get(random.nextInt(deck1.size()));
            Card card2 = deck2.get(random.nextInt(deck2.size()));

            battleLog.append(player1.getUsername()).append(" plays ").append(card1.getName())
                    .append(" (").append(card1.getDamage()).append(" damage)").append(System.lineSeparator());
            battleLog.append(player2.getUsername()).append(" plays ").append(card2.getName())
                    .append(" (").append(card2.getDamage()).append(" damage)").append(System.lineSeparator());

            double card1Damage = calculateEffectiveDamage(card1, card2);
            double card2Damage = calculateEffectiveDamage(card2, card1);

            if (card1Damage > card2Damage) {
                battleLog.append(card1.getName()).append(" wins the round!").append(System.lineSeparator());
                deck2.remove(card2);
                deck1.add(card2);
            } else if (card2Damage > card1Damage) {
                battleLog.append(card2.getName()).append(" wins the round!").append(System.lineSeparator());
                deck1.remove(card1);
                deck2.add(card1);
            } else {
                battleLog.append("It's a draw!").append(System.lineSeparator());
            }

            round++;
        }

        if (deck1.isEmpty() && deck2.isEmpty()) {
            battleLog.append("Battle ends in a draw! No cards left.").append(System.lineSeparator());
        } else if (deck1.isEmpty()) {
            battleLog.append(player2.getUsername()).append(" wins the battle!").append(System.lineSeparator());
            updateELO(player2, player1);
        } else {
            battleLog.append(player1.getUsername()).append(" wins the battle!").append(System.lineSeparator());
            updateELO(player1, player2);
        }

        return battleLog.toString();
    }

    private double calculateEffectiveDamage(Card attacker, Card defender) {
        if (attacker.getType().equals("Spell") && defender.getType().equals("Spell")) {
            switch (attacker.getElement().name()) {
                case "Water":
                    if (defender.getElement().name().equals("Fire")) return attacker.getDamage() * 2;
                    if (defender.getElement().name().equals("Normal")) return attacker.getDamage() / 2;
                    break;
                case "Fire":
                    if (defender.getElement().name().equals("Normal")) return attacker.getDamage() * 2;
                    if (defender.getElement().name().equals("Water")) return attacker.getDamage() / 2;
                    break;
                case "Normal":
                    if (defender.getElement().name().equals("Water")) return attacker.getDamage() * 2;
                    if (defender.getElement().name().equals("Fire")) return attacker.getDamage() / 2;
                    break;
            }
        }

        if (attacker.getName().equals("Kraken") && defender.getType().equals("Spell")) return attacker.getDamage();
        if (attacker.getName().equals("FireElf") && defender.getName().equals("Dragon")) return attacker.getDamage();
        if (attacker.getName().equals("Knight") && defender.getName().equals("WaterSpell")) return 0;

        return attacker.getDamage();
    }

    private void updateELO(User winner, User loser) {
        /**winner.setElo(winner.getElo() + 3);
        loser.setElo(loser.getElo() - 5);
        winner.setWins(winner.getWins() + 1);
        loser.setLosses(loser.getLosses() + 1);*/
        //userRepository.updateWin(winner.getUsername());
        //userRepository.updateLoss(loser.getUsername());
        userRepository.updateEloLoss(loser.getUsername());
        userRepository.updateEloWin(winner.getUsername());
        userRepository.uniqueFeature(winner.getUsername(), loser.getUsername());

    }
}
