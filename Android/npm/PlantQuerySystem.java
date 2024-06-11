package com.tom.npm;

import java.util.HashMap;
import java.util.Map;

public class PlantQuerySystem {
    private Map<String, String> plantNameMap = new HashMap<String, String>() {{
        put("绿萝", "Heart-leaf philodendron");
        put("风铃花", "Campanula medium L.");
        put("茉莉花", "Orange jasmine");
        put("栀子花", "Sweetbay Magnolia");
        put("仙人掌", "California hedgehog");
        put("不死鸟", "Mother of millions");
        put("芦荟", "Mitra aloe");
        put("睡莲", "Red and blue water-lily");
        put("吊兰", "Spider Plant");
        put("发财树", "Money Plant");
        put("小叶赤楠", "Pondo Waterberry");
        put("洋桔梗", "Lisianthus");
        put("洋甘菊", "Feverfew");
        put("郁金香", "Didier's tulip");
        put("玫瑰", "Rose");
        put("向日葵", "Sunflower");
        put("蒲公英", "Common dandelion");
        put("豌豆苗", "Garden cress");
        put("吊竹梅", "Wandering-Jew");
        put("虎尾兰", "Spotted peperomia");
        put("芳香万寿菊", "Sweet sultan");
        put("虞美人", "Common poppy");
        put("凤尾竹", "Indian-nut");
        put("月季", "Austrian copper rose");
        put("多肉植物", "Red top queen");
        put("薄荷", "Fragrant padritree");
        put("宝石花", "Blue Echeveria");
        put("鹤望兰", "Bird of Paradise");
        put("白掌", "Peace lily");
        put("铁线蕨", "Boston fern");
        put("马蹄莲", "Calla lily");
        put("大花蕙兰", "Orchid");
        put("富贵竹", "Lucky Bamboo");
        put("铁兰", "Pink quill");
        put("沙漠玫瑰", "Desert-rose");
        put("火炬花", "Torch Ginger");
        put("万年青", "Dumb cane");
        put("狐尾兰", "Spotted peperomia");
        put("海桐", "Australian Laurel");
        put("虹之玉", "Jelly beans");
        put("凤梨", "Pineapple");
        put("雏菊", "Wild Daisy");
        put("马齿苋", "Common Purselane");
        put("室内植物竹", "Lucky Bamboo");
        put("阔叶紫云英", "Wild verbena");
        put("铜钱草", "Whorled marshpennywort");
        put("银皇后", "Dumb cane");
        put("黑玫瑰", "Black rose");
        put("九里香", "Orange jasmine");
        put("彩叶草", "Coleus");
        put("钱串", "Rosary Plant");
        put("蕙兰", "Fukien-orchid");
        put("金边吊兰", "Spider plant");
        put("蕃茄", "Garden tomato");
        put("金钱树", "Money Plant");
        put("室内松树", "Jade plant");
        put("长春花", "Five-needle pine");
        put("圣诞红", "Poinsettia");
        put("蓝雪花", "Plumbago");
        put("海棠花", "Paradise Apple");
        put("金盏花", "Marsh Marigold");
        put("白鹤芋", "Peace lily");
        put("金鱼草", "Snapdragon");
        put("紫藤", "Japanese wisteria");
        put("紫苑", "New England Aster");
        put("水培芦荟", "Mitra aloe");
        put("香蕉植物", "Flowering banana");
        put("矮牵牛", "Common garden petunia");
        put("令箭荷花", "Orchid Cactus");
        put("小香松,金冠柏", "Monterey cypress");
        put("巴西木，香龙血树", "Monterey cypress");
        put("山茶花", "Camellia");
        put("菩提树", "Mistletoe fig");
        put("含羞草", "Touch-me-not");
        put("文竹", "Asparagus fern");
        put("椰子", "Caribbean royal palm");
        put("竹子", "Lucky Bamboo");
        put("金钱木", "Lucky Bamboo");
        put("香水柠檬树", "Lemon");
        put("桂花", "Sweet osmanthus");
        put("柠檬薄荷", "Lemon Balm");
        put("紫苏", "Coleus");
        put("红枫", "Japanese maple");
        put("草莓", "Garden Strawberry");
        put("绣球花", "Hydrangea");
        put("香水百合", "Candlestick lily");
        put("四叶草", "Common yellow wood-sorrel");
        put("紫花地丁", "Chinese violet");

    }};

    public String processQuery(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return null;
        }

        String normalizedInput = userInput.toLowerCase();
        for (String plant : plantNameMap.keySet()) {
            if (normalizedInput.contains(plant.toLowerCase())) {
                return getEnglishName(plant); // 找到植物名称，返回植物的英文名称
            }
        }

        return null; // 未找到植物名称
    }




    public String getEnglishName(String chineseName) {
        return plantNameMap.getOrDefault(chineseName, "Unknown"); // 如果未找到匹配项，则返回 "Unknown"
    }









}
