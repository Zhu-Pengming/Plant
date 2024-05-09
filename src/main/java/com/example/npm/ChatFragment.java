package com.example.npm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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

public class ChatFragment extends Fragment {
    private RecyclerView chatsRV;
    private ImageView sendMsgIB;
    private EditText userMsgEdt;
    private RequestQueue mRequestQueue;
    private ArrayList<Message> messageModalArrayList;
    private MessageRVAdapter messageRVAdapter;

    private String USER_KEY = "user";
    private String BOT_KEY = "bot";

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatsRV = view.findViewById(R.id.idRVChats);
        sendMsgIB = view.findViewById(R.id.idIBSend);
        userMsgEdt = view.findViewById(R.id.idEdtMessage);

        // Initialize RequestQueue
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.getCache().clear();

        messageModalArrayList = new ArrayList<>();
        messageRVAdapter = new MessageRVAdapter(messageModalArrayList, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);

        chatsRV.setLayoutManager(linearLayoutManager);
        chatsRV.setAdapter(messageRVAdapter);

        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userMsgEdt.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter your message.", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(userMsgEdt.getText().toString());
                    userMsgEdt.setText("");
                }
            }
        });

        return view;
    }

    private void sendMessage(String userMsg) {
        messageModalArrayList.add(new Message(userMsg, USER_KEY));
        messageRVAdapter.notifyDataSetChanged();

        // Construct the JSON payload
        JSONObject messagePayload = new JSONObject();
        try {
            messagePayload.put("message", userMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a JsonObjectRequest
        String apiUrl = "https://your-api-url.com/nlp";  // Replace with your actual API URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, apiUrl, messagePayload,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String botResponse = response.getString("reply"); // Adjust based on actual JSON response
                                    messageModalArrayList.add(new Message(botResponse, BOT_KEY));
                                    messageRVAdapter.notifyDataSetChanged();
                                    chatsRV.scrollToPosition(messageModalArrayList.size() - 1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getActivity(), "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }

}
