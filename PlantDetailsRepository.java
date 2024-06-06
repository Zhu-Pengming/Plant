package com.example.npm;


import android.content.Context;
import android.content.res.AssetManager;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlantDetailsRepository {
    private List<PlantDetails> plantDetailsList;

    public PlantDetails getPlantDetailsByEnglishName(String englishName) {
        // 获取所有匹配的英文名
        List<String> matchedNames = FuzzyMatcher.fuzzyMatch(englishName, getAllEnglishNames());

        // 如果有匹配的英文名，返回第一个匹配的 PlantDetails 对象
        if (!matchedNames.isEmpty()) {
            for (PlantDetails plantDetails : plantDetailsList) {
                if (plantDetails.getEnglishName().equals(matchedNames.get(0))) {
                    return plantDetails;
                }
            }
        }

        // 如果没有找到匹配的对象，返回 null
        return null;
    }

    public List<String> getAllEnglishNames() {
        List<String> englishNames = new ArrayList<>();
        for (PlantDetails plantDetails : plantDetailsList) {
            englishNames.add(plantDetails.getEnglishName());
        }
        return englishNames;
    }

    // 构造函数
    public PlantDetailsRepository() {
        plantDetailsList = new ArrayList<>();
        // 在这里添加植物详细信息
        addPlantDetails();
    }

    public PlantDetails getPlantDetailsBySpecies(String species) {
        for (PlantDetails plantDetails : plantDetailsList) {
            if (plantDetails.getSpecies().equals(species)) {
                return plantDetails;
            }
        }
        return null;
    }



    // 添加植物详细信息的方法
    private void addPlantDetails() {

        plantDetailsList.add(new PlantDetails("绿萝", "是", "是", "可能", "室内植物", "多年生", "春季", "观赏", "Epipremnum aureum"));
        plantDetailsList.add(new PlantDetails("风铃花", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Campanula medium"));
        plantDetailsList.add(new PlantDetails("茉莉花", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Jasminum sambac"));
        plantDetailsList.add(new PlantDetails("栀子花", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Gardenia jasminoides"));
        plantDetailsList.add(new PlantDetails("仙人掌", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Cactaceae spp."));
        plantDetailsList.add(new PlantDetails("不死鸟", "否", "否", "否", "外部植物", "一年生", "秋季", "观赏", "Kalanchoe daigremontiana"));
        plantDetailsList.add(new PlantDetails("芦荟", "否", "是", "否", "室内植物", "一年生", "春季", "药用", "Aloe vera"));
        plantDetailsList.add(new PlantDetails("睡莲", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Nymphaea spp."));
        plantDetailsList.add(new PlantDetails("吊兰", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Chlorophytum comosum"));
        plantDetailsList.add(new PlantDetails("发财树", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Pachira aquatica"));
        plantDetailsList.add(new PlantDetails("小叶赤楠", "是", "否", "否", "室内植物", "一年生", "春季", "观赏", "Syzygium buxifolium"));
        plantDetailsList.add(new PlantDetails("洋桔梗", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Eustoma grandiflorum"));
        plantDetailsList.add(new PlantDetails("洋甘菊", "否", "是", "否", "室内植物", "多年生", "春季", "药用", "Matricaria chamomilla"));
        plantDetailsList.add(new PlantDetails("郁金香", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Tulipa spp."));
        plantDetailsList.add(new PlantDetails("玫瑰", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Rosa spp."));
        plantDetailsList.add(new PlantDetails("向日葵", "否", "是", "可能", "外部植物", "一年生", "秋季", "观赏", "Helianthus annuus"));
        plantDetailsList.add(new PlantDetails("蒲公英", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Taraxacum officinale"));
        plantDetailsList.add(new PlantDetails("豌豆苗", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Pisum sativum"));
        plantDetailsList.add(new PlantDetails("吊竹梅", "否", "是", "否", "室内植物", "一年生", "春季", "药用", "Tradescantia zebrina"));
        plantDetailsList.add(new PlantDetails("虎尾兰", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Sansevieria trifasciata"));
        plantDetailsList.add(new PlantDetails("芳香万寿菊", "是", "否", "否", "室内植物", "多年生", "春季", "观赏", "Tagetes lucida"));
        plantDetailsList.add(new PlantDetails("虞美人", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Papaver rhoeas"));
        plantDetailsList.add(new PlantDetails("凤尾竹", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Bambusa multiplex"));
        plantDetailsList.add(new PlantDetails("月季", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Rosa chinensis"));
        plantDetailsList.add(new PlantDetails("多肉植物", "否", "是", "否", "室内植物", "多年生", "春季", "药用", "Succulent plants"));
        plantDetailsList.add(new PlantDetails("薄荷", "否", "否", "否", "外部植物", "一年生", "秋季", "观赏", "Mentha spp."));
        plantDetailsList.add(new PlantDetails("宝石花", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Echeveria spp."));
        plantDetailsList.add(new PlantDetails("鹤望兰", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Strelitzia reginae"));
        plantDetailsList.add(new PlantDetails("白掌", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Spathiphyllum spp."));
        plantDetailsList.add(new PlantDetails("铁线蕨", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Adiantum spp."));
        plantDetailsList.add(new PlantDetails("马蹄莲", "是", "是", "可能", "室内植物", "一年生", "春季", "观赏", "Zantedeschia aethiopica"));
        plantDetailsList.add(new PlantDetails("大花蕙兰", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Cymbidium spp."));
        plantDetailsList.add(new PlantDetails("富贵竹", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Dracaena sanderiana"));
        plantDetailsList.add(new PlantDetails("铁兰", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Tillandsia spp."));
        plantDetailsList.add(new PlantDetails("沙漠玫瑰", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Adenium obesum"));
        plantDetailsList.add(new PlantDetails("火炬花", "否", "否", "否", "外部植物", "一年生", "秋季", "观赏", "Kniphofia uvaria"));
        plantDetailsList.add(new PlantDetails("万年青", "否", "是", "否", "室内植物", "多年生", "春季", "药用", "Rohdea japonica"));
        plantDetailsList.add(new PlantDetails("狐尾兰", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Asparagus densiflorus"));
        plantDetailsList.add(new PlantDetails("海桐", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Pittosporum tobira"));
        plantDetailsList.add(new PlantDetails("虹之玉", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Sedum rubrotinctum"));
        plantDetailsList.add(new PlantDetails("凤梨", "是", "否", "否", "室内植物", "多年生", "春季", "观赏", "Ananas comosus"));
        plantDetailsList.add(new PlantDetails("雏菊", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Bellis perennis"));
        plantDetailsList.add(new PlantDetails("马齿苋", "否", "是", "否", "室内植物", "一年生", "春季", "药用", "Portulaca oleracea"));
        plantDetailsList.add(new PlantDetails("室内植物竹", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Dracaena sanderiana"));
        plantDetailsList.add(new PlantDetails("阔叶紫云英", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Verbena officinalis"));
        plantDetailsList.add(new PlantDetails("铜钱草", "否", "是", "可能", "外部植物", "一年生", "秋季", "观赏", "Hydrocotyle vulgaris"));
        plantDetailsList.add(new PlantDetails("银皇后", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Dieffenbachia spp."));
        plantDetailsList.add(new PlantDetails("黑玫瑰", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Aeonium arboreum 'Zwartkop'"));
        plantDetailsList.add(new PlantDetails("九里香", "否", "是", "否", "室内植物", "多年生", "春季", "药用", "Murraya paniculata"));
        plantDetailsList.add(new PlantDetails("彩叶草", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Plectranthus scutellarioides"));
        plantDetailsList.add(new PlantDetails("钱串", "是", "否", "否", "室内植物", "一年生", "春季", "观赏", "Crassula perforata"));
        plantDetailsList.add(new PlantDetails("蕙兰", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Cymbidium spp."));
        plantDetailsList.add(new PlantDetails("金边吊兰", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Chlorophytum comosum"));
        plantDetailsList.add(new PlantDetails("蕃茄", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Solanum lycopersicum"));
        plantDetailsList.add(new PlantDetails("金钱树", "否", "是", "否", "室内植物", "一年生", "春季", "药用", "Zamioculcas zamiifolia"));
        plantDetailsList.add(new PlantDetails("室内松树", "否", "否", "否", "外部植物", "一年生", "秋季", "观赏", "Araucaria heterophylla"));
        plantDetailsList.add(new PlantDetails("长春花", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Catharanthus roseus"));
        plantDetailsList.add(new PlantDetails("圣诞红", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Euphorbia pulcherrima"));
        plantDetailsList.add(new PlantDetails("蓝雪花", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Plumbago auriculata"));
        plantDetailsList.add(new PlantDetails("海棠花", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Malus spp."));
        plantDetailsList.add(new PlantDetails("金盏花", "是", "是", "可能", "室内植物", "多年生", "春季", "观赏", "Calendula officinalis"));
        plantDetailsList.add(new PlantDetails("白鹤芋", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Spathiphyllum spp."));
        plantDetailsList.add(new PlantDetails("金鱼草", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Antirrhinum majus"));
        plantDetailsList.add(new PlantDetails("紫藤", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Wisteria sinensis"));
        plantDetailsList.add(new PlantDetails("紫苑", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Symphyotrichum novae-angliae"));
        plantDetailsList.add(new PlantDetails("水培芦荟", "否", "否", "否", "外部植物", "一年生", "秋季", "观赏", "Aloe vera"));
        plantDetailsList.add(new PlantDetails("香蕉植物", "否", "是", "否", "室内植物", "一年生", "春季", "药用", "Musa spp."));
        plantDetailsList.add(new PlantDetails("矮牵牛", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Petunia spp."));
        plantDetailsList.add(new PlantDetails("令箭荷花", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Epiphyllum spp."));
        plantDetailsList.add(new PlantDetails("小香松", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Santolina chamaecyparissus"));
        plantDetailsList.add(new PlantDetails("金冠柏", "是", "否", "否", "室内植物", "一年生", "春季", "观赏", "Cupressus macrocarpa"));
        plantDetailsList.add(new PlantDetails("巴西木", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Dracaena fragrans"));
        plantDetailsList.add(new PlantDetails("香龙血树", "否", "是", "否", "室内植物", "多年生", "春季", "药用", "Dracaena cinnabari"));
        plantDetailsList.add(new PlantDetails("山茶花", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Camellia japonica"));
        plantDetailsList.add(new PlantDetails("菩提树", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Ficus religiosa"));
        plantDetailsList.add(new PlantDetails("含羞草", "否", "是", "可能", "外部植物", "一年生", "秋季", "观赏", "Mimosa pudica"));
        plantDetailsList.add(new PlantDetails("文竹", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Asparagus setaceus"));
        plantDetailsList.add(new PlantDetails("椰子", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Cocos nucifera"));
        plantDetailsList.add(new PlantDetails("竹柏", "否", "是", "否", "室内植物", "一年生", "春季", "药用", "Podocarpus macrophyllus"));
        plantDetailsList.add(new PlantDetails("金钱木", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Crassula ovata"));
        plantDetailsList.add(new PlantDetails("香水柠檬树", "是", "否", "否", "室内植物", "多年生", "春季", "观赏", "Citrus limon"));
        plantDetailsList.add(new PlantDetails("桂花", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Osmanthus fragrans"));
        plantDetailsList.add(new PlantDetails("柠檬薄荷", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Melissa officinalis"));
        plantDetailsList.add(new PlantDetails("紫苏", "否", "否", "否", "外部植物", "一年生", "秋季", "药用", "Perilla frutescens"));
        plantDetailsList.add(new PlantDetails("红枫", "否", "是", "否", "室内植物", "多年生", "春季", "药用", "Acer rubrum"));
        plantDetailsList.add(new PlantDetails("草莓", "否", "否", "否", "外部植物", "一年生", "秋季", "观赏", "Fragaria × ananassa"));
        plantDetailsList.add(new PlantDetails("绣球花", "否", "否", "否", "室内植物", "一年生", "春季", "药用", "Hydrangea macrophylla"));
        plantDetailsList.add(new PlantDetails("香水百合", "否", "是", "否", "外部植物", "一年生", "秋季", "药用", "Lilium longiflorum"));
        plantDetailsList.add(new PlantDetails("四叶草", "否", "否", "否", "室内植物", "多年生", "春季", "药用", "Oxalis tetraphylla"));
    }

    // 获取所有植物详细信息的方法
    public List<PlantDetails> getAllPlantDetails() {
        return plantDetailsList;
    }
}
