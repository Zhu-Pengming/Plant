package com.example.npm;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class PlantQuerySystem {
    private List<String> knownSpecies = Arrays.asList(
            "绿萝",
            "风铃花",
            "茉莉花",
            "栀子花",
            "仙人掌",
            "不死鸟",
            "芦荟",
            "睡莲",
            "吊兰",
            "发财树",
            "小叶赤楠",
            "洋桔梗",
            "洋甘菊",
            "郁金香",
            "玫瑰",
            "向日葵",
            "蒲公英",
            "豌豆苗",
            "吊竹梅",
            "虎尾兰",
            "芳香万寿菊",
            "虞美人",
            "凤尾竹",
            "月季",
            "多肉植物",
            "薄荷",
            "宝石花",
            "鹤望兰",
            "白掌",
            "铁线蕨",
            "马蹄莲",
            "大花蕙兰",
            "富贵竹",
            "铁兰",
            "沙漠玫瑰",
            "火炬花",
            "万年青",
            "狐尾兰",
            "海桐",
            "虹之玉",
            "凤梨",
            "雏菊",
            "马齿苋",
            "室内植物竹",
            "阔叶紫云英",
            "铜钱草",
            "银皇后",
            "黑玫瑰",
            "九里香",
            "彩叶草",
            "钱串",
            "蕙兰",
            "金边吊兰",
            "蕃茄",
            "金钱树",
            "室内松树",
            "长春花",
            "圣诞红",
            "蓝雪花",
            "海棠花",
            "金盏花",
            "白鹤芋",
            "金鱼草",
            "紫藤",
            "紫苑",
            "水培芦荟",
            "香蕉植物",
            "矮牵牛",
            "令箭荷花",
            "小香松,金冠柏",
            "巴西木，香龙血树",
            "山茶花",
            "菩提树",
            "含羞草",
            "文竹",
            "椰子",
            "竹柏",
            "金钱木",
            "香水柠檬树",
            "桂花",
            "柠檬薄荷",
            "紫苏",
            "红枫",
            "草莓",
            "绣球花",
            "香水百合",
            "四叶草",
            "紫花地丁"
    );



    private final LevenshteinDistance distanceCalculator = new LevenshteinDistance();
    private final int MATCH_THRESHOLD =2; // 可根据需要调整阈值



    public String processQuery(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return null;
        }

        String normalizedInput = userInput.toLowerCase();
        for (String plant : knownSpecies) {
            if (normalizedInput.contains(plant.toLowerCase())) {
                return plant; // 找到植物名称，返回植物名称
            }
        }

        return null; // 未找到植物名称
    }





    private String fetchInformation(String plant) {
        return "Detailed care instructions for " + plant + ".";
    }

}
