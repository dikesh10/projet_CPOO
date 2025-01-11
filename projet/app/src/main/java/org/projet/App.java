package org.projet;

import org.projet.analyzer.TextAnalyzer;
import org.projet.analyzer.TextLoader;
import org.projet.evaluator.LayoutEvaluator;
import org.projet.config.KeyboardConfigLoader;
import org.projet.optimizer.KeyboardOptimizer;

import java.net.URI;
import java.nio.file.Path;

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
            TextLoader.analyzeDirectory(analyzer, textsDir);
            
            // Afficher les résultats de l'analyse
            analyzer.displayResults();
            
            // Charger la disposition AZERTY
            KeyboardConfigLoader configLoader = new KeyboardConfigLoader();
            var layoutOpt = configLoader.loadLayout(
                Path.of(App.class.getClassLoader()
                    .getResource("layouts/azerty.json")
                    .toURI())
            );
            
            if (layoutOpt.isPresent()) {
                var layout = layoutOpt.get();
                
                // Évaluer la disposition initiale
                LayoutEvaluator evaluator = new LayoutEvaluator(analyzer.getAllFrequencies());
                evaluator.displayEvaluation(layout);
                
                // Optimiser la disposition
                System.out.println("\nOptimisation de la disposition...");
                KeyboardOptimizer optimizer = new KeyboardOptimizer(
                    evaluator,    // évaluateur
                    50,          // taille de la population
                    100,         // nombre de générations
                    0.1,         // taux de mutation
                    0.8          // taux de croisement
                );
                
                var optimizedLayout = optimizer.optimize(layout);
                
                System.out.println("\nDisposition optimisée :");
                evaluator.displayEvaluation(optimizedLayout);
            } else {
                System.err.println("Impossible de charger la disposition AZERTY");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
