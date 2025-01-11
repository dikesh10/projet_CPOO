# Exemples d'Utilisation

Ce document fournit des exemples concrets d'utilisation de l'Optimiseur de Disposition de Clavier.

## 1. Analyse Simple d'un Texte

```java
import org.projet.analyzer.TextAnalyzer;

public class SimpleAnalysis {
    public static void main(String[] args) {
        TextAnalyzer analyzer = new TextAnalyzer();
        
        // Analyser un texte simple
        analyzer.analyzeText("Hello, World!");
        
        // Afficher les fréquences
        System.out.println("Fréquences des caractères :");
        analyzer.getCharacterFrequencies().forEach((c, f) -> 
            System.out.printf("%s : %d%n", c, f));
    }
}
```

## 2. Évaluation d'une Disposition AZERTY

```java
import org.projet.model.KeyboardLayout;
import org.projet.evaluator.LayoutEvaluator;

public class AzertyEvaluation {
    public static void main(String[] args) {
        // Créer une disposition AZERTY simplifiée
        Map<Character, KeyboardLayout.Key> layout = new HashMap<>();
        
        // Rangée du milieu
        layout.put('a', new KeyboardLayout.Key(2, 0, KeyboardLayout.Finger.LEFT_PINKY, 'A', null));
        layout.put('z', new KeyboardLayout.Key(2, 1, KeyboardLayout.Finger.LEFT_RING, 'Z', null));
        layout.put('e', new KeyboardLayout.Key(2, 2, KeyboardLayout.Finger.LEFT_MIDDLE, 'E', '€'));
        layout.put('r', new KeyboardLayout.Key(2, 3, KeyboardLayout.Finger.LEFT_INDEX, 'R', null));
        layout.put('t', new KeyboardLayout.Key(2, 4, KeyboardLayout.Finger.LEFT_INDEX, 'T', null));
        
        KeyboardLayout azerty = new KeyboardLayout("AZERTY", layout);
        
        // Créer un évaluateur avec des fréquences
        Map<String, Long> frequencies = new HashMap<>();
        frequencies.put("e", 1000L);
        frequencies.put("a", 800L);
        frequencies.put("s", 700L);
        
        LayoutEvaluator evaluator = new LayoutEvaluator(frequencies);
        
        // Évaluer la disposition
        double score = evaluator.evaluateLayout(azerty);
        System.out.printf("Score AZERTY : %.2f%n", score);
        
        // Afficher les statistiques détaillées
        evaluator.displayEvaluation(azerty);
    }
}
```

## 3. Optimisation avec Contraintes

```java
import org.projet.optimizer.KeyboardOptimizer;
import org.projet.model.KeyboardLayout;

public class ConstrainedOptimization {
    public static void main(String[] args) {
        // Créer un analyseur et analyser du texte
        TextAnalyzer analyzer = new TextAnalyzer();
        analyzer.analyzeFile("textes/corpus.txt");
        
        // Créer un optimiseur
        KeyboardOptimizer optimizer = new KeyboardOptimizer(analyzer.getAllFrequencies());
        
        // Configurer l'optimisation
        optimizer.setPopulationSize(100);
        optimizer.setGenerations(1000);
        optimizer.setMutationRate(0.1);
        
        // Ajouter des contraintes
        optimizer.setConstraints(constraints -> {
            // Garder certaines touches fixes
            constraints.addFixedKey('a');
            constraints.addFixedKey('z');
            
            // Ne modifier que certaines rangées
            constraints.setModifiableRows(1, 2);
        });
        
        // Lancer l'optimisation
        KeyboardLayout bestLayout = optimizer.optimize();
        
        // Évaluer le résultat
        LayoutEvaluator evaluator = new LayoutEvaluator(analyzer.getAllFrequencies());
        evaluator.displayEvaluation(bestLayout);
    }
}
```

## 4. Analyse de Corpus Complet

```java
import org.projet.analyzer.TextAnalyzer;
import java.nio.file.*;

public class CorpusAnalysis {
    public static void main(String[] args) throws IOException {
        TextAnalyzer analyzer = new TextAnalyzer();
        
        // Analyser tous les fichiers d'un dossier
        try (var files = Files.walk(Path.of("textes"))) {
            files.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".txt"))
                 .forEach(p -> {
                     try {
                         analyzer.analyzeFile(p.toString());
                         System.out.println("Analysé : " + p);
                     } catch (IOException e) {
                         System.err.println("Erreur avec " + p + ": " + e);
                     }
                 });
        }
        
        // Afficher les statistiques
        System.out.println("\nTop 10 caractères les plus fréquents :");
        analyzer.getCharacterFrequencies().entrySet().stream()
               .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
               .limit(10)
               .forEach(e -> System.out.printf("%s : %d%n", e.getKey(), e.getValue()));
    }
}
```

## 5. Comparaison de Dispositions

```java
import org.projet.model.KeyboardLayout;
import org.projet.evaluator.LayoutEvaluator;

public class LayoutComparison {
    public static void main(String[] args) {
        // Créer un analyseur et analyser du texte
        TextAnalyzer analyzer = new TextAnalyzer();
        analyzer.analyzeFile("textes/corpus.txt");
        
        // Créer un évaluateur
        LayoutEvaluator evaluator = new LayoutEvaluator(analyzer.getAllFrequencies());
        
        // Charger différentes dispositions
        KeyboardLayout azerty = loadAzertyLayout();
        KeyboardLayout qwerty = loadQwertyLayout();
        KeyboardLayout bepo = loadBepoLayout();
        
        // Évaluer chaque disposition
        System.out.println("=== Comparaison des Dispositions ===\n");
        
        System.out.println("AZERTY :");
        double azertyScore = evaluator.evaluateLayout(azerty);
        evaluator.displayEvaluation(azerty);
        
        System.out.println("\nQWERTY :");
        double qwertyScore = evaluator.evaluateLayout(qwerty);
        evaluator.displayEvaluation(qwerty);
        
        System.out.println("\nBÉPO :");
        double bepoScore = evaluator.evaluateLayout(bepo);
        evaluator.displayEvaluation(bepo);
        
        // Afficher un résumé
        System.out.println("\n=== Résumé des Scores ===");
        System.out.printf("AZERTY : %.2f%n", azertyScore);
        System.out.printf("QWERTY : %.2f%n", qwertyScore);
        System.out.printf("BÉPO   : %.2f%n", bepoScore);
    }
    
    private static KeyboardLayout loadAzertyLayout() {
        // Implémentation de chargement AZERTY
        return null;
    }
    
    private static KeyboardLayout loadQwertyLayout() {
        // Implémentation de chargement QWERTY
        return null;
    }
    
    private static KeyboardLayout loadBepoLayout() {
        // Implémentation de chargement BÉPO
        return null;
    }
}
```

Ces exemples montrent différentes façons d'utiliser l'API pour :
- Analyser des textes
- Évaluer des dispositions existantes
- Optimiser des dispositions avec contraintes
- Comparer différentes dispositions
- Traiter des corpus complets

Chaque exemple peut être adapté selon vos besoins spécifiques.
