package com.example.npm;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Model2Executor {
    private Interpreter tflite;

    public Model2Executor(AssetManager assetManager) {
        try {
            tflite = new Interpreter(loadModelFile(assetManager, "bert_qa_model.tflite"));
        } catch (IOException e) {
            Log.e("tfliteSupport", "Error loading model", e);
        }
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelName) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String executeModelOnInput(String question, String context) {
        // 假设我们已经有一个方法来预处理问题和上下文
        float[][] input = preprocess(question, context);

        // BERT模型通常需要两种输出：开始和结束的索引
        float[][] outputStartLogits = new float[1][context.length()];  // 调整为适合你的模型
        float[][] outputEndLogits = new float[1][context.length()];    // 调整为适合你的模型

        // 输入数据需要包装成一个对象数组，传递给Interpreter
        Object[] inputArray = {input};

        // 输出数据也需要包装成一个Map对象，以便Interpreter可以填充数据
        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, outputStartLogits);
        outputMap.put(1, outputEndLogits);

        // 执行模型
        tflite.runForMultipleInputsOutputs(inputArray, outputMap);

        // 解析输出，获取答案的开始和结束位置
        int startIdx = argMax(outputStartLogits[0]);
        int endIdx = argMax(outputEndLogits[0]);

        // 根据索引从原文本中提取答案
        return extractAnswerByIndices(context, startIdx, endIdx);
    }

    private int argMax(float[] array) {
        int bestIdx = -1;
        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    private String extractAnswerByIndices(String context, int startIdx, int endIdx) {
        // 此处简化处理，实际使用时需要根据token位置转换为实际文字位置
        return context.substring(startIdx, Math.min(endIdx + 1, context.length()));
    }
    public float[][] preprocess(String question, String context) {
        BertPreprocessor preprocessor = new BertPreprocessor();
        int[] inputIds = preprocessor.tokenizeAndConvertToInput(question, context);
        float[][] attentionMask = preprocessor.generateAttentionMask(inputIds);

    // 根据你的模型输入需求，可能还需要其他步骤
    // 返回适当格式的数据
        return new float[][] {Arrays.stream(inputIds).asDoubleStream().toArray()};
    }
}
