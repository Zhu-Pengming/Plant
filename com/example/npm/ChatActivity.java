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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatsRV;
    private ImageView sendMsgIB,sendImageIB;
    private EditText userMsgEdt;
    private RequestQueue mRequestQueue;
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


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);

        // Initialize RequestQueue
        mRequestQueue = Volley.newRequestQueue(this);
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
        // Image processing logic
    }

    private void processImageAndText(Bitmap image, String text) {
        // Combined image and text processing logic
    }

    private void processTextOnly(String text) {
        // Text processing logic
        JSONObject messagePayload = new JSONObject();
        try {
            messagePayload.put("message", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiUrl = "https://your-api-url.com/nlp";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, messagePayload,
                response -> {
                    try {
                        String botResponse = response.getString("reply");
                        updateChatWithBotResponse(botResponse); ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            error.printStackTrace();
            Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
        });

        mRequestQueue.add(jsonObjectRequest);
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
