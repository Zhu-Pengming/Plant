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

public class PlantDetailsRepository {
    private List<PlantDetails> plantDetailsList = new ArrayList<>();

    public void readExcelFile(Context context, String plantName) {
        try {
            // 打开 assets 文件夹中的 Excel 文件
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("data.xlsx");

            // 读取 Excel 文件
            EasyExcel.read(inputStream, PlantDetails.class, new AnalysisEventListener<PlantDetails>() {
                @Override
                public void invoke(PlantDetails data, AnalysisContext context) {
                    // 如果植物名称匹配，将这行数据添加到列表中
                    if (data.getSpecies().equals(plantName)) {
                        plantDetailsList.add(data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 数据读取完成
                }
            }).doReadAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PlantDetails> getPlantDetailsList() {
        return plantDetailsList;
    }
}
