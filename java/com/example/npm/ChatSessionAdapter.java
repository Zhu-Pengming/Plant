package com.example.npm;


import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatSessionAdapter extends RecyclerView.Adapter<ChatSessionAdapter.ViewHolder> {
    private Context context;
    private List<ChatSession> chatSessions;



    public ChatSessionAdapter(Context context, List<ChatSession> chatSessions) {
        this.context = context;
        this.chatSessions = chatSessions;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_session_item, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatSession session = chatSessions.get(position);
        Log.d("ChatSessionAdapter", "Session name: " + session.getChatName());

        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = parseFormat.parse(session.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            String formattedTime = format.format(date);
            Log.d("ChatSessionAdapter", "Formatted timestamp: " + formattedTime);

            holder.ChaSessionName.setText(session.getChatName());
            holder.timeStamp.setText(formattedTime);
        }
    }

    @Override
    public int getItemCount() {
        return chatSessions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageSession;
        TextView ChaSessionName, timeStamp;

        ImageView deleteIcon, pinIcon;

        public ViewHolder(View itemView, ChatSessionAdapter adapter) {
            super(itemView);
            imageSession = itemView.findViewById(R.id.imageSession);
            ChaSessionName = itemView.findViewById(R.id.ChaSessionName);
            timeStamp = itemView.findViewById(R.id.timeStamp);

            deleteIcon = itemView.findViewById(R.id.ic_delete);
            pinIcon = itemView.findViewById(R.id.ic_pin);

            deleteIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("删除确认")
                            .setMessage("你确定要删除这个聊天吗？")
                            .setPositiveButton("是", (dialog, which) -> {
                                adapter.removeItem(position);
                            })
                            .setNegativeButton("否", null)
                            .show();
                }
            });

            pinIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("置顶确认")
                            .setMessage("你确定要置顶这个项目吗？")
                            .setPositiveButton("是", (dialog, which) -> {
                                adapter.pinItem(position);
                            })
                            .setNegativeButton("否", null)
                            .show();
                }
            });
        }
    }

    public void setChatSessions(List<ChatSession> newChatSessions) {
        this.chatSessions = newChatSessions;
        notifyDataSetChanged(); // Notify the adapter that data has changed so the view can be reloaded
    }

    public void filter(String text) {
        List<ChatSession> filteredList = new ArrayList<>();
        for (ChatSession item : chatSessions) {
            if (item.getChatName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        setChatSessions(filteredList);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        chatSessions.remove(position);
        notifyItemRemoved(position);
    }
    public void pinItem(int position) {
        ChatSession session = chatSessions.get(position);
        chatSessions.remove(position);
        chatSessions.add(0, session);
        notifyItemMoved(position, 0);
    }

    public Context getContext() {
        return context;
    }
}

