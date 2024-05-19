package com.example.npm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BertPreprocessor {
    private static final int MAX_SEQ_LENGTH = 256; // 根据你的BERT模型设置最大长度

    // 简单的分词方法，实际应用中应该用更复杂的NLP工具
    private List<String> simpleTokenize(String text) {
        return Arrays.asList(text.toLowerCase().split("\\s+"));
    }

    // 将分词转换为token IDs，这需要一个词汇表（这里假设为静态初始化）
    private static final List<String> vocab = Arrays.asList("[PAD]", "[UNK]", "[CLS]", "[SEP]"); // 示例词汇表

    public int[] tokenizeAndConvertToInput(String question) {
        List<String> tokens = new ArrayList<>();
        tokens.add("[CLS]");
        tokens.addAll(simpleTokenize(question));
        tokens.add("[SEP]");

        int[] inputIds = new int[MAX_SEQ_LENGTH];
        int i = 0;
        for (String token : tokens) {
            int tokenId = vocab.indexOf(token);
            if (tokenId == -1) tokenId = vocab.indexOf("[UNK]"); // 使用[UNK]代替未知词
            inputIds[i++] = tokenId;
            if (i == MAX_SEQ_LENGTH) break;
        }
        // 使用[PAD]填充剩余的部分
        Arrays.fill(inputIds, i, MAX_SEQ_LENGTH, vocab.indexOf("[PAD]"));
        return inputIds;
    }

    // 生成注意力掩码
    public float[][] generateAttentionMask(int[] inputIds) {
        float[][] attentionMask = new float[1][inputIds.length];
        for (int i = 0; i < inputIds.length; i++) {
            attentionMask[0][i] = (inputIds[i] != vocab.indexOf("[PAD]")) ? 1.0f : 0.0f;
        }
        return attentionMask;
    }
}

