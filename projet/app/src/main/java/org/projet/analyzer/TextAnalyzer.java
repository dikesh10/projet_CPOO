package org.projet.analyzer;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Classe responsable de l'analyse statistique des textes.
 * Cette classe permet d'analyser un corpus de texte pour en extraire :
 * - Les fréquences de caractères individuels
 * - Les fréquences de bigrammes (séquences de 2 caractères)
 * - Les fréquences de trigrammes (séquences de 3 caractères)
 * 
 * <p>Exemple d'utilisation :
 * <pre>{@code
 * TextAnalyzer analyzer = new TextAnalyzer();
 * analyzer.analyzeText("texte");
 * 
 * // Obtenir les fréquences
 * Map<String, Long> charFreq = analyzer.getFrequency("a");
 * Map<String, Long> bigramFreq = analyzer.getFrequency("ab");
 * Map<String, Long> trigramFreq = analyzer.getFrequency("abc");
 * }</pre>
 * 
 * @see TextLoader
 */
public class TextAnalyzer {
    private final Map<String, Long> ngramFrequencies;
    private long totalCharacters;

    /**
     * Constructeur initialisant les structures de données pour l'analyse.
     */
    public TextAnalyzer() {
        this.ngramFrequencies = new HashMap<>();
        this.totalCharacters = 0;
    }

    /**
     * Analyse un texte donné et met à jour les statistiques.
     * 
     * @param text Le texte à analyser
     * @param n La longueur des n-grammes à analyser (1, 2 ou 3)
     */
    public void analyzeText(String text, int n) {
        if (n < 1 || n > 3) {
            throw new IllegalArgumentException("N doit être compris entre 1 et 3");
        }
        
        totalCharacters += text.length();
        
        // Analyser chaque n-gramme possible dans le texte
        for (int i = 0; i <= text.length() - n; i++) {
            String ngram = text.substring(i, i + n);
            ngramFrequencies.merge(ngram, 1L, Long::sum);
        }
    }

    /**
     * Calcule le pourcentage d'occurrence d'un n-gramme.
     * 
     * @param frequency La fréquence brute du n-gramme
     * @return Le pourcentage d'occurrence (entre 0 et 100)
     */
    private double calculatePercentage(long frequency) {
        if (totalCharacters == 0) return 0.0;
        return (frequency * 100.0) / totalCharacters;
    }

    /**
     * Affiche les statistiques d'analyse.
     * Montre les N n-grammes les plus fréquents pour chaque longueur.
     */
    public void displayResults() {
        System.out.println("\n=== Analyse de texte ===");
        System.out.println("Nombre total de caractères : " + totalCharacters);
        
        // Trier les n-grammes par longueur
        Map<Integer, Map<String, Long>> ngramsByLength = ngramFrequencies.entrySet().stream()
            .collect(Collectors.groupingBy(
                e -> e.getKey().length(),
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    Long::sum,
                    TreeMap::new
                )
            ));
        
        // Afficher les résultats pour chaque longueur de n-gramme
        ngramsByLength.forEach((length, frequencies) -> {
            String title = switch (length) {
                case 1 -> "Fréquence des caractères";
                case 2 -> "Fréquence des bigrammes";
                case 3 -> "Fréquence des trigrammes";
                default -> "Fréquence des " + length + "-grammes";
            };
            
            System.out.println("\n=== " + title + " ===");
            
            // Trier par fréquence décroissante et afficher les 10 premiers
            frequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    double percentage = calculatePercentage(entry.getValue());
                    System.out.printf("'%s' : %d (%.2f%%)\n",
                        entry.getKey(),
                        entry.getValue(),
                        percentage);
                });
        });
        System.out.println("\n===================");
    }

    /**
     * Retourne la fréquence d'un n-gramme spécifique.
     * 
     * @param ngram L'n-gramme dont on veut connaître la fréquence
     * @return La fréquence de l'n-gramme (nombre d'occurrences)
     */
    public long getFrequency(String ngram) {
        return ngramFrequencies.getOrDefault(ngram, 0L);
    }

    /**
     * Retourne toutes les fréquences de n-grammes.
     * 
     * @return Une vue non modifiable des fréquences de n-grammes
     */
    public Map<String, Long> getAllFrequencies() {
        return Map.copyOf(ngramFrequencies);
    }

    /**
     * Retourne le nombre total de caractères analysés.
     * 
     * @return Le nombre total de caractères
     */
    public long getTotalCharacters() {
        return totalCharacters;
    }
}
