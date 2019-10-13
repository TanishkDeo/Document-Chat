package com.deo.tanis.safechat.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deo.tanis.safechat.LatestMessaging.Chatlog;
import com.deo.tanis.safechat.Models.Chat;
import com.deo.tanis.safechat.Models.User;
import com.deo.tanis.safechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;

    String theLastMessage;


    public UserAdapter(Context mContext, List<User> mUsers, boolean ischat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_row, viewGroup, false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = mUsers.get(i);
        viewHolder.username.setText(user.getName());


        if(user.imageUrl.equals("default")){
            viewHolder.imageView.setImageResource(R.drawable.default_profile);
        }
        else{

            Glide.with(mContext).load(user.imageUrl).dontAnimate().into(viewHolder.imageView);
        }




        if (ischat) {
            lastMessage(user.getUid(), viewHolder.last_msg);
        }
        else {
            viewHolder.last_msg.setVisibility(View.GONE);
        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, Chatlog.class);
                intent.putExtra("username", user.getName());
                intent.putExtra("userid", user.getUid());
                mContext.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView imageView;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            imageView = itemView.findViewById(R.id.imageView);
            last_msg = itemView.findViewById(R.id.last_msg);

        }
    }
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = null;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if ((firebaseUser != null) && (chat != null)) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                                theLastMessage = chat.getMessage();
                            if (theLastMessage != null) {


                                // compare times of all messages of all users
                                if (chat.getSender() == firebaseUser.getUid()) {
                                        last_msg.setTypeface(null, Typeface.NORMAL);
                            }
                                if (chat.getReceiver().equals(firebaseUser.getUid())) {
                                    if (!chat.isSeen()) {
                                        last_msg.setTypeface(null, Typeface.BOLD);
                                    }
                                }

                            }

                        }


                    }
                }

                switch (theLastMessage){

                    default:

                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
