package com.deo.tanis.safechat.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deo.tanis.safechat.Models.User;
import com.deo.tanis.safechat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class Settings extends  AppCompatActivity{

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    public static final int IMAGE_REQUEST = 1;
    CircleImageView profile_image;
    Button select_profile;
    Uri imageUri;
    TextView textView;
    String imageurl;


    TextView accountButton;
    TextView notifButton;
    TextView friendButton;
    TextView invButton;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        select_profile = findViewById(R.id.select_profile_settings);
        select_profile.setAlpha(0f);


        // define button views

        accountButton = findViewById(R.id.account_settings_text);
        notifButton = findViewById(R.id.notif_settings_text);
        friendButton = findViewById(R.id.friend_settings_text);
        invButton = findViewById(R.id.friend_settings_text);

        // Profile Picture

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("images");
        profile_image = findViewById(R.id.pfp);
        profile_image.setTooltipText("EDIT");
        textView = findViewById(R.id.textview_settings);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                textView.setText(user.getName());
                imageurl = user.getImageUrl();
                Log.d("Settings", imageurl);
                imageurl = user.getImageUrl();
                if (imageurl.equals("default")) {
                    profile_image.setImageResource(R.drawable.default_profile);

                } else {
                    Glide.with(getApplicationContext()).load(imageurl).dontAnimate().into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        select_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        // Done with Profile Picture

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getApplicationContext(), Account.class);
                startActivity(intent);
            }
        });



        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out Safechat for your smartphone. Download it today at *link*");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

    }
    // Profile Functions

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                select_profile.setAlpha(0f);
                profile_image.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage(imageUri);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Settings", "not wroking");
            }


        }
    }

    private void uploadImageToFirebaseStorage(final Uri uri) {
        if(uri == null) return;
        String filename = firebaseUser.getUid();
        storageReference = FirebaseStorage.getInstance().getReference("images").child(filename);
        if (imageurl.equals("default")){
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("imageUrl", uri.toString());
                                databaseReference.updateChildren(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            Toast.makeText(Settings.this,"Uploaded Profile Picture", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                });


    }
        if (!imageurl.equals("default")) {
            Log.d("Settings", "uwu : starting not equalling default");
            storageReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Settings", "Deleted Profile Picture");
                            storageReference.putFile(uri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Log.d("Settings", "Put file in Storage");
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) { HashMap<String, Object> map = new HashMap<>();
                                                    map.put("imageUrl", uri.toString());
                                                    databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("Settings", "Put Image Url in Database");
                                                            Toast.makeText(Settings.this,"Uploaded Profile Picture", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });


                                                }
                                            });
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Settings", "Failed to delete profile pic");
                        }
                    });

        }
    }




    // Done With Profile Switching Functions




}






