package org.projet.config;

import org.junit.jupiter.api.Test;
import org.projet.model.KeyboardLayout;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class KeyboardConfigLoaderTest {
    
    @Test
    void shouldLoadAzertyLayout() {
        KeyboardConfigLoader loader = new KeyboardConfigLoader();
        
        Optional<KeyboardLayout> layoutOpt = loader.loadLayout(
            Path.of("src/main/resources/layouts/azerty.json")
        );
        
        assertTrue(layoutOpt.isPresent(), "Layout should be loaded successfully");
        
        KeyboardLayout layout = layoutOpt.get();
        assertEquals("AZERTY", layout.name());
        
        // Verify some key positions
        var aKey = layout.characterToKeyMap().get('a');
        assertNotNull(aKey);
        assertEquals(2, aKey.row());
        assertEquals(0, aKey.column());
        assertEquals(KeyboardLayout.Finger.LEFT_PINKY, aKey.finger());
        
        var eKey = layout.characterToKeyMap().get('e');
        assertNotNull(eKey);
        assertEquals(2, eKey.row());
        assertEquals(2, eKey.column());
        assertEquals(KeyboardLayout.Finger.LEFT_MIDDLE, eKey.finger());
    }
    
    @Test
    void shouldReturnEmptyForInvalidFile() {
        KeyboardConfigLoader loader = new KeyboardConfigLoader();
        
        Optional<KeyboardLayout> layoutOpt = loader.loadLayout(
            Path.of("non_existent_file.json")
        );
        
        assertTrue(layoutOpt.isEmpty(), "Should return empty for invalid file");
    }
}
