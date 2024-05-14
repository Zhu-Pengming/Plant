package com.example.npm;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.google.gson.JsonObject;

import org.tensorflow.lite.Interpreter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatsRV;
    private ImageView sendMsgIB,sendImageIB;
    private EditText userMsgEdt;
    private ArrayList<Message> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;
    private RecognitionService recognitionService; // Corrected the semicolon

    private Interpreter tflite;

    private String USER_KEY = "user";
    private String BOT_KEY = "bot";
    private final int CAMERA_REQ_CODE = 100;
    static Uri uri;
    ImageView imgCamera;
    private static final String FOLDER_NAME = "MyAppImages";

    private static final int OPEN_GALLERY_REQUEST_CODE = 102;

    private ChatViewModel viewModel;

    private String identifyspiece;

    private Context context;

    private String appId = "20240514002051247";//APP ID 来源于百度翻译平台 请使用自己的
    private String key = "UpI1T3c9vqa_J8rig85V";//秘钥 来源于百度翻译平台 请使用自己的



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);


        messageModalArrayList = new ArrayList<>();
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        chatsRV.setLayoutManager(linearLayoutManager);
        chatsRV.setAdapter(messageRVAdapter);

        // ！！！！ idIBPicture  触发反应
        sendImageIB =findViewById(R.id.idIBPicture);
        sendImageIB.setOnClickListener(v -> showPopupWindow(v));

        sendMsgIB.setOnClickListener(v -> {
            if (userMsgEdt.getText().toString().isEmpty()) {
                Toast.makeText(ChatActivity.this, "Please enter your message.", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(userMsgEdt.getText().toString(), uri.getPath());
                userMsgEdt.setText("");
            }
        });
    }

    private void sendMessage(String userMsg, String userImageUri) {
        if (userMsg.isEmpty() && userImageUri == null) {
            Toast.makeText(this, "Please enter a message or select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        messageModalArrayList.add(new Message(userMsg, userImageUri, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();
        chatsRV.scrollToPosition(messageModalArrayList.size() - 1);

        if (userImageUri != null) {
            // Load image as Bitmap from URI
            Bitmap image = loadImageFromUri(Uri.parse(userImageUri));
            if (image != null) {
                if (!userMsg.isEmpty()) {
                    processImageAndText(image, userMsg);
                } else {
                    processImageOnly(image);
                }
            }
        } else {
            processTextOnly(userMsg);
        }

        long timestamp = System.currentTimeMillis();
        ChatSession newSession = new ChatSession("Session Name", timestamp);
        viewModel.insertChatSession(newSession);
        userMsgEdt.setText(""); // Clear the text input after sending
    }

    private Bitmap loadImageFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processImageOnly(Bitmap image) {
        Call<JsonObject> call = recognitionService.getSpeciesWithImage(image, "en", "kt");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<com.google.gson.JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    com.google.gson.JsonObject jsonObject = response.body();
                    // Process the jsonObject as needed

                    identifyspiece = jsonObject.toString();

                    // ChatSession Name 显示
                } else {
                    // Handle error
                    Log.e("API_ERROR", "Response was successful but no data was returned");
                }
            }

            @Override
            public void onFailure(Call<com.google.gson.JsonObject> call, Throwable t) {
                // Handle failure, such as a network error
                Log.e("NETWORK_FAILURE", "Failed to reach the server", t);
                runOnUiThread(() -> { // Inform the user of the network issue
                    Toast.makeText(context, "Network failure, please check your connection: " + t.getMessage(), Toast.LENGTH_LONG).show(); });
            }
        });
    }

    private void processImageAndText(Bitmap image, String text) {
        // Combined image and text processing logic

        processImageOnly(image);

        processTextOnly(text);
    }

    public List<String> processTextWithModel1(String text) {
        // This is a stub function. Replace it with actual model processing code.
        // Let's assume this model returns a list of aspects.
        return Arrays.asList(text.split(" ")); // Simplistic splitting; replace with actual logic.
    }

    private void processTextOnly(String text) {

        // 转英文

        // translation(text,"zh","en");

        List<String> aspects = processTextWithModel1(text);
        // Assuming 'identifyspiece' is a string that you've prepared or retrieved


        // Combine aspects and identifyspiece into a single string or structured format as needed
        String combinedInput = String.join(" ", aspects) + " " + identifyspiece;

        //转英文
        // translation(text,"zh","en");

        // Now process with Model 2
        String botResponse = processWithModel2(combinedInput, text); // where 'text' could be context

        //// translation(text,"en","zh");

        // 转中文
        updateChatWithBotResponse(botResponse);
    }

    private String processWithModel2(String input, String context) {
        Model2Executor model2Executor = new Model2Executor(getAssets());
        return model2Executor.executeModelOnInput(input, context);
    }
    private void updateChatWithBotResponse(String botMessage) {
        Message botMessageObject = new Message(botMessage,null, BOT_KEY);
        messageModalArrayList.add(botMessageObject); messageRVAdapter.notifyDataSetChanged();
        chatsRV.scrollToPosition(messageModalArrayList.size() - 1);
    }

    public void showPopupWindow(View anchorView) {
        // 加载布局文件
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // 创建PopupWindow对象
        PopupWindow popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true);

        // 设置点击窗口外让窗口消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 必须设置背景才能消失

        // 设置按钮事件
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnCamera = popupView.findViewById(R.id.btn_camera);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnGallery = popupView.findViewById(R.id.btn_gallery);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnExit = popupView.findViewById(R.id.btn_exit);

        btnCamera.setOnClickListener(view -> {
            Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(iCamera, CAMERA_REQ_CODE);

            popupWindow.dismiss();
        });

        btnGallery.setOnClickListener(view -> {
            // 处理相册事件
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, OPEN_GALLERY_REQUEST_CODE);

            popupWindow.dismiss();
        });

        btnExit.setOnClickListener(view -> {
            // 处理退出事件
            popupWindow.dismiss();
        });

        // 显示PopupWindow
        popupWindow.showAsDropDown(anchorView);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQ_CODE:
                    if (data != null) {
                        handleCameraResult(data);
                    }
                    break;
                case OPEN_GALLERY_REQUEST_CODE:
                    handleGalleryResult(data);
                    break;
            }
        }
    }

    private void handleCameraResult(Intent data) {
        Bitmap img = (Bitmap) data.getExtras().get("data");
        imgCamera.setImageBitmap(img);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File folder = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = timeStamp + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + FOLDER_NAME);
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                img.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                outputStream.close();
                Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGalleryResult(Intent data) {
        uri = data.getData();
        // Additional logic to handle the selected image
    }



    /**
     * 翻译
     */
    private void translation(String text,String fromLanguage,String toLanguage) {
        //获取输入的内容
        String inputTx = text.trim();
        //判断输入内容是否为空
        if (!inputTx.isEmpty() || !"".equals(inputTx)) {//不为空
            tvTranslation.setText("翻译中...");
            tvTranslation.setEnabled(false);//不可更改，同样就无法点击
            String salt = num(1);//随机数
            //拼接一个字符串然后加密
            String spliceStr = appId + inputTx + salt + key;//根据百度要求 拼接
            String sign = stringToMD5(spliceStr);//将拼接好的字符串进行MD5加密   作为一个标识
            //异步Get请求访问网络
            asyncGet(inputTx, fromLanguage, toLanguage, salt, sign);
        } else {//为空
            showMsg("请输入要翻译的内容！");
        }
    }

    /**
     * 异步Get请求
     *
     * @param content  要翻译的内容
     * @param fromType 翻译源语言
     * @param toType   翻译后语言
     * @param salt     随机数
     * @param sign     标识
     */
    private void asyncGet(String content, String fromType, String toType, String salt, String sign) {
        //通用翻译API HTTP地址：
        //http://api.fanyi.baidu.com/api/trans/vip/translate
        //通用翻译API HTTPS地址：
        //https://fanyi-api.baidu.com/api/trans/vip/translate

        String httpStr = "http://api.fanyi.baidu.com/api/trans/vip/translate";
        String httpsStr = "https://fanyi-api.baidu.com/api/trans/vip/translate";
        //拼接请求的地址
        String url = httpsStr +
                "?appid=" + appId + "&q=" + content + "&from=" + fromType + "&to=" +
                toType + "&salt=" + salt + "&sign=" + sign;
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                //异常返回
                goToUIThread(e.toString(), 0);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //正常返回
                goToUIThread(response.body().toString(), 1);

            }
        });
    }

    /**
     * 接收到返回值后，回到UI线程操作页面变化
     *
     * @param object 接收一个返回对象
     * @param key    表示正常还是异常
     */
    private void goToUIThread(final Object object, final int key) {
        //切换到主线程处理数据
        ChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTranslation.setText("翻译");
                tvTranslation.setEnabled(true);

                if (key == 0) {//异常返回
                    showMsg("异常信息：" + object.toString());
                    Log.e("MainActivity",object.toString());
                } else {//正常返回
                    //通过Gson 将 JSON字符串转为实体Bean
                    final TranslateResult result = new Gson().fromJson(object.toString(), TranslateResult.class);
                    tvTranslation.setVisibility(View.GONE);
                    //显示翻译的结果

                    if(result.getTrans_result().get(0).getDst() == null){
                        showMsg("数据为空");
                    }
                    tvResult.setText(result.getTrans_result().get(0).getDst());
                    resultLay.setVisibility(View.VISIBLE);
                    beforeLay.setVisibility(View.GONE);
                    afterLay.setVisibility(View.VISIBLE);
                    //翻译成功后的语言判断显示
                    initAfter(result.getFrom(), result.getTo());
                }
            }
        });
    }

    /**
     * 随机数 (根据百度的要求需要一个随机数)
     */
    public static String num(int a) {
        Random r = new Random(a);
        int ran1 = 0;
        for (int i = 0; i < 5; i++) {
            ran1 = r.nextInt(100);
            System.out.println(ran1);
        }
        return String.valueOf(ran1);
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * Toast提示
     *
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
