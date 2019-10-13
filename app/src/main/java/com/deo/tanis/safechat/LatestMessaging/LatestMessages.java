package com.deo.tanis.safechat.LatestMessaging;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwnerInitializer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.deo.tanis.safechat.Adapter.UserAdapter;
import com.deo.tanis.safechat.Auth.Welcome;
import com.deo.tanis.safechat.Models.Chatlist;
import com.deo.tanis.safechat.Models.User;
import com.deo.tanis.safechat.Notifications.FirebaseId;
import com.deo.tanis.safechat.Notifications.Token;
import com.deo.tanis.safechat.R;
import com.deo.tanis.safechat.Settings.Settings;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LatestMessages extends AppCompatActivity {

    private ImageButton mButton;

    private RecyclerView recyclerView;

    private RecyclerView.LayoutManager layoutManager;

    private UserAdapter userAdapter;
    private List<User> mUsers;



    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private List<Chatlist> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyUserIsLoggedIn();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_messages);
        mButton = findViewById(R.id.imageButton);
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), Welcome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewMessage.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler_view_latest);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();





        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            userList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Chatlist chatlist = snapshot.getValue(Chatlist.class);
                userList.add(chatlist);

            }

            chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                updateToken(newToken);
            }
        });



    }


    private void updateToken(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void verifyUserIsLoggedIn() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser.getUid().equals(null)){
            startActivity(new Intent(getApplicationContext(), Welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_sign_out:

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), Settings.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    private void chatList() {
        mUsers = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        for (Chatlist chatlist : userList) {
                            if (user.getUid().equals(chatlist.getId())) {
                                mUsers.add(user);
                            }
                        }
                }
            userAdapter = new UserAdapter(getApplicationContext(), mUsers, true);
            recyclerView.setAdapter(userAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }






}

