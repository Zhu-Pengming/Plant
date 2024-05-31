package com.example.npm;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageBotAdapter extends RecyclerView.Adapter<MessageBotAdapter.ViewHolder> {
    private List<MessageBot> messages;

    public MessageBotAdapter(List<MessageBot> messages) {
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagebot_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageBot message = messages.get(position);
        holder.messageTextView.setText(message.getContent());

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.messageTextView.getLayoutParams();

        if (message.getSender() == MessageBot.Sender.USER) {
            // Align to the right
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
        } else {
            // Align to the left
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
        }

        holder.messageTextView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
