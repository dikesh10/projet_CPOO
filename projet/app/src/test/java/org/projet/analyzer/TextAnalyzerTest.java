package org.projet.analyzer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Tests unitaires pour la classe TextAnalyzer.
 */
public class TextAnalyzerTest {
    private TextAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new TextAnalyzer();
    }

    @Test
    @DisplayName("Test de l'analyse des caractères individuels")
    void testAnalyzeSimpleText() {
        String text = "hello";
        analyzer.analyzeText(text, 1);
        
        // Vérifier les fréquences individuelles
        assertEquals(1L, analyzer.getFrequency("h"));
        assertEquals(1L, analyzer.getFrequency("e"));
        assertEquals(2L, analyzer.getFrequency("l"));
        assertEquals(1L, analyzer.getFrequency("o"));
        
        // Vérifier le total des caractères
        assertEquals(5L, analyzer.getTotalCharacters());
    }

    @Test
    @DisplayName("Test de l'analyse des bigrammes")
    void testAnalyzeBigrams() {
        String text = "hello";
        analyzer.analyzeText(text, 2);
        
        // Vérifier les bigrammes
        assertEquals(1L, analyzer.getFrequency("he"));
        assertEquals(1L, analyzer.getFrequency("el"));
        assertEquals(1L, analyzer.getFrequency("ll"));
        assertEquals(1L, analyzer.getFrequency("lo"));
        
        // Vérifier qu'un bigramme inexistant retourne 0
        assertEquals(0L, analyzer.getFrequency("xx"));
    }

    @Test
    @DisplayName("Test de l'analyse des trigrammes")
    void testAnalyzeTrigrams() {
        String text = "hello";
        analyzer.analyzeText(text, 3);
        
        // Vérifier les trigrammes
        assertEquals(1L, analyzer.getFrequency("hel"));
        assertEquals(1L, analyzer.getFrequency("ell"));
        assertEquals(1L, analyzer.getFrequency("llo"));
        
        // Vérifier qu'un trigramme inexistant retourne 0
        assertEquals(0L, analyzer.getFrequency("xyz"));
    }

    @Test
    @DisplayName("Test des valeurs invalides de N")
    void testInvalidNValue() {
        // Tester les valeurs invalides de N
        assertThrows(IllegalArgumentException.class, () -> 
            analyzer.analyzeText("test", 0));
            
        assertThrows(IllegalArgumentException.class, () -> 
            analyzer.analyzeText("test", 4));
    }

    @Test
    @DisplayName("Test avec un texte vide")
    void testEmptyText() {
        analyzer.analyzeText("", 1);
        
        // Vérifier que le texte vide est géré correctement
        assertEquals(0L, analyzer.getTotalCharacters());
        assertTrue(analyzer.getAllFrequencies().isEmpty());
    }

    @Test
    @DisplayName("Test avec plusieurs analyses successives")
    void testMultipleAnalysis() {
        // Analyser deux textes successivement
        analyzer.analyzeText("hello", 1);
        analyzer.analyzeText("world", 1);
        
        // Vérifier le total des caractères
        assertEquals(10L, analyzer.getTotalCharacters());
        
        // Vérifier les fréquences individuelles
        Map<String, Long> frequencies = analyzer.getAllFrequencies();
        assertEquals(7, frequencies.size()); // h,e,l,o,w,r,d (l et o apparaissent deux fois)
        
        // Vérifier chaque fréquence
        assertEquals(1L, frequencies.get("h"));
        assertEquals(1L, frequencies.get("e"));
        assertEquals(3L, frequencies.get("l"));
        assertEquals(2L, frequencies.get("o"));
        assertEquals(1L, frequencies.get("w"));
        assertEquals(1L, frequencies.get("r"));
        assertEquals(1L, frequencies.get("d"));
        
        // Vérifier qu'un caractère inexistant retourne 0
        assertEquals(0L, analyzer.getFrequency("x"));
    }

    @Test
    @DisplayName("Test de getAllFrequencies")
    void testGetAllFrequencies() {
        String text = "hello";
        analyzer.analyzeText(text, 1);
        
        // Vérifier que la map retournée est non modifiable
        Map<String, Long> frequencies = analyzer.getAllFrequencies();
        assertThrows(UnsupportedOperationException.class, () -> 
            frequencies.put("x", 1L));
        
        // Vérifier que toutes les fréquences sont présentes
        assertEquals(4, frequencies.size()); // h,e,l,o (l apparaît deux fois)
        assertTrue(frequencies.containsKey("h"));
        assertTrue(frequencies.containsKey("e"));
        assertTrue(frequencies.containsKey("l"));
        assertTrue(frequencies.containsKey("o"));
    }
}
