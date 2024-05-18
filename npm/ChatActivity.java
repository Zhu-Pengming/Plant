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
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.tensorflow.lite.Interpreter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private TranslationService translationService;



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
        // First translate the text from Chinese to English
        translateText(text, "zh", "en", translatedText -> {
            // Process text with Model 1
            List<String> aspects = processTextWithModel1(translatedText);

            // Assuming 'identifyspiece' is a string that you've prepared or retrieved
            String combinedInput = String.join(" ", aspects) + " " + identifyspiece;

            // Now process with Model 2
            String botResponse = processWithModel2(combinedInput, translatedText); // where 'translatedText' could be context

            // Finally translate the response from English back to Chinese
            translateText(botResponse, "en", "zh", finalResponse -> {
                // Update chat with the final translated response
                updateChatWithBotResponse(finalResponse);
            });
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





}
