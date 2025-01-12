package org.projet.analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class to load and process text files.
 */
public class TextLoader {
    
    /**
     * Loads and analyzes a single text file.
     * @param analyzer The text analyzer to use
     * @param filePath Path to the text file
     * @throws IOException if the file cannot be read
     */
    public static void analyzeFile(TextAnalyzer analyzer, Path filePath) throws IOException {
        String content = Files.readString(filePath);
        System.out.println("Analyse du fichier : " + filePath.getFileName());
        
        // Créer l'analyseur d'accents
        AccentAnalyzer accentAnalyzer = new AccentAnalyzer(analyzer);
        
        // Analyser les caractères individuels et les séquences de touches
        accentAnalyzer.analyzeAccentedText(content);
    }
    
    /**
     * Loads and analyzes all text files in a directory.
     * @param analyzer The text analyzer to use
     * @param directoryPath Path to the directory containing text files
     * @throws IOException if the directory cannot be read
     */
    public static void analyzeDirectory(TextAnalyzer analyzer, Path directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            paths.filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .forEach(path -> {
                    try {
                        analyzeFile(analyzer, path);
                    } catch (IOException e) {
                        System.err.println("Erreur lors de l'analyse du fichier " + path + ": " + e.getMessage());
                    }
                });
        }
    }
}
