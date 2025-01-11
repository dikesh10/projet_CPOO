package org.projet.evaluator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.projet.model.KeyboardLayout.Key;
import org.projet.model.KeyboardLayout.Finger;

public class MovementEvaluatorTest {
    private final MovementEvaluator evaluator = new MovementEvaluator();

    @Test
    void shouldDetectSameFinger() {
        Key key1 = new Key(1, 0, Finger.LEFT_PINKY, null, null);
        Key key2 = new Key(2, 0, Finger.LEFT_PINKY, null, null);
        
        assertTrue(evaluator.isSameFinger(key1, key2));
        
        Key key3 = new Key(1, 0, Finger.LEFT_RING, null, null);
        assertFalse(evaluator.isSameFinger(key1, key3));
    }
    
    @Test
    void shouldDetectSameHand() {
        Key leftPinky = new Key(1, 0, Finger.LEFT_PINKY, null, null);
        Key leftIndex = new Key(1, 3, Finger.LEFT_INDEX, null, null);
        Key rightIndex = new Key(1, 4, Finger.RIGHT_INDEX, null, null);
        
        assertTrue(evaluator.isSameHand(leftPinky, leftIndex));
        assertFalse(evaluator.isSameHand(leftIndex, rightIndex));
    }
    
    @Test
    void shouldDetectLateralStretch() {
        Key key1 = new Key(1, 0, Finger.LEFT_INDEX, null, null);
        Key key2 = new Key(1, 3, Finger.LEFT_INDEX, null, null);
        Key key3 = new Key(1, 1, Finger.LEFT_INDEX, null, null);
        
        assertTrue(evaluator.isLateralStretch(key1, key2));
        assertFalse(evaluator.isLateralStretch(key1, key3));
    }
    
    @Test
    void shouldDetectInwardRoll() {
        // Test pour la main gauche
        Key leftPinky = new Key(1, 0, Finger.LEFT_PINKY, null, null);
        Key leftRing = new Key(1, 1, Finger.LEFT_RING, null, null);
        Key leftMiddle = new Key(1, 2, Finger.LEFT_MIDDLE, null, null);
        
        assertTrue(evaluator.isInwardRoll(leftPinky, leftRing));
        assertTrue(evaluator.isInwardRoll(leftRing, leftMiddle));
        assertFalse(evaluator.isInwardRoll(leftMiddle, leftRing));
        
        // Test pour la main droite
        Key rightPinky = new Key(1, 9, Finger.RIGHT_PINKY, null, null);
        Key rightRing = new Key(1, 8, Finger.RIGHT_RING, null, null);
        
        assertTrue(evaluator.isInwardRoll(rightPinky, rightRing));
    }
    
    @Test
    void shouldDetectScissors() {
        // Test pour la main gauche
        Key leftPinky = new Key(1, 2, Finger.LEFT_PINKY, null, null);
        Key leftRing = new Key(1, 0, Finger.LEFT_RING, null, null);
        
        assertTrue(evaluator.isScissors(leftPinky, leftRing));
        
        // Test pour la main droite
        Key rightPinky = new Key(1, 7, Finger.RIGHT_PINKY, null, null);
        Key rightRing = new Key(1, 9, Finger.RIGHT_RING, null, null);
        
        assertTrue(evaluator.isScissors(rightPinky, rightRing));
        
        // Test sans croisement
        Key key1 = new Key(1, 0, Finger.LEFT_PINKY, null, null);
        Key key2 = new Key(1, 1, Finger.LEFT_RING, null, null);
        
        assertFalse(evaluator.isScissors(key1, key2));
    }
}
