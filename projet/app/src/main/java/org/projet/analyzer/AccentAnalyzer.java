package org.projet.analyzer;

import java.util.HashMap;
import java.util.Map;

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
        StringBuilder keySequence = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String sequence = accentSequences.get(c);
            
            if (sequence != null) {
                // Si c'est un caractère accentué, ajouter sa séquence de touches
                keySequence.append(sequence);
            } else {
                // Sinon, ajouter le caractère tel quel
                keySequence.append(c);
            }
        }
        
        // Analyser la séquence de touches comme des bigrammes
        textAnalyzer.analyzeText(keySequence.toString(), 2);
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
