# Guide d'Utilisation - Optimiseur de Disposition de Clavier

Ce guide détaille l'utilisation de l'Optimiseur de Disposition de Clavier, un outil permettant d'analyser et d'optimiser les dispositions de clavier selon des critères ergonomiques.

## Table des Matières

1. [Installation](#installation)
2. [Analyse de Texte](#analyse-de-texte)
3. [Évaluation de Disposition](#évaluation-de-disposition)
4. [Optimisation](#optimisation)
5. [Personnalisation](#personnalisation)
6. [Dépannage](#dépannage)

## Installation

1. Vérifiez que vous avez Java 21 ou supérieur installé :
   ```bash
   java --version
   ```

2. Clonez le dépôt et compilez le projet :
   ```bash
   git clone [URL_DU_DEPOT]
   cd projet
   ./gradlew build
   ```

## Analyse de Texte

L'analyse de texte permet de déterminer les fréquences d'utilisation des caractères dans votre langue.

### Analyse d'un Fichier

```java
TextAnalyzer analyzer = new TextAnalyzer();

// Analyser un fichier
analyzer.analyzeFile("textes/exemple.txt");

// Obtenir les statistiques
Map<String, Long> charFreq = analyzer.getCharacterFrequencies();
System.out.println("Fréquence de 'e' : " + charFreq.get("e"));
```

### Analyse de Plusieurs Fichiers

```java
TextAnalyzer analyzer = new TextAnalyzer();

// Analyser plusieurs fichiers
analyzer.analyzeFile("textes/livre1.txt");
analyzer.analyzeFile("textes/livre2.txt");

// Les fréquences sont cumulées automatiquement
```

### Réinitialisation

```java
// Réinitialiser les statistiques
analyzer.reset();
```

## Évaluation de Disposition

L'évaluateur de disposition permet de noter une disposition selon différents critères ergonomiques.

### Création d'une Disposition

```java
// Créer une disposition AZERTY simplifiée
Map<Character, KeyboardLayout.Key> layout = new HashMap<>();

// Rangée du milieu
layout.put('a', new KeyboardLayout.Key(2, 0, KeyboardLayout.Finger.LEFT_PINKY, 'A', null));
layout.put('z', new KeyboardLayout.Key(2, 1, KeyboardLayout.Finger.LEFT_RING, 'Z', null));
layout.put('e', new KeyboardLayout.Key(2, 2, KeyboardLayout.Finger.LEFT_MIDDLE, 'E', '€'));
layout.put('r', new KeyboardLayout.Key(2, 3, KeyboardLayout.Finger.LEFT_INDEX, 'R', null));
layout.put('t', new KeyboardLayout.Key(2, 4, KeyboardLayout.Finger.LEFT_INDEX, 'T', null));

KeyboardLayout keyboardLayout = new KeyboardLayout("AZERTY", layout);
```

### Évaluation

```java
// Créer un évaluateur avec les fréquences d'un analyseur
LayoutEvaluator evaluator = new LayoutEvaluator(analyzer.getAllFrequencies());

// Évaluer la disposition
double score = evaluator.evaluateLayout(keyboardLayout);

// Afficher les statistiques détaillées
evaluator.displayEvaluation(keyboardLayout);
```

### Interprétation des Résultats

Le score d'évaluation prend en compte plusieurs facteurs :

1. **Mouvements problématiques** (pénalités) :
   - Même doigt (2.0)
   - Extension latérale (1.5)
   - Ciseaux (1.8)
   - Mauvaise redirection (1.7)
   - Redirection simple (1.2)
   - Skipgram même doigt (1.6)

2. **Mouvements favorables** (bonus) :
   - Alternance des mains (-0.8)
   - Roulement intérieur (-1.0)
   - Roulement extérieur (-0.5)

Plus le score est bas, meilleure est la disposition.

## Optimisation

L'optimiseur utilise un algorithme génétique pour trouver une meilleure disposition.

### Configuration de Base

```java
KeyboardOptimizer optimizer = new KeyboardOptimizer(analyzer.getAllFrequencies());

// Configurer les paramètres
optimizer.setPopulationSize(100);     // Taille de la population
optimizer.setGenerations(1000);       // Nombre de générations
optimizer.setMutationRate(0.1);       // Taux de mutation

// Lancer l'optimisation
KeyboardLayout bestLayout = optimizer.optimize();
```

### Configuration Avancée

```java
// Définir des contraintes
optimizer.setConstraints(constraints -> {
    // Garder certaines touches fixes
    constraints.addFixedKey('a');
    constraints.addFixedKey('z');
    
    // Définir des zones de touches modifiables
    constraints.setModifiableRows(1, 2);
});

// Définir une fonction de fitness personnalisée
optimizer.setFitnessFunction(layout -> {
    double score = evaluator.evaluateLayout(layout);
    double handBalance = evaluator.getHandBalance();
    return score * (1 + Math.abs(0.5 - handBalance));
});
```

## Personnalisation

### Modification des Poids

Vous pouvez modifier les poids des différents critères dans `LayoutEvaluator` :

```java
Map<MovementType, Double> weights = new EnumMap<>(MovementType.class);
weights.put(MovementType.SAME_FINGER, 2.5);      // Augmenter la pénalité
weights.put(MovementType.HAND_ALTERNATION, -1.0); // Augmenter le bonus

evaluator.setWeights(weights);
```

### Ajout de Contraintes

```java
// Définir des touches qui doivent rester proches
optimizer.addProximityConstraint('c', 'v');

// Définir des groupes de touches
optimizer.addGroup(Arrays.asList('a', 'e', 'i', 'o', 'u'));
```

## Dépannage

### Problèmes Courants

1. **Score toujours élevé**
   - Vérifiez que les fréquences sont correctement chargées
   - Augmentez le nombre de générations
   - Ajustez les poids des critères

2. **Optimisation trop lente**
   - Réduisez la taille de la population
   - Limitez le nombre de touches modifiables
   - Utilisez des contraintes pour réduire l'espace de recherche

3. **Résultats inconsistants**
   - Augmentez la taille de la population
   - Réduisez le taux de mutation
   - Utilisez plusieurs runs avec des graines différentes

### Logs et Débogage

```java
// Activer les logs détaillés
optimizer.setVerbose(true);

// Sauvegarder les résultats intermédiaires
optimizer.enableCheckpointing("results/");

// Analyser une disposition spécifique
evaluator.displayEvaluation(layout);
```
