package com.tom.npm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageBotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MessageBot> messages;

    public MessageBotAdapter(List<MessageBot> messages) {
        this.messages = messages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagebot_item1, parent, false);
            return new UserHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagebot_item2, parent, false);
            return new BotHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSender() == MessageBot.Sender.USER ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageBot message = messages.get(position);
        if (holder instanceof UserHolder) {
            ((UserHolder) holder).messageTextView.setText(message.getContent());
        } else if (holder instanceof BotHolder) {
            ((BotHolder) holder).messageTextView.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;

        public UserHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageUserText);
        }
    }

    public static class BotHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;

        public BotHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageBotText);
        }
    }
}