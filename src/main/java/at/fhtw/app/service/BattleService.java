package at.fhtw.app.service;

import at.fhtw.app.model.Card;
import at.fhtw.app.model.User;
import at.fhtw.app.persistence.repository.UserRepository;
import java.util.*;

public class BattleService {
    private static final int MAX_ROUNDS = 100;
    private final UserRepository userRepository;

    public BattleService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String startBattle(User player1, User player2) {
        List<Card> deck1 = new ArrayList<>(player1.getDeck());
        List<Card> deck2 = new ArrayList<>(player2.getDeck());
        StringBuilder battleLog = new StringBuilder();

        battleLog.append("Battle Start: ").append(player1.getName()).append(" vs ").append(player2.getName()).append("\n");

        int round = 1;

        while (!deck1.isEmpty() && !deck2.isEmpty() && round <= MAX_ROUNDS) {
            Card card1 = deck1.get(new Random().nextInt(deck1.size()));
            Card card2 = deck2.get(new Random().nextInt(deck2.size()));

            battleLog.append("Round ").append(round).append(": ")
                    .append(card1.getName()).append(" (").append(card1.getDamage()).append(") vs ")
                    .append(card2.getName()).append(" (").append(card2.getDamage()).append(")\n");

            double damage1 = calculateDamage(card1, card2);
            double damage2 = calculateDamage(card2, card1);

            if (damage1 > damage2) {
                battleLog.append("Winner: ").append(card1.getName()).append("\n");
                deck2.remove(card2);
                deck1.add(card2);
            } else if (damage2 > damage1) {
                battleLog.append("Winner: ").append(card2.getName()).append("\n");
                deck1.remove(card1);
                deck2.add(card1);
            } else {
                battleLog.append("Draw!\n");
            }
            round++;
        }

        if (deck1.isEmpty()) {
            battleLog.append(player2.getName()).append(" wins the battle!\n");
            userRepository.updateEloWin(player2.getUsername());
            userRepository.updateWin(player2.getUsername());
            userRepository.updateEloLoss(player1.getUsername());
            userRepository.updateLoss(player1.getUsername());
        } else if (deck2.isEmpty()) {
            battleLog.append(player1.getName()).append(" wins the battle!\n");
            userRepository.updateEloLoss(player2.getUsername());
            userRepository.updateLoss(player2.getUsername());
            userRepository.updateEloWin(player1.getUsername());
            userRepository.updateWin(player1.getUsername());
        } else {
            battleLog.append("The battle is a draw!\n");
        }

        return battleLog.toString();
    }

    private static double calculateDamage(Card attacker, Card defender) {
        if (attacker.getCardType().equals("SPELL") && defender.getCardType().equals("MONSTER")) {
            return attacker.getDamage(); // No elemental effect for monster vs monster
        }

        if (attacker.getCardType().equals("SPELL") && defender.getCardType().equals("SPELL")) {
            return calculateElementalEffect(attacker, defender);
        }

        if (attacker.getCardType().equals("SPELL") && defender.getCardType().equals("SPELL")) {
            return calculateElementalEffect(attacker, defender);
        }

        // Handle special rules
        if (attacker.getName().equals("Goblin") && defender.getName().equals("Dragon")) {
            return 0; // Goblin can't attack Dragon
        }
        if (attacker.getName().equals("Wizard") && defender.getName().equals("Ork")) {
            return 0; // Wizard controls Ork
        }
        if (attacker.getCardType().equals("SPELL") && defender.getName().equals("Kraken")) {
            return 0; // Kraken immune to spells
        }
        if (attacker.getCardType().equals("SPELL") && defender.getName().equals("Knight")) {
            return Double.MAX_VALUE; // Knight drowned by water spell
        }
        if (attacker.getName().equals("FireElf") && defender.getName().equals("Dragon")) {
            return 0; // FireElf evades Dragon
        }

        return attacker.getDamage();
    }

    private static double calculateElementalEffect(Card attacker, Card defender) {
        String attackerElement = attacker.getCardType();
        String defenderElement = defender.getCardType();

        if (attackerElement.equals("Water") && defenderElement.equals("Fire") ||
                attackerElement.equals("Fire") && defenderElement.equals("Normal") ||
                attackerElement.equals("Normal") && defenderElement.equals("Water")) {
            return attacker.getDamage() * 2; // Effective
        }

        if (attackerElement.equals("Fire") && defenderElement.equals("Water") ||
                attackerElement.equals("Normal") && defenderElement.equals("Fire") ||
                attackerElement.equals("Water") && defenderElement.equals("Normal")) {
            return attacker.getDamage() / 2; // Not effective
        }

        return attacker.getDamage(); // Neutral
    }
}

