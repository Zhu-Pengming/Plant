package com.example.npm;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.Manifest;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 1;
    private RecyclerView chatsRV;
    private ImageView sendMsgIB,sendImageIB;
    private EditText userMsgEdt;
    private ArrayList<Message> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;
    private RecognitionService recognitionService; // Corrected the semicolon

    private Interpreter tflite;

    private static final String API_URL = "https://api.openai-proxy.org/v1/chat/completions";
    private static final String API_KEY = "sk-O1RiVN9ZHxJBfYfMruhixCrBJE72Ds9lhYKi2R1M2f0WKL8s";

    private String TAG = "Camera-Test";

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

    private TranslationService translationService;

    private String baiduBaikeInfo;

    private boolean IsExistText;

    private String resultText;

    private int currentSequenceNumber = 0;

    private ChatSessionDao chatSessionDao;
    private ChatSessionAdapter chatSessionAdapter;

    private List<String> knownSpecies = Arrays.asList(
            "Pothos", "Brugmansia", "Jasmine", "Gardenia", "Cactus", "Phoenix palm", "Aloe", "Water lily",
            "Spider plant", "Money tree", "Melaleuca bracteata", "Eustoma", "Chamomile", "Tulip", "Rose",
            "Sunflower", "Dandelion", "Pea shoots", "Medinilla magnifica", "Sansevieria", "Tagetes", "Poppy",
            "Fern", "Chinese rose", "Succulents", "Mint", "Lithops", "Crinum", "Spathiphyllum", "Asparagus fern",
            "Horseshoe geranium", "Large-flowered Cymbidium", "Lucky bamboo", "Aspidistra", "Adenium", "Kniphofia",
            "Rohdea japonica", "Foxtail agave", "Pittosporum tobira", "Haworthia coarctata", "Pineapple", "Daisy",
            "Purslane", "Indoor bamboo", "Broadleaf purple clover", "Pennywort", "Silver queen pothos",
            "Black rose succulent", "Orange jessamine", "Coleus", "Chinese money plant", "Cymbidium orchid",
            "Variegated spider plant", "Tomato", "Jade plant", "Indoor pine", "Periwinkle", "Poinsettia",
            "Blue plumbago", "Chinese crabapple", "Calendula", "Calla lily", "Goldfish plant", "Wisteria",
            "Aster", "Hydroponic aloe", "Banana plant", "Petunia", "Nopalxochia ackermannii", "Goldcrest",
            "Dracaena fragrans", "Camellia oleifera", "Ficus religiosa", "Mimosa pudica", "Asparagus setaceus",
            "Cocos nucifera", "Podocarpus nagi", "Portulaca molokiniensis", "Citrus × limon 'Rosso'",
            "Osmanthus fragrans", "Mentha citrata", "Perilla frutescens", "Red Maple", "Strawberry", "Hydrangea",
            "Lily","Viola phillipina", "Four Leaf Clover"
    );// Replace with actual data source

    private ImageView returnTo,setting;

    private WebView webView;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);

        translationService = new TranslationService();
        messageModalArrayList = new ArrayList<>();
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        chatsRV.setLayoutManager(linearLayoutManager);
        chatsRV.setAdapter(messageRVAdapter);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "chat_sessions")
                .fallbackToDestructiveMigration()
                .build();

        chatSessionDao = db.chatSessionDao();


        returnTo = findViewById(R.id.chat_return);
        returnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                overridePendingTransition(R.anim.default_anim_out, R.anim.default_anim_in);
                startActivity(intent);
            }
        });
        setting = findViewById(R.id.chat_setting);



        webView = findViewById(R.id.tes);
        webView.clearCache(true);
        webView.loadUrl("file:///android_asset/tea.html");
        webView.setVisibility(View.GONE);




        // 添加机器人的欢迎消息
        addBotWelcomeMessage();

        sendImageIB =findViewById(R.id.idIBPicture);
        sendImageIB.setOnClickListener(v -> showPopupWindow(v));

        sendMsgIB.setOnClickListener(v -> {



            if (userMsgEdt.getText().toString().isEmpty() && uri == null) {
                Toast.makeText(ChatActivity.this, "Please enter a message or select an image.", Toast.LENGTH_SHORT).show();
            } else {
                webView.setVisibility(View.VISIBLE);
                if (uri != null) {
                    sendMessage(userMsgEdt.getText().toString(), uri.getPath());


                    userMsgEdt.setText("");
                } else {
                    String inputText = userMsgEdt.getText().toString();
                    String species = findSpeciesInText(inputText, knownSpecies);

                    if (species != null) {
                        String resultText1 = processTextWithModel1(inputText).toString();
                        if (resultText1 != null) {
                            processTextOnly(resultText1);
                        } else {
                            GPTAI(inputText+species);
                        }
                    } else {
                        GPTAI(inputText);
                    }

                    updateUserChat1(userMsgEdt.getText().toString());
                    userMsgEdt.setText("");
                }
            }
        });
    }


    private void GPTAI(String input) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                // Create JSON for the body
                JSONObject message = new JSONObject();
                message.put("role", "user");

                // Translate user's input to English
                TranslationService translationService = new TranslationService();
                translationService.translate(input, "zh", "en", new TranslationCallback() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(String translatedText) {
                        try {
                            String transMessage = parseTranslationResult(translatedText);

                            message.put("content", transMessage);

                            JSONArray messages = new JSONArray();
                            messages.put(message);

                            JSONObject bodyJson = new JSONObject();
                            bodyJson.put("messages", messages);
                            bodyJson.put("model", "gpt-3.5-turbo");

                            MediaType mediaType = MediaType.parse("application/json");
                            RequestBody body = RequestBody.create(mediaType, bodyJson.toString());

                            Request request = new Request.Builder()
                                    .url(API_URL)
                                    .post(body)
                                    .addHeader("Authorization", "Bearer " + API_KEY)
                                    .addHeader("Content-Type", "application/json")
                                    .build();

                            okhttp3.Response responseText = client.newCall(request).execute();
                            String responseBody = responseText.body().string();

                            // Parse JSON response
                            JsonParser parser = new JsonParser();
                            JsonElement jsonElement = parser.parse(responseBody);
                            JsonObject jsonResponse = jsonElement.getAsJsonObject();
                            String botResponse = jsonResponse.getAsJsonArray("choices")
                                    .get(0).getAsJsonObject()
                                    .get("message").getAsJsonObject()
                                    .get("content").getAsString();

                            // Translate bot's response to Chinese
                            translationService.translate(botResponse, "en", "zh", new TranslationCallback() {
                                @Override
                                public void onFailure(Exception e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onSuccess(String translatedText) throws JSONException {

                                    String transMessage1 = parseTranslationResult(translatedText);
                                    // Decode Unicode characters
                                    String decodedText = decodeUnicode(transMessage1);


                                    // Update the UI on the main thread
                                    runOnUiThread(() -> {
                                        // Display the translated text
                                        updateChatWithBotResponse(decodedText);
                                    });


                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public String parseTranslationResult(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray transResult = jsonObject.getJSONArray("trans_result");
        JSONObject firstResult = transResult.getJSONObject(0);
        String translatedText = firstResult.getString("dst");
        return translatedText;
    }

    @SuppressLint("SuspiciousIndentation")
    public static String decodeUnicode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuilder retBuf = new StringBuilder();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                try {
                    retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                    i += 5;
                } catch (NumberFormatException localNumberFormatException) {
                    retBuf.append(unicodeStr.charAt(i));
                }
            else
                retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }

    private String findSpeciesInText(String text, List<String> knownSpecies) {
        for (String species : knownSpecies) {
            if (text.contains(species)) {
                return species;
            }
        }
        return null;
    }


    private void sendMessage(String userMsg, String userImageUri) {
        if ((userMsg == null || userMsg.isEmpty()) && userImageUri == null) {
            Toast.makeText(this, "Please enter a message or select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userMsg != null && !userMsg.isEmpty()) {
            updateUserChat1(userMsg);
        }

        if (userImageUri != null) {

            if (uri != null) {

                updateUserChat2(String.valueOf(uri));
            }
        }

        userMsgEdt.setText(""); // Clear the text input after sending
    }

    private void processImageOnly(Bitmap image) {
        // Assuming the image is converted to a format acceptable by the recognition service
        Call<JsonObject> call = recognitionService.getSpeciesWithImage(image, "en", "kt");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        // Extract species name or ID from the response
                        identifyspiece = jsonObject.get("speciesName").getAsString(); // Adjust according to actual JSON structure

                        if (IsExistText) {
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
                            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            try {
                                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                                if (outputStream != null) {
                                    image.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                                    outputStream.close();

                                    Toast.makeText(ChatActivity.this, "Image saved successfully", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // Get the actual file path
                            String imagePath = getPathFromUri(ChatActivity.this, uri);

                            new ImageToText(new OnTaskCompleted() {
                                @Override
                                public void onTaskCompleted(String result) {
                                    // Handle the result here
                                    // For example, you can display the result in a Toast
                                    Toast.makeText(ChatActivity.this, "Result: " + result, Toast.LENGTH_SHORT).show();

                                    List<String> aspects = processTextWithModel1(result);

                                    String question = generateQuestion(identifyspiece, aspects.toString());




                                    // Compare with local knowledge base
                                    boolean isKnownSpecies = checkKnowledgeBase(identifyspiece);

                                    // Process the result based on comparison
                                    if (isKnownSpecies) {
                                        Log.i("SPECIES_IDENTIFIED", "The species is known: " + identifyspiece);
                                        // You can update the chat with a positive response or take other actions

                                        // Now process with Model 2
                                        String botResponse = processWithModel2(question); // where 'translatedText' could be context

                                        // Finally translate the response from English back to Chinese
                                        translateText(botResponse, "en", "zh", finalResponse -> {
                                            // Update chat with the final translated response
                                            updateChatWithBotResponse(finalResponse);
                                        });


                                        //显示UI

                                    } else {
                                        Log.i("SPECIES_UNKNOWN", "The species is unknown: " + identifyspiece);
                                        // You can update the chat with a negative response or take other actions

                                        // 未收入知识库，网络搜索
                                        performBaiduBaikeSearch(identifyspiece);

                                    }



                                }
                            }).execute(imagePath);


                        }

                    }
                } else {
                    Log.e("API_ERROR", "Response was successful but no data was returned");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("NETWORK_FAILURE", "Failed to reach the server", t);
                runOnUiThread(() -> Toast.makeText(context, "Network failure, please check your connection: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
    public static String getPathFromUri(Context context, Uri uri) {
        String path = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(column_index);
            }
            cursor.close();
        }
        return path;
    }

    private void processImageAndText(Bitmap image, String text) {
        // Combined image and text processing logic

        processImageOnly(image);

        processTextOnly(text);
    }

    public List<String> processTextWithModel1(String text) {
        List<String> result = new ArrayList<>();

        try {
            // URL of the API endpoint
            URL url = new URL("http://124.223.53.219:5000/predict");

            // Setting up the connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // JSON data to send with the POST request
            String jsonInputString = "{\"question\": \"" + text + "\"}";

            // Sending the request
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Handling the response
            int responseCode = conn.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Request successful.");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                // Close connections
                in.close();
                conn.disconnect();

                // Convert response content to list of strings
                result = Arrays.asList(content.toString().split(" ")); // Simplistic splitting; replace with actual logic.
            } else {
                System.out.println("Request failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void processTextOnly(String text) {
        // First translate the text from Chinese to English
        translateText(text, "zh", "en", translatedText -> {
            // Process text with Model 1
            List<String> aspects = processTextWithModel1(translatedText);

            String question = generateQuestion(identifyspiece, aspects.toString());




            // Compare with local knowledge base
            boolean isKnownSpecies = checkKnowledgeBase(identifyspiece);

            // Process the result based on comparison
            if (isKnownSpecies) {
                Log.i("SPECIES_IDENTIFIED", "The species is known: " + identifyspiece);
                // You can update the chat with a positive response or take other actions

                // Now process with Model 2
                String botResponse = processWithModel2(question); // where 'translatedText' could be context

                // Finally translate the response from English back to Chinese
                translateText(botResponse, "en", "zh", finalResponse -> {
                    // Update chat with the final translated response
                    updateChatWithBotResponse(finalResponse);
                });


                //显示UI

            } else {
                Log.i("SPECIES_UNKNOWN", "The species is unknown: " + identifyspiece);
                // You can update the chat with a negative response or take other actions

                // 未收入知识库，网络搜索
                performBaiduBaikeSearch(identifyspiece);

            }


        });
    }

    private void translateText(String text, String fromLang, String toLang, Consumer<String> callback) {
        translationService.translate(text, fromLang, toLang, new TranslationCallback() {
            @Override
            public void onSuccess(String translatedText) {
                callback.accept(translatedText);
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Translation error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }


    private String processWithModel2(String input) {
        Model2Executor model2Executor = new Model2Executor(getAssets());
        return model2Executor.executeModelOnInput(input);
    }
    private void updateChatWithBotResponse(String botMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                webView.setVisibility(View.GONE);
                Message botMessageObject = new Message(botMessage,null, BOT_KEY);
                messageModalArrayList.add(botMessageObject);
                messageRVAdapter.notifyDataSetChanged();
                chatsRV.scrollToPosition(messageModalArrayList.size() - 1);

                onNewMessage(currentSequenceNumber++,botMessage,BOT_KEY);
            }
        });
    }

    private void updateUserChat1(String input){
        Message botMessageObject = new Message(input,null, USER_KEY);
        messageModalArrayList.add(botMessageObject);
        messageRVAdapter.notifyDataSetChanged();
        chatsRV.scrollToPosition(messageModalArrayList.size() - 1);
        onNewMessage(currentSequenceNumber++,input,USER_KEY);
    }

    private void updateUserChat2(String imageUri){
        Message botMessageObject = new Message(imageUri, USER_KEY);
        messageModalArrayList.add(botMessageObject);
        messageRVAdapter.notifyDataSetChanged();
        chatsRV.scrollToPosition(messageModalArrayList.size() - 1);
        onNewMessage(currentSequenceNumber++,"图片",USER_KEY);
    }


    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private void onNewMessage(int sequence, String message, String sender) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(new Date());
        ChatSession session = new ChatSession(identifyspiece,sequence,sender,message,time);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                chatSessionDao.insert(session);
            }
        });

    }





    public static String generateQuestion(String species, String aspect) { return "What is the " + aspect + " of " + species + "?"; }

    public void showPopupWindow(View anchorView) {
        // 加载布局文件
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // 获取屏幕宽度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

// 创建PopupWindow对象
        PopupWindow popupWindow = new PopupWindow(popupView,
                screenWidth, // 设置宽度为屏幕宽度
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
            requestStoragePermission();
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
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);
    }

    private void handleCameraResult(Intent data) {
        Bitmap img = (Bitmap) data.getExtras().get("data");
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

                String imageUri = String.valueOf(uri);
                Log.d(TAG, imageUri);
                updateUserChat2(imageUri);
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


    private boolean checkKnowledgeBase(String species) {


        return knownSpecies.contains(species);
    }

    private void performBaiduBaikeSearch(String query) {
        // 百度百科搜索 URL
        String urlString = "https://baike.baidu.com/item/" + Uri.encode(query);

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                String response = result.toString();
                // Parse and handle the response
                parseBaiduBaikeResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(context, "Error during Baidu Baike search: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void parseBaiduBaikeResponse(String html) {
        Document doc = Jsoup.parse(html);
        String title = doc.select("meta[property=og:title]").first().attr("content");
        String description = doc.select("meta[property=og:description]").first().attr("content");

        // Now you have the title and description, you can update the UI or do other things
        runOnUiThread(() -> {
            // Assuming you have TextViews with the ids 'titleView' and 'descriptionView'
            updateChatWithBotResponse(title + " " + description);
        });
    }

    private void addBotWelcomeMessage() {
        String welcomeMessage = "欢迎来到Flora Talk！我是您的植物护理助手。请问您今天需要关于哪种植物的帮助？";
        Message welcomeMsg = new Message(welcomeMessage, BOT_KEY);
        messageModalArrayList.add(welcomeMsg);
        messageRVAdapter.notifyItemInserted(messageModalArrayList.size() - 1);
        chatsRV.scrollToPosition(messageModalArrayList.size() - 1);
        onNewMessage(currentSequenceNumber,welcomeMessage,BOT_KEY);
    }








}
