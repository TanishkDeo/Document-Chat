package com.deo.tanis.safechat.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deo.tanis.safechat.Models.Chat;
import com.deo.tanis.safechat.R;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageUrl;

    FirebaseUser firebaseUser;




    public MessageAdapter(Context mContext, List<Chat> mChat, String imageUrl) {
            this.mContext = mContext;
            this.mChat = mChat;
            this.imageUrl = imageUrl;
            }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(i == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
            }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Chat chat = mChat.get(i);
        viewHolder.show_message.setText(chat.getMessage());


        if (imageUrl.equals("default")){
            viewHolder.imageView.setImageResource(R.drawable.default_profile);
        } else {
            Glide.with(mContext).load(imageUrl).dontAnimate().into(viewHolder.imageView);
        }






            if (mChat.size() > 0) {
                if (i != mChat.size() - 1) {
                    viewHolder.seen_message.setVisibility(View.GONE);
                }
                if (chat.isSeen()) {
                    viewHolder.seen_message.setText("Read");
                }
                else {
                    viewHolder.seen_message.setText("Delivered");
                }

            }






    }

    @Override
    public int getItemCount() {
            return mChat.size();
            }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView imageView;
        public TextView seen_message;


        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            imageView = itemView.findViewById(R.id.imageView);
            seen_message = itemView.findViewById(R.id.read_chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}
