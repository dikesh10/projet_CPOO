package org.projet;

import org.projet.analyzer.TextAnalyzer;
import org.projet.analyzer.TextLoader;
import org.projet.evaluator.LayoutEvaluator;
import org.projet.config.KeyboardConfigLoader;
import org.projet.optimizer.KeyboardOptimizer;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

/**
 * Main application class for the keyboard layout analyzer and optimizer.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("=== Analyseur et Optimiseur de Disposition Clavier ===\n");
        
        try {
            // Créer l'analyseur
            TextAnalyzer analyzer = new TextAnalyzer();
            
            // Obtenir le chemin des ressources
            var textsResource = App.class.getClassLoader().getResource("texts");
            if (textsResource == null) {
                throw new IllegalStateException("Le dossier 'texts' n'a pas été trouvé dans les ressources");
            }
            Path textsDir = Path.of(textsResource.toURI());
            
            System.out.println("Analyse des fichiers dans : " + textsDir);
            
            // Charger et analyser tous les fichiers en parallèle
            List<String> texts = TextLoader.loadFromDirectory(textsDir);
            analyzer.analyzeTexts(texts);
            
            // Afficher les résultats
            System.out.println("\n=== Analyse de texte ===");
            System.out.println("Nombre total de caractères : " + analyzer.getTotalCharacters());
            
            // Afficher les fréquences des touches
            System.out.println("\n=== Fréquence des touches ===");
            Map<String, Long> frequencies = analyzer.getAllFrequencies();
            frequencies.entrySet().stream()
                .filter(entry -> entry.getKey().length() == 1)  // Uniquement les touches individuelles
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / analyzer.getTotalCharacters();
                    System.out.printf("'%s' : %d (%.2f%%)\n",
                        entry.getKey(), entry.getValue(), percentage);
                });

            // Afficher les fréquences des bigrammes de touches
            System.out.println("\n=== Fréquence des bigrammes de touches ===");
            frequencies.entrySet().stream()
                .filter(entry -> entry.getKey().length() == 2)  // Uniquement les bigrammes
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / analyzer.getTotalCharacters();
                    System.out.printf("'%s' : %d (%.2f%%)\n",
                        entry.getKey(), entry.getValue(), percentage);
                });

            // Afficher les fréquences des trigrammes de touches
            System.out.println("\n=== Fréquence des trigrammes de touches ===");
            frequencies.entrySet().stream()
                .filter(entry -> entry.getKey().length() == 3)  // Uniquement les trigrammes
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / analyzer.getTotalCharacters();
                    System.out.printf("'%s' : %d (%.2f%%)\n",
                        entry.getKey(), entry.getValue(), percentage);
                });
            
            System.out.println("\n===================");
            
            // Charger la disposition AZERTY
            KeyboardConfigLoader configLoader = new KeyboardConfigLoader();
            var layoutOpt = configLoader.loadLayout(
                Path.of(App.class.getClassLoader()
                    .getResource("layouts/azerty.json")
                    .toURI())
            );
            
            if (layoutOpt.isPresent()) {
                var layout = layoutOpt.get();
                
                // Créer un Map des fréquences pour l'évaluateur
                Map<String, Long> frequenciesForEvaluator = texts.stream()
                    .flatMap(text -> {
                        var ngrams = new java.util.ArrayList<String>();
                        // Caractères
                        for (int i = 0; i < text.length(); i++) {
                            ngrams.add(String.valueOf(text.charAt(i)));
                        }
                        // Bigrammes
                        for (int i = 0; i < text.length() - 1; i++) {
                            ngrams.add(text.substring(i, i + 2));
                        }
                        // Trigrammes
                        for (int i = 0; i < text.length() - 2; i++) {
                            ngrams.add(text.substring(i, i + 3));
                        }
                        return ngrams.stream();
                    })
                    .collect(Collectors.groupingBy(
                        ngram -> ngram,
                        Collectors.counting()
                    ));
                
                // Évaluer la disposition initiale
                LayoutEvaluator evaluator = new LayoutEvaluator(frequenciesForEvaluator);
                evaluator.displayEvaluation(layout);
                
                // Optimiser la disposition
                System.out.println("\nOptimisation de la disposition...");
                KeyboardOptimizer optimizer = new KeyboardOptimizer(evaluator);
                var optimizedLayout = optimizer.optimize(layout);
                
                System.out.println("\nDisposition optimisée :\n");
                evaluator.displayEvaluation(optimizedLayout);
            } else {
                System.err.println("Impossible de charger la disposition AZERTY");
            }
            
            // Fermer proprement l'analyseur
            analyzer.shutdown();
            
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
