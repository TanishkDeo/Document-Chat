package com.deo.tanis.safechat.Auth;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.deo.tanis.safechat.Models.User;
import com.deo.tanis.safechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity {

    private Button mButton;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mButton = findViewById(R.id.register_button_register);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email_text = findViewById(R.id.email_register);
                final String email = email_text.getText().toString();
                EditText pw_text = findViewById(R.id.password_register);
                final String password = pw_text.getText().toString();
                EditText name_text = findViewById(R.id.name_register);
                final String name = name_text.getText().toString();


// Firebase Authentication
    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(Register.this, "Created Account Successfully.",
                                Toast.LENGTH_SHORT).show();
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String id = currentUser.getUid();
                        saveUserToFirebaseDatabase(name, email, id);


                    } else {
                        Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();

                    }
                }
            });

            }
        });



    }

    private void saveUserToFirebaseDatabase(String name, String email, String uid) {
        User user = new User(name, email, uid, "default");
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(uid).setValue(user)
                .addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                           startActivity(new Intent(getApplicationContext(), Profilepic.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        else {
                            Log.d("Register", "Failed to save user to database");
                        }
                    }
                });


    }

    public void returnActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
