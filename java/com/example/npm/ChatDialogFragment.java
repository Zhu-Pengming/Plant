package com.example.npm;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

public class ChatDialogFragment extends DialogFragment {
    private List<MessageBot> messages = new ArrayList<>();
    private MessageBotAdapter adapter;
    private EditText inputEditText;

    private CharSequence selectedText;

    private String fullText;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_chat_dialog, null);


        addBotFirstMessage();


        // Setup input text field and button
        inputEditText = view.findViewById(R.id.inputEditText);
        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> sendMessage());

        // Setup RecyclerView for messages
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageBotAdapter(messages);
        recyclerView.setAdapter(adapter);

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        // Apply custom styles and animations
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }


    private void addBotFirstMessage() {
        String question = generateQuestion(fullText, selectedText.toString());
        new GetBotResponseTask().execute(question);
    }

    public String getBotResponse(String message) throws IOException, JSONException {
        String API_URL = "https://api.openai-proxy.org/v1/chat/completions";
        String API_KEY = "sk-O1RiVN9ZHxJBfYfMruhixCrBJE72Ds9lhYKi2R1M2f0WKL8s";

        OkHttpClient client = new OkHttpClient();

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
        JsonArray choices = jsonResponse.getAsJsonArray("choices");
        if (choices != null && choices.size() > 0) {
            String botResponse = choices.get(0).getAsJsonObject()
                    .get("message").getAsJsonObject()
                    .get("content").getAsString();
            return botResponse;
        } else {
            // Handle the case where "choices" is null or empty
            return "No response from bot";
        }
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

    private class GetBotResponseTask extends AsyncTask<String, Void, String> {
        private Exception exception;

        @Override
        protected String doInBackground(String... params) {
            try {
                return getBotResponse(params[0]);
            } catch (IOException | JSONException e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (exception != null) {
                throw new RuntimeException(exception);
            } else {
                messages.add(new MessageBot(result, MessageBot.Sender.BOT));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void sendMessage() {
        String input = inputEditText.getText().toString();
        if (!input.isEmpty()) {
            messages.add(new MessageBot(input, MessageBot.Sender.USER));
            new GetBotResponseTask().execute(input);
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
