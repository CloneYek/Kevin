import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class PlagiarismChecker {
    public static void main(String[] args) {
        // 检查命令行参数数量
        if (args.length != 3) {
            System.out.println("Usage: java PlagiarismChecker <original_file_path> <plagiarized_file_path> <output_file_path>");
            return; // 如果参数不正确，输出用法提示并退出
        }
        // 获取文件路径
        String originalFilePath = args[0];  // 原始文本文件路径
        String plagiarizedFilePath = args[1];    // 抄袭文本文件路径
        String outputFilePath = args[2];    // 输出结果文件路径

        try {
            // 读取原始文本和抄袭文本内容
            String originalText = new String(Files.readAllBytes(Paths.get(originalFilePath)));
            String plagiarizedText = new String(Files.readAllBytes(Paths.get(plagiarizedFilePath)));
            // 计算相似度
            double similarityRate = calculateSimilarity(originalText, plagiarizedText);

            // 将结果写入输出文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                writer.write(String.format("%.2f", similarityRate)); // 格式化为两位小数
            }

        } catch (IOException e) {
            e.printStackTrace();  // 捕获并打印异常信息
        }
    }

    private static double calculateSimilarity(String originalText, String plagiarizedText) {
        JiebaSegmenter segmenter = new JiebaSegmenter(); // 创建 Jieba 分词器实例
        // 分词并转换为集合
        Set<String> originalWords = new HashSet<>(segmenter.sentenceProcess(originalText));  // 原始文本分词
        Set<String> plagiarizedWords = new HashSet<>(segmenter.sentenceProcess(plagiarizedText));  // 抄袭文本分词

        // 计算交集
        Set<String> intersection = new HashSet<>(originalWords); // 创建原始文本单词的副本
        intersection.retainAll(plagiarizedWords);   // 保留交集部分

        // 计算相似度百分比
        double similarityRate = (double) intersection.size() / originalWords.size() * 100;

        return similarityRate;
    }
}
