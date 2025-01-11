package org.projet.analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextAnalyzerTest {
    
    @Test
    void shouldAnalyzeSingleCharacters() {
        TextAnalyzer analyzer = new TextAnalyzer();
        String text = "hello";
        
        analyzer.analyzeText(text, 1);
        
        assertEquals(1, analyzer.getFrequency("h"));
        assertEquals(1, analyzer.getFrequency("e"));
        assertEquals(2, analyzer.getFrequency("l"));
        assertEquals(1, analyzer.getFrequency("o"));
    }
    
    @Test
    void shouldAnalyzeBigrams() {
        TextAnalyzer analyzer = new TextAnalyzer();
        String text = "hello";
        
        analyzer.analyzeText(text, 2);
        
        assertEquals(1, analyzer.getFrequency("he"));
        assertEquals(1, analyzer.getFrequency("el"));
        assertEquals(1, analyzer.getFrequency("ll"));
        assertEquals(1, analyzer.getFrequency("lo"));
    }
    
    @Test
    void shouldThrowExceptionForInvalidN() {
        TextAnalyzer analyzer = new TextAnalyzer();
        String text = "hello";
        
        assertThrows(IllegalArgumentException.class, () -> {
            analyzer.analyzeText(text, 0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            analyzer.analyzeText(text, 4);
        });
    }
}
