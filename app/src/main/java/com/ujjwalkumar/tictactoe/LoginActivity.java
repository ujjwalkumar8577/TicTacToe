package com.ujjwalkumar.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    String name, email, password, uid;

    EditText emailBox, passwordBox;
    TextView forgotPassword;
    Button login, createAccount;
    LottieAnimationView animationViewLoading;

    private SharedPreferences sp;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        forgotPassword = findViewById(R.id.forgotPassword);
        login = findViewById(R.id.login);
        createAccount = findViewById(R.id.createAccount);
        animationViewLoading = findViewById(R.id.animationViewLoading);

        animationViewLoading.setVisibility(View.INVISIBLE);
        sp = getSharedPreferences("user", Activity.MODE_PRIVATE);

        forgotPassword.setOnClickListener(view -> {
            String email = emailBox.getText().toString();
            if (!email.equals("")) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(LoginActivity.this, "Reset password email sent", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
            else
                Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
        });

        login.setOnClickListener(view -> {
            email = emailBox.getText().toString();
            password = passwordBox.getText().toString();
            if(!email.equals("") && !password.equals("")) {
                animationViewLoading.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        uid = auth.getUid();
                        dbref.child(uid).child("name").get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                name = task1.getResult().getValue().toString();
                                sp.edit().putString("name", name).apply();
                                sp.edit().putString("email", email).apply();
                                sp.edit().putString("password", password).apply();
                                sp.edit().putString("uid", uid).apply();

                                Intent in = new Intent();
                                in.setAction(Intent.ACTION_VIEW);
                                in.setClass(getApplicationContext(), HomeActivity.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(in);
                                finish();
                            } else {
                                animationViewLoading.setVisibility(View.INVISIBLE);
                                Toast.makeText(LoginActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        animationViewLoading.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(LoginActivity.this, "Enter email & password", Toast.LENGTH_SHORT).show();
            }
        });

        createAccount.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), SignupActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });
    }
}