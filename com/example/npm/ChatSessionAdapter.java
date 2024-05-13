package com.example.npm;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class ChatSessionAdapter extends RecyclerView.Adapter<ChatSessionAdapter.ViewHolder> {
    private Context context;
    private List<ChatSession> chatSessions;

    public ChatSessionAdapter(Context context, List<ChatSession> chatSessions) {
        this.context = context;
        this.chatSessions = chatSessions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_session_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatSession session = chatSessions.get(position);
        holder.ChaSessionName.setText(session.getName());
        holder.timeStamp.setText(session.getFormattedTimestamp()); // Assuming you have a method to format the timestamp
        // Set an icon or an image if available
        // Glide.with(context).load(session.getImageUrl()).into(holder.imageSession); // Uncomment if image URLs are used
    }

    @Override
    public int getItemCount() {
        return chatSessions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageSession;
        TextView ChaSessionName, timeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            imageSession = itemView.findViewById(R.id.imageSession);
            ChaSessionName = itemView.findViewById(R.id.ChaSessionName);
            timeStamp = itemView.findViewById(R.id.timeStamp);
        }
    }

}


