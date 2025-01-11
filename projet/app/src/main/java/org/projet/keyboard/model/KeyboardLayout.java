package org.projet.keyboard.model;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * Représente une disposition de clavier complète.
 * Cette classe est immuable une fois créée.
 */
public class KeyboardLayout {
    private final Map<Character, KeyPosition> characterToPosition;
    private final Map<KeyPosition, Character> positionToCharacter;
    private final String name;

    /**
     * Crée une nouvelle disposition de clavier.
     * @param layout Map associant les caractères à leurs positions
     * @param name Nom de la disposition (ex: "AZERTY", "QWERTY")
     */
    public KeyboardLayout(Map<Character, KeyPosition> layout, String name) {
        this.characterToPosition = Collections.unmodifiableMap(new HashMap<>(layout));
        
        // Créer la map inverse
        Map<KeyPosition, Character> inverse = new HashMap<>();
        layout.forEach((character, position) -> inverse.put(position, character));
        this.positionToCharacter = Collections.unmodifiableMap(inverse);
        
        this.name = name;
    }

    /**
     * Retourne la position d'un caractère sur le clavier.
     * @param c Le caractère à localiser
     * @return La position du caractère, ou null si le caractère n'est pas dans la disposition
     */
    public KeyPosition getPosition(char c) {
        return characterToPosition.get(c);
    }

    /**
     * Retourne le caractère à une position donnée.
     * @param position La position à vérifier
     * @return Le caractère à cette position, ou null si aucun caractère n'y est assigné
     */
    public Character getCharacter(KeyPosition position) {
        return positionToCharacter.get(position);
    }

    /**
     * Retourne le nom de la disposition.
     * @return Le nom de la disposition
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne toutes les positions de touches utilisées dans cette disposition.
     * @return Une vue non modifiable des positions
     */
    public Map<Character, KeyPosition> getAllPositions() {
        return characterToPosition;
    }
}
