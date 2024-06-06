package com.example.npm;

import static com.example.npm.ChatActivity.decodeUnicode;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatDialogFragment extends DialogFragment {
    private MessageBotAdapter adapter;
    private EditText inputEditText;
    private CharSequence selectedText;
    private String fullText;
    private List<MessageBot> messages = new ArrayList<>();
    private static final String API_URL = "https://api.openai-proxy.org/v1/chat/completions"; // Should be retrieved from a secure source
    private static final String API_KEY = "sk-O1RiVN9ZHxJBfYfMruhixCrBJE72Ds9lhYKi2R1M2f0WKL8s"; // Retrieve securely

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_chat_dialog, null);

        setupViews(view);
        Dialog dialog = builder.setView(view).create();
        dialog.setCanceledOnTouchOutside(true);
        styleDialog(dialog);

        return dialog;
    }

    private void setupViews(View view) {
        inputEditText = view.findViewById(R.id.inputEditText);
        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> sendMessage());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageBotAdapter(messages);
        recyclerView.setAdapter(adapter);

        ImageView closeImageView = view.findViewById(R.id.blog_chat_delete);
        closeImageView.setOnClickListener(v -> dismiss());

        addBotFirstMessage();
    }

    private void styleDialog(Dialog dialog) {
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void addBotFirstMessage() {
        String question = generateQuestion(fullText, selectedText.toString());
        sendMessageToBot(question);
    }

    private void sendMessageToBot(String input) {
        OkHttpClient client = new OkHttpClient();
        new Thread(() -> {
            try {
                JSONObject messageJson = createMessageJson(input);
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), messageJson.toString());
                Request request = new Request.Builder().url(API_URL).post(body).addHeader("Authorization", "Bearer " + API_KEY).build();
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    updateUIWithResponse(responseBody);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace(); // Handle this properly in your code
            }
        }).start();
    }

    private JSONObject createMessageJson(String input) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", input);
        JSONArray messages = new JSONArray();
        messages.put(message);
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("messages", messages);
        bodyJson.put("model", "gpt-3.5-turbo");
        return bodyJson;
    }

    private void updateUIWithResponse(String responseBody) {
        getActivity().runOnUiThread(() -> {
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                if (!jsonResponse.has("choices")) {
                    // Log error or show user an error message
                    Log.e("ChatDialogFragment", "No 'choices' in response: " + responseBody);
                    return; // Exit if no "choices" key found
                }
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() == 0) {
                    // Handle the case where "choices" array is empty
                    Log.e("ChatDialogFragment", "Choices array is empty.");
                    return;
                }
                JSONObject firstChoice = choices.getJSONObject(0);
                if (!firstChoice.has("message")) {
                    // Handle the case where the "message" key is missing
                    Log.e("ChatDialogFragment", "No 'message' key in choices object.");
                    return;
                }
                String botResponse = firstChoice.getJSONObject("message").getString("content");
                messages.add(new MessageBot(botResponse, MessageBot.Sender.BOT));
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace(); // Proper error handling goes here
                Log.e("ChatDialogFragment", "Error parsing JSON response", e);
            }
        });
    }

    private String generateQuestion(String fullText, String selectedText) {
        return "在文中（" + fullText + "），'" + selectedText + "'是什么意思？";
    }

    public void setSelectedText(CharSequence text) {
        selectedText = text;
    }

    public void setFullText(String text) {
        this.fullText = text;
    }

    private void sendMessage() {
        String input = inputEditText.getText().toString();
        if (!input.isEmpty()) {
            messages.add(new MessageBot(input, MessageBot.Sender.USER));
            adapter.notifyDataSetChanged();
            sendMessageToBot(input);
            inputEditText.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = getActivity().getWindow().getDecorView().getHeight() / 2;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
