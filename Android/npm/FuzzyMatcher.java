package com.tom.npm;

import java.util.ArrayList;
import java.util.List;

public class FuzzyMatcher {

    // 模糊匹配方法
    public static List<String> fuzzyMatch(String inputName, List<String> plantNames) {
        List<String> matchedNames = new ArrayList<>();

        // 将输入名称拆分为单词
        String[] inputWords = inputName.toLowerCase().split("\\s+");

        // 遍历植物名称列表
        for (String plantName : plantNames) {
            // 将植物名称拆分为单词
            String[] plantWords = plantName.toLowerCase().split("\\s+");

            // 计算匹配度
            int matchCount = 0;
            for (String inputWord : inputWords) {
                for (String plantWord : plantWords) {
                    if (plantWord.contains(inputWord)) {
                        matchCount++;
                        break;
                    }
                }
            }

            // 如果匹配度超过阈值，将该植物名称添加到匹配列表中
            if (matchCount > Math.min(inputWords.length, plantWords.length) / 2) {
                matchedNames.add(plantName);
            }
        }

        return matchedNames;
    }
}
