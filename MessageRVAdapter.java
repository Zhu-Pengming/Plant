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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.io.InputStream;
import java.util.ArrayList;

public class MessageRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messageModalArrayList;

    private static final int TYPE_MESSAGE_ITEM1 = 0; // 只有图片
    private static final int TYPE_MESSAGE_ITEM2 = 1; // 只有文字

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
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_item, parent, false);
                return new BotViewHolder(view);
            case TYPE_MESSAGE_ITEM1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item1, parent, false);
                return new UserViewHolder1(view);
            case TYPE_MESSAGE_ITEM2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item2, parent, false);
                return new UserViewHolder2(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageModalArrayList.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_MESSAGE_ITEM1:
                UserViewHolder1 userHolder1 = (UserViewHolder1) holder;
                if (message.getImageUri() != null) {
                    Bitmap image = loadImageFromUri(Uri.parse(message.getImageUri()));
                    userHolder1.bubbleImageView.setImageBitmap(image);
                }
                break;
            case TYPE_MESSAGE_ITEM2:
                UserViewHolder2 userHolder2 = (UserViewHolder2) holder;
                userHolder2.userTV.setText(message.getMessage());
                break;

            case 3:
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
        Message message = messageModalArrayList.get(position);
        switch (messageModalArrayList.get(position).getSender()) {
            case "user":
                if (message.getImageUri() != null) {
                    return TYPE_MESSAGE_ITEM1;
                } else {
                    return TYPE_MESSAGE_ITEM2;
                }
            case "bot":
                return 3;
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



    public static class UserViewHolder1 extends RecyclerView.ViewHolder {

        ImageView userIV;

        BubbleImageView bubbleImageView;


        @SuppressLint("WrongViewCast")
        public UserViewHolder1(@NonNull View itemView) {
            super(itemView);

            userIV = itemView.findViewById(R.id.idIVUser1_1);
            // userIV = itemView.findViewById(R.id.idIVUser);  // Assuming you have an ImageView in your layout
            bubbleImageView = (BubbleImageView) itemView.findViewById(R.id.idIVUser1_2);


        }
    }
    public static class UserViewHolder2 extends RecyclerView.ViewHolder {
        TextView userTV;
        ImageView userIV;



        @SuppressLint("WrongViewCast")
        public UserViewHolder2(@NonNull View itemView) {
            super(itemView);
            userTV = itemView.findViewById(R.id.idTVUser2_2);
            userIV = itemView.findViewById(R.id.idIVUser2_1);

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