package com.example.npm;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
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
import android.view.inputmethod.InputMethodManager;
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

import java.io.FileNotFoundException;
import java.io.InputStreamReader;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;


public class ChatActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE =1 ;
    private String information,measure;
    private static final int STORAGE_PERMISSION_CODE = 1;
    private RecyclerView chatsRV;
    private ImageView sendMsgIB,sendImageIB;
    private EditText userMsgEdt;
    private ArrayList<Message> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;


    private static final String API_URL = "https://api.openai-proxy.org/v1/chat/completions";
    private static final String API_KEY = "sk-O1RiVN9ZHxJBfYfMruhixCrBJE72Ds9lhYKi2R1M2f0WKL8s";

    private String TAG = "Camera-Test";

    private String USER_KEY = "user";
    private String BOT_KEY = "bot";
    private final int CAMERA_REQ_CODE = 100;
    static Uri uri;
    ImageView imgCamera;
    private static final String FOLDER_NAME = "MyAppImages";

    static final int OPEN_GALLERY_REQUEST_CODE = 102;

    private ChatViewModel viewModel;

    private String identifyspiece;

    private TranslationService translationService = new TranslationService();

    private int currentSequenceNumber = 0;

    private ChatSessionDao chatSessionDao;
    private ChatSessionAdapter chatSessionAdapter;

    private ImageView returnTo,setting;

    private WebView webView;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().hide();
        }

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

        userMsgEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });



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

                String inputText = userMsgEdt.getText().toString();
                String species = findSpeciesInText();

                if (species != null) { // 检查是否找到植物

                    identifyspiece = species;
                    processTextWithModel1(inputText, species,new OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(String result) {
                            Log.d("Model1",species);

                            processTextOnly(inputText,species);
                        }

                        @Override
                        public void onFailure(Exception e) {
                        }
                    });
                } else {
                    GPTAI(inputText);
                    Log.d("GPTAI",inputText);
                }
                updateUserChat1(userMsgEdt.getText().toString());
                userMsgEdt.setText("");

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

    private String findSpeciesInText() {
        String inputText = userMsgEdt.getText().toString();
        PlantQuerySystem querySystem = new PlantQuerySystem();
        return querySystem.processQuery(inputText); // 直接返回查询结果
    }

    private final String APIKey1 = "2b10MglNpUK4UBkH5myWDPWe";
    private PlantIdentificationUtil plantIdentificationUtil;



    private void processImageOnly(Bitmap image) {
        webView.setVisibility(View.VISIBLE);
        if (image != null) {

            // Initialize the Retrofit service
            PlantIdentificationService service = RetrofitClientInstance.getRetrofitInstance(APIKey1).create(PlantIdentificationService.class);
            plantIdentificationUtil = new PlantIdentificationUtil(service, APIKey1);



            plantIdentificationUtil.identifyPlant(image, new PlantIdentificationUtil.PlantIdentificationCallback() {
                @Override
                public void onSuccess(PlantIdentificationResponse response) {
                    runOnUiThread(() -> {
                        // Update your UI here with response data
                        identifyspiece = response.getBestMatch();
                        Toast.makeText(ChatActivity.this, "Plant identified: " + identifyspiece, Toast.LENGTH_LONG).show();

                        Log.d("plantIdentificationUtil","Plant identified: " + identifyspiece);
                        try {
                            new NetworkTask(identifyspiece, image).execute();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(ChatActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }

    }

    private class NetworkTask extends AsyncTask<Void, Void, Void> {
        private String speciesName;
        private Bitmap image;

        public NetworkTask(String speciesName, Bitmap image) {
            this.speciesName = speciesName;
            this.image = image;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                handleSuccessfulIdentification(speciesName, image);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private void handleSuccessfulIdentification(String speciesName, Bitmap image) throws IOException, JSONException {
        String imagePath = getPathFromUri(ChatActivity.this, uri);
        Log.d("handleSuccessfulIdentification", "hhhh");
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        try {
            // 读取图片字节数据
            byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
            String imageBase64 = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

            // 输入数据
            JSONObject prompt = new JSONObject();
            prompt.put("role", "user");
            JSONObject content = new JSONObject();
            content.put("type", "text");
            content.put("text", "This photo has the plant" + identifyspiece + "Please ask a question, 1-2 sentences, based on its appearance, color, shape, size and the environment in which it was grown or other relevant information.");
            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");
            JSONObject imageUrl = new JSONObject();
            imageUrl.put("url", "data:image/jpeg;base64," + imageBase64);
            imageContent.put("image_url", imageUrl);
            JSONArray contentArray = new JSONArray();
            contentArray.put(content);
            contentArray.put(imageContent);
            prompt.put("content", contentArray);

            JSONArray messages = new JSONArray();
            messages.put(prompt);

// 创建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4o"); // 添加模型
            requestBody.put("messages", messages);

// 构建请求
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody.toString());


            // 构建请求
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .build();


            try (Response responseText = client.newCall(request).execute()) {
                if (!responseText.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + responseText);
                }

                String responseBody = responseText.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                String botResponse = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                System.out.println("Bot response: " + botResponse);


                // Process the translated text with your model
                processTextWithModel1(botResponse, identifyspiece,new OnTaskCompleted() {
                            @Override
                            public void onTaskCompleted(String result) {

                                parseServerResponse(result);


                                String  hh = null;


                                if (information.equals(measure)) {
                                    hh=measure;
                                    System.out.println("Information: " + information);
                                } else {


                                    hh=measure+measure;
                                }


                                // Translate the question back to Chinese
                                translateText(hh, "en", "zh", finalResponse -> {



                                    try {
                                        String transMessage2 = parseTranslationResult(finalResponse);

                                        // Decode Unicode characters
                                        String decodedText = decodeUnicode(transMessage2);

                                        String result1  = decodedText.replace("[“", "").replace("”]", "");
                                        Log.d("Final",decodedText);
// Update chat with the final translated response
                                        updateChatWithBotResponse(result1);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                });
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });


            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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




    @SuppressLint("StaticFieldLeak")
    public void processTextWithModel1(String text, String species, final OnTaskCompleted listener) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            new AsyncTask<String, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(String... params) {
                    List<String> result = new ArrayList<>();
                    try {
                        String response = sendPostRequest(params[0], params[1]);
                        if (response == null || response.isEmpty()) {
                            System.out.println("Response is null or empty");
                            return new ArrayList<>(); // Return an empty list to safely handle in onPostExecute
                        }
                        result = Arrays.asList(response.split(" "));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        System.out.println("Exception: " + e.getMessage());
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(List<String> result) {
                    if (result.isEmpty()) {
                        System.out.println("Result is empty");
                        try {
                            listener.onTaskCompleted("Empty result");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                    try {
                        if (result.size() > 0) {
                            String jsonResponse = result.get(0); // Assume the first result is a JSON string

                        }
                        listener.onTaskCompleted(result.toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.execute(text, species);
        }
    }

    public void parseServerResponse(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonResponse = jsonArray.getJSONObject(0); // get the first object from the array
            information = jsonResponse.optString("informations", "No information available");
            measure = jsonResponse.optString("measures", "No measures available");
            System.out.println("Information: " + information);
            System.out.println("Measure: " + measure);
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error parsing JSON response: " + e.getMessage());
        }
    }

    private String sendPostRequest(String question, String species) throws IOException, JSONException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://124.223.53.219:5000/predict");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(15000); // 15 seconds
            connection.setReadTimeout(15000); // 15 seconds

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("question", question+" ?");
            jsonObject.put("species", species);
            String jsonInputString = jsonObject.toString();

            byte[] input = jsonInputString.getBytes("utf-8");
            try (OutputStream os = connection.getOutputStream()) {
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            StringBuilder content = new StringBuilder();
            try (InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "utf-8");
                 BufferedReader br = new BufferedReader(isr)) {
                char[] buffer = new char[1024];
                int length;
                while ((length = br.read(buffer)) != -1) {
                    content.append(buffer, 0, length);
                }
            }
            return content.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void processTextOnly(String text, String species) {
        Log.d("processTextOnly",text);
        // First translate the text from Chinese to English
        translateText(text, "zh", "en", translatedText -> {
            try {
                // Parse the JSON response to get the actual translated string
                String transMessage = parseTranslationResult(translatedText);
                System.out.println("Translation response: " + transMessage);


                // Process the translated text with your model
                processTextWithModel1(transMessage, species,new OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(String result) {

                        parseServerResponse(result);


                        String  hh = null;


                        if (information.equals(measure)) {
                            hh=measure;
                            System.out.println("Information: " + information);
                        } else {


                            hh=measure+measure;
                        }


                        // Translate the question back to Chinese
                        translateText(hh, "en", "zh", finalResponse -> {



                            try {
                                String transMessage2 = parseTranslationResult(finalResponse);

                                // Decode Unicode characters
                                String decodedText = decodeUnicode(transMessage2);

                                String result1  = decodedText.replace("[“", "").replace("”]", "");
                                Log.d("Final",decodedText);
// Update chat with the final translated response
                                updateChatWithBotResponse(result1);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        });
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            } catch (JSONException e) {
                Log.d("TranslationError", "Error in parsing translation result: " + e.getMessage());
                // Handle JSON parsing errors
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Error in parsing translation result: " + e.getMessage(), Toast.LENGTH_LONG).show());

            }
        });
    }


    public String parseTranslationResult(String json) throws JSONException {
        System.out.println("JSON to parse: " + json); // Add this line
        JSONObject jsonObject = new JSONObject(json);
        if (!jsonObject.has("trans_result")) {
            throw new JSONException("No translation result in the response");
        }
        JSONArray transResult = jsonObject.getJSONArray("trans_result");
        System.out.println("trans_result array: " + transResult.toString()); // Add this line
        JSONObject firstResult = transResult.getJSONObject(0);
        String translatedText = firstResult.getString("dst");
        return translatedText;
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
        Message messageObject = new Message(null, imageUri, USER_KEY);
        messageModalArrayList.add(messageObject);
        messageRVAdapter.notifyDataSetChanged();
        chatsRV.scrollToPosition(messageModalArrayList.size() - 1);
        onNewMessage(currentSequenceNumber++,imageUri,USER_KEY);
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
                // Check if the camera permission is granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // If not, request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    // If permission is already granted, proceed as normal
                    requestStoragePermission();
                    Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(iCamera, CAMERA_REQ_CODE);
                }

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
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, start camera intent
                requestStoragePermission();
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(iCamera, CAMERA_REQ_CODE);
            } else {
                // Permission was denied. Disable the functionality that depends on this permission.
                Toast.makeText(this, "Camera permission is required to use camera.", Toast.LENGTH_SHORT).show();
            }
        }
        // Handle other permissions results if exists
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
        String fileName = timeStamp + ".png";
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

                // Process the image
                processImageOnly(img);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handleGalleryResult(Intent data) {
        uri = data.getData();
        if (uri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                updateUserChat2(String.valueOf(uri));
                processImageOnly(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
