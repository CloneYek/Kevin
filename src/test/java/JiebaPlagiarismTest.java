import com.huaban.analysis.jieba.JiebaSegmenter;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class JiebaPlagiarismTest {
    private double calculateJaccardSimilarity(String originalText, String plagiarizedText) {
        JiebaSegmenter segmenter = new JiebaSegmenter();

        Set<String> originalWords = new HashSet<>(segmenter.sentenceProcess(originalText));
        Set<String> plagiarizedWords = new HashSet<>(segmenter.sentenceProcess(plagiarizedText));

        // 计算交集和并集
        Set<String> intersection = new HashSet<>(originalWords);
        intersection.retainAll(plagiarizedWords);

        Set<String> union = new HashSet<>(originalWords);
        union.addAll(plagiarizedWords);

        // Jaccard 相似度计算
        return union.isEmpty() ? 0 : (double) intersection.size() / union.size() * 100;
    }

    @Test
    public void testExactMatch() {
        double similarityRate = calculateJaccardSimilarity("今天是星期天，天气晴，今天晚上我要去看电影。",
                "今天是星期天，天气晴，今天晚上我要去看电影。");
        System.out.printf("%.2f", similarityRate);
    }

    @Test
    public void testReverseString() {
        double similarityRate = calculateJaccardSimilarity("今天是星期天，天气晴，今天晚上我要去看电影。",
                "电影看去我要晚上今天，晴天气，天星期是今天。");
        System.out.printf("%.2f", similarityRate);
    }

    @Test
    public void testWithSymbols() {
        double similarityRate = calculateJaccardSimilarity("今天是星期天，天气晴，今天晚上我要去看电影。",
                "今天是星期天，天气晴，今天晚上我要去看电影！");
        System.out.printf("%.2f", similarityRate);
    }

    @Test
    public void testPartialChange() {
        double similarityRate = calculateJaccardSimilarity("今天是星期天，天气晴，今天晚上我要去看电影。",
                "今天是星期天，天气晴，今晚上我要去看电影。");
        System.out.printf("%.2f", similarityRate);
    }

    @Test
    public void testPartialMissing() {
        double similarityRate = calculateJaccardSimilarity("今天是星期天，天气晴，今天晚上我要去看电影。",
                "今天是星期天，天气晴，今晚上要去看电影。");
        System.out.printf("%.2f", similarityRate);
    }

    @Test
    public void testEmptyString() {
        double similarityRate = calculateJaccardSimilarity("", "");
        System.out.printf("%.2f", similarityRate);
    }
}
