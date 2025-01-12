package org.projet.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour analyser les caractères accentués et leurs séquences de touches.
 */
public class AccentAnalyzer {
    private final Map<Character, String> accentSequences;
    private final TextAnalyzer textAnalyzer;

    public AccentAnalyzer(TextAnalyzer textAnalyzer) {
        this.textAnalyzer = textAnalyzer;
        this.accentSequences = new HashMap<>();
        initializeAccentSequences();
    }

    private void initializeAccentSequences() {
        // Accents circonflexes
        accentSequences.put('â', "^a");
        accentSequences.put('ê', "^e");
        accentSequences.put('î', "^i");
        accentSequences.put('ô', "^o");
        accentSequences.put('û', "^u");

        // Accents aigus
        accentSequences.put('é', "´e");
        accentSequences.put('á', "´a");
        accentSequences.put('í', "´i");
        accentSequences.put('ó', "´o");
        accentSequences.put('ú', "´u");

        // Accents graves
        accentSequences.put('à', "`a");
        accentSequences.put('è', "`e");
        accentSequences.put('ì', "`i");
        accentSequences.put('ò', "`o");
        accentSequences.put('ù', "`u");

        // Tréma
        accentSequences.put('ë', "¨e");
        accentSequences.put('ï', "¨i");
        accentSequences.put('ü', "¨u");
        accentSequences.put('ÿ', "¨y");

        // Cédille
        accentSequences.put('ç', "c,");
    }

    /**
     * Analyse un texte en prenant en compte les séquences de touches pour les accents.
     * @param text Le texte à analyser
     */
    public void analyzeAccentedText(String text) {
        List<String> keyStrokes = new ArrayList<>();
        
        // Convertir le texte en séquence de frappes
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String sequence = accentSequences.get(c);
            
            if (sequence != null) {
                // Pour un caractère accentué, ajouter chaque frappe séparément
                for (char keystroke : sequence.toCharArray()) {
                    keyStrokes.add(String.valueOf(keystroke));
                }
            } else {
                // Pour un caractère normal, l'ajouter tel quel
                keyStrokes.add(String.valueOf(c));
            }
        }
        
        // Analyser les caractères individuels (unigrammes)
        for (String keystroke : keyStrokes) {
            textAnalyzer.analyzeText(keystroke, 1);
        }
        
        // Analyser les séquences de frappes deux par deux (bigrammes)
        for (int i = 0; i < keyStrokes.size() - 1; i++) {
            String bigramme = keyStrokes.get(i) + keyStrokes.get(i + 1);
            textAnalyzer.analyzeText(bigramme, 2);
        }
        
        // Analyser les séquences de frappes trois par trois (trigrammes)
        for (int i = 0; i < keyStrokes.size() - 2; i++) {
            String trigramme = keyStrokes.get(i) + keyStrokes.get(i + 1) + keyStrokes.get(i + 2);
            textAnalyzer.analyzeText(trigramme, 3);
        }
    }

    /**
     * Vérifie si un caractère est accentué.
     * @param c Le caractère à vérifier
     * @return true si le caractère est accentué, false sinon
     */
    public boolean isAccented(char c) {
        return accentSequences.containsKey(c);
    }

    /**
     * Retourne la séquence de touches pour un caractère accentué.
     * @param c Le caractère accentué
     * @return La séquence de touches ou null si le caractère n'est pas accentué
     */
    public String getKeySequence(char c) {
        return accentSequences.get(c);
    }
}
