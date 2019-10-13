package com.deo.tanis.safechat.Auth;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.deo.tanis.safechat.LatestMessaging.LatestMessages;
import com.deo.tanis.safechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mButton = findViewById(R.id.login_button_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email_text = findViewById(R.id.email_login);
                final String email = email_text.getText().toString();
                EditText pw_text = findViewById(R.id.password_login);
                final String password = pw_text.getText().toString();
                // Sign In
                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), LatestMessages.class);
                                            startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        }
                                        else {
                                            Toast.makeText(MainActivity.this, "Login Failed: " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                        );
            }
        });
    }


    public void startRegisterActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}


