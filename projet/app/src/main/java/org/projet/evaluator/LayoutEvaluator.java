package org.projet.evaluator;

import org.projet.model.KeyboardLayout;
import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

/**
 * Évalue les dispositions de clavier selon différents critères ergonomiques.
 */
public class LayoutEvaluator {
    private final Map<String, Long> ngramFrequencies;
    private final MovementEvaluator movementEvaluator;
    private final Map<MovementType, Double> weights;
    private final Map<MovementType, Long> movementCounts;
    private long totalBigramCount;
    private long totalTrigramCount;
    private Map<KeyboardLayout.Finger, Double> fingerLoads;
    
    public LayoutEvaluator(Map<String, Long> ngramFrequencies) {
        this.ngramFrequencies = Map.copyOf(ngramFrequencies);
        this.movementEvaluator = new MovementEvaluator();
        this.weights = initializeWeights();
        this.movementCounts = new EnumMap<>(MovementType.class);
        this.fingerLoads = new EnumMap<>(KeyboardLayout.Finger.class);
        for (MovementType type : MovementType.values()) {
            movementCounts.put(type, 0L);
        }
        for (KeyboardLayout.Finger finger : KeyboardLayout.Finger.values()) {
            fingerLoads.put(finger, 0.0);
        }
        this.totalBigramCount = 0;
        this.totalTrigramCount = 0;
    }
    
    private Map<MovementType, Double> initializeWeights() {
        Map<MovementType, Double> weights = new EnumMap<>(MovementType.class);
        
        // Pénalités pour les mouvements indésirables
        weights.put(MovementType.SAME_FINGER, 2.0);        // Forte pénalité pour même doigt
        weights.put(MovementType.LATERAL_STRETCH, 1.5);    // Pénalité pour extension
        weights.put(MovementType.SCISSORS, 1.8);           // Pénalité pour ciseaux
        weights.put(MovementType.BAD_REDIRECTION, 1.7);    // Pénalité pour mauvaise redirection
        weights.put(MovementType.REDIRECTION, 1.2);        // Pénalité légère pour redirection
        weights.put(MovementType.SAME_FINGER_SKIPGRAM, 1.6); // Pénalité pour skipgram
        
        // Bonus pour les bons mouvements
        weights.put(MovementType.HAND_ALTERNATION, -0.8);  // Bonus pour alternance
        weights.put(MovementType.INWARD_ROLL, -1.0);       // Bonus pour roulement intérieur
        weights.put(MovementType.OUTWARD_ROLL, -0.5);      // Petit bonus pour roulement extérieur
        
        return weights;
    }
    
    /**
     * Retourne les statistiques de mouvements après l'évaluation.
     */
    public Map<MovementType, Long> getMovementCounts() {
        return Map.copyOf(movementCounts);
    }
    
    /**
     * Retourne les charges de chaque doigt après l'évaluation.
     */
    public Map<KeyboardLayout.Finger, Double> getFingerLoads() {
        return Map.copyOf(fingerLoads);
    }
    
    /**
     * Évalue une disposition de clavier et retourne un score.
     * Plus le score est bas, meilleure est la disposition.
     */
    public double evaluateLayout(KeyboardLayout layout) {
        // Réinitialiser les compteurs
        movementCounts.clear();
        fingerLoads.clear();
        for (MovementType type : MovementType.values()) {
            movementCounts.put(type, 0L);
        }
        for (KeyboardLayout.Finger finger : KeyboardLayout.Finger.values()) {
            fingerLoads.put(finger, 0.0);
        }
        totalBigramCount = 0;
        totalTrigramCount = 0;
        
        double score = 0.0;
        
        // Évaluer les bigrammes et trigrammes
        for (var entry : ngramFrequencies.entrySet()) {
            String ngram = entry.getKey();
            long frequency = entry.getValue();
            
            if (ngram.length() == 2) {
                totalBigramCount += frequency;
                score += evaluateBigram(layout, ngram, frequency);
            }
            else if (ngram.length() == 3) {
                totalTrigramCount += frequency;
                score += evaluateTrigram(layout, ngram, frequency);
            }
            else if (ngram.length() == 1) {
                // Mettre à jour les charges des doigts
                char c = ngram.charAt(0);
                KeyboardLayout.Key key = layout.characterToKeyMap().get(c);
                if (key != null) {
                    fingerLoads.merge(key.finger(), 
                        (frequency * 100.0) / ngramFrequencies.values().stream().mapToLong(Long::longValue).sum(),
                        Double::sum);
                }
            }
        }
        
        return score;
    }
    
    private double evaluateBigram(KeyboardLayout layout, String bigram, long frequency) {
        if (bigram.length() != 2) return 0.0;
        
        char c1 = bigram.charAt(0);
        char c2 = bigram.charAt(1);
        
        KeyboardLayout.Key key1 = layout.characterToKeyMap().get(c1);
        KeyboardLayout.Key key2 = layout.characterToKeyMap().get(c2);
        
        if (key1 == null || key2 == null) return 0.0;
        
        double score = 0.0;
        
        // Vérifier les différents types de mouvements
        if (movementEvaluator.isSameFinger(key1, key2)) {
            score += weights.get(MovementType.SAME_FINGER);
            movementCounts.merge(MovementType.SAME_FINGER, frequency, Long::sum);
        }
        
        if (movementEvaluator.isLateralStretch(key1, key2)) {
            score += weights.get(MovementType.LATERAL_STRETCH);
            movementCounts.merge(MovementType.LATERAL_STRETCH, frequency, Long::sum);
        }
        
        if (movementEvaluator.isScissors(key1, key2)) {
            score += weights.get(MovementType.SCISSORS);
            movementCounts.merge(MovementType.SCISSORS, frequency, Long::sum);
        }
        
        if (!movementEvaluator.isSameHand(key1, key2)) {
            score += weights.get(MovementType.HAND_ALTERNATION);
            movementCounts.merge(MovementType.HAND_ALTERNATION, frequency, Long::sum);
        }
        
        if (movementEvaluator.isInwardRoll(key1, key2)) {
            score += weights.get(MovementType.INWARD_ROLL);
            movementCounts.merge(MovementType.INWARD_ROLL, frequency, Long::sum);
        }
        
        return score * frequency;
    }
    
    private double evaluateTrigram(KeyboardLayout layout, String trigram, long frequency) {
        if (trigram.length() != 3) return 0.0;
        
        char c1 = trigram.charAt(0);
        char c2 = trigram.charAt(1);
        char c3 = trigram.charAt(2);
        
        KeyboardLayout.Key key1 = layout.characterToKeyMap().get(c1);
        KeyboardLayout.Key key2 = layout.characterToKeyMap().get(c2);
        KeyboardLayout.Key key3 = layout.characterToKeyMap().get(c3);
        
        if (key1 == null || key2 == null || key3 == null) return 0.0;
        
        double score = 0.0;
        
        if (movementEvaluator.isBadRedirection(key1, key2, key3)) {
            score += weights.get(MovementType.BAD_REDIRECTION);
            movementCounts.merge(MovementType.BAD_REDIRECTION, frequency, Long::sum);
        }
        else if (movementEvaluator.isRedirection(key1, key2, key3)) {
            score += weights.get(MovementType.REDIRECTION);
            movementCounts.merge(MovementType.REDIRECTION, frequency, Long::sum);
        }
        
        // Vérifier les skipgrams
        if (movementEvaluator.isSameFinger(key1, key3)) {
            score += weights.get(MovementType.SAME_FINGER_SKIPGRAM);
            movementCounts.merge(MovementType.SAME_FINGER_SKIPGRAM, frequency, Long::sum);
        }
        
        return score * frequency;
    }
    
    /**
     * Affiche les statistiques d'évaluation d'une disposition.
     */
    public void displayEvaluation(KeyboardLayout layout) {
        double score = evaluateLayout(layout);
        
        System.out.println("\nÉvaluation détaillée de la disposition " + layout.name() + " :");
        System.out.println("=".repeat(50));
        
        // Afficher les charges des doigts
        System.out.println("\nCharges des doigts (%) :");
        System.out.println("-".repeat(30));
        for (Map.Entry<KeyboardLayout.Finger, Double> entry : fingerLoads.entrySet()) {
            System.out.printf("%-15s : %6.2f%%\n", entry.getKey(), entry.getValue());
        }
        
        // Afficher les statistiques des mouvements avec leurs contributions au score
        System.out.println("\nContribution des mouvements au score :");
        System.out.println("-".repeat(50));
        System.out.println("Type de mouvement      Poids    Occurrences    Impact sur score");
        System.out.println("-".repeat(50));
        
        // Bigrammes
        System.out.println("\nBigrammes (" + totalBigramCount + " total) :");
        displayMovementStats(MovementType.SAME_FINGER, "Même doigt", totalBigramCount);
        displayMovementStats(MovementType.LATERAL_STRETCH, "Extension latérale", totalBigramCount);
        displayMovementStats(MovementType.SCISSORS, "Ciseaux", totalBigramCount);
        displayMovementStats(MovementType.HAND_ALTERNATION, "Alternance mains", totalBigramCount);
        displayMovementStats(MovementType.INWARD_ROLL, "Roulement intérieur", totalBigramCount);
        displayMovementStats(MovementType.OUTWARD_ROLL, "Roulement extérieur", totalBigramCount);
        
        // Trigrammes
        System.out.println("\nTrigrammes (" + totalTrigramCount + " total) :");
        displayMovementStats(MovementType.BAD_REDIRECTION, "Mauvaise redirection", totalTrigramCount);
        displayMovementStats(MovementType.REDIRECTION, "Redirection", totalTrigramCount);
        displayMovementStats(MovementType.SAME_FINGER_SKIPGRAM, "Skipgram même doigt", totalTrigramCount);
        
        System.out.println("\nScore global : " + String.format("%.2f", score));
        System.out.println("Plus le score est bas, meilleure est la disposition.");
        System.out.println("=".repeat(50));
    }
    
    private void displayMovementStats(MovementType type, String label, long total) {
        long count = movementCounts.getOrDefault(type, 0L);
        double weight = weights.get(type);
        double impact = count * weight;
        double percentage = total > 0 ? (count * 100.0) / total : 0.0;
        
        System.out.printf("%-20s %6.2f %8d (%5.1f%%) %10.2f\n",
            label, weight, count, percentage, impact);
    }
}
