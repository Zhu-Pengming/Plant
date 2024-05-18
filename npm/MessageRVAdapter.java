package com.example.npm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.InputStream;
import java.util.ArrayList;

public class MessageRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messageModalArrayList;
    private Context context;



    public MessageRVAdapter(ArrayList<Message> messageModalArrayList, Context context) {
        this.messageModalArrayList = messageModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
                return new UserViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_item, parent, false);
                return new BotViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageModalArrayList.get(position);
        switch (message.getSender()) {
            case "user":
                UserViewHolder userHolder = (UserViewHolder) holder;
                userHolder.userTV.setText(message.getMessage());
                if (message.getImageUri() != null) {
                    Bitmap image = loadImageFromUri(Uri.parse(message.getImageUri()));
                    userHolder.userIV.setImageBitmap(image);
                }
                break;
            case "bot":
                BotViewHolder botHolder = (BotViewHolder) holder;
                botHolder.botTV.setText(message.getMessage());

                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageModalArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (messageModalArrayList.get(position).getSender()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    private Bitmap loadImageFromUri(Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userTV;
        ImageView userIV;

        BubbleImageView bubbleImageView;


        @SuppressLint("WrongViewCast")
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userTV = itemView.findViewById(R.id.idTVUser);
            userIV = itemView.findViewById(R.id.idIVUser);
            // userIV = itemView.findViewById(R.id.idIVUser);  // Assuming you have an ImageView in your layout
            bubbleImageView = (BubbleImageView) itemView.findViewById(R.id.idIVUser2);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView botTV;
        ImageView botIV;

        @SuppressLint("WrongViewCast")
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botTV = itemView.findViewById(R.id.idTVBot);

        }
    }
}