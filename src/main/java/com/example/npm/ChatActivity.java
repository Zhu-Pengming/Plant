package com.example.npm;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatsRV;
    private ImageView sendMsgIB;
    private EditText userMsgEdt;
    private RequestQueue mRequestQueue;
    private ArrayList<Message> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;
    private RecognitionService recognitionService; // Corrected the semicolon

    private String USER_KEY = "user";
    private String BOT_KEY = "bot";

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
        //保存image

        sendMsgIB.setOnClickListener(v -> {
            if (userMsgEdt.getText().toString().isEmpty()) {
                Toast.makeText(ChatActivity.this, "Please enter your message.", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(userMsgEdt.getText().toString(), null);
                userMsgEdt.setText("");
            }
        });
    }

    private void sendMessage(String userMsg, Bitmap userImage) {
        // Check if there is text or image to send
        if (userMsg.isEmpty() && userImage == null) {
            Toast.makeText(this, "Please enter a message or select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the message to the RecyclerView
        messageModalArrayList.add(new Message(userMsg, userImage, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();
        chatsRV.scrollToPosition(messageModalArrayList.size() - 1);

        // Process and send the message
        if (userImage != null) {
            // Combine text and image processing
            processImageAndText(userImage, userMsg);
        } else {
            // Only text
            processTextOnly(userMsg);
        }
        userMsgEdt.setText(""); // Clear the text input after sending
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
                        messageModalArrayList.add(new Message(botResponse, BOT_KEY));
                        messageRVAdapter.notifyDataSetChanged();
                        chatsRV.scrollToPosition(messageModalArrayList.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            error.printStackTrace();
            Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
        });

        mRequestQueue.add(jsonObjectRequest);
    }
}

