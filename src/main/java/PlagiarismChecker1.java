import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PlagiarismChecker1 {
    private static Map<String, Set<String>> cache = new HashMap<>();

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlagiarismChecker <original_file_path> <plagiarized_file_path> <output_file_path>");
            return;
        }

        String originalFilePath = args[0];
        String plagiarizedFilePath = args[1];
        String outputFilePath = args[2];

        try {
            if (!Files.exists(Paths.get(originalFilePath)) || !Files.exists(Paths.get(plagiarizedFilePath))) {
                System.out.println("One or both input files do not exist.");
                return;
            }

            String originalText = readFile(originalFilePath);
            String plagiarizedText = readFile(plagiarizedFilePath);

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            Future<Double> similarityFuture = executor.submit(() -> calculateSimilarity(originalText, plagiarizedText));

            try {
                double similarityRate = similarityFuture.get();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                    writer.write(String.format("Similarity Rate: %.2f%%", similarityRate));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }

    private static Set<String> getWords(String text) {
        if (cache.containsKey(text)) {
            return cache.get(text);
        }

        JiebaSegmenter segmenter = new JiebaSegmenter();
        Set<String> words = new HashSet<>(segmenter.sentenceProcess(text));
        cache.put(text, words);
        return words;
    }

    private static double calculateSimilarity(String originalText, String plagiarizedText) {
        Set<String> originalWords = getWords(originalText);
        Set<String> plagiarizedWords = getWords(plagiarizedText);

        Set<String> intersection = new HashSet<>(originalWords.size() < plagiarizedWords.size() ? originalWords : plagiarizedWords);
        intersection.retainAll(originalWords.size() < plagiarizedWords.size() ? plagiarizedWords : originalWords);

        if (originalWords.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / originalWords.size() * 100;
    }
}
