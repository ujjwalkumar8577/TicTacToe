package com.ujjwalkumar.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    String name, email, password, uid;

    EditText nameBox, emailBox, passwordBox;
    Button signup, loginAccount;
    LottieAnimationView animationViewLoading;

    private SharedPreferences sp;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameBox = findViewById(R.id.nameBox);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        signup = findViewById(R.id.signup);
        loginAccount = findViewById(R.id.loginAccount);
        animationViewLoading = findViewById(R.id.animationViewLoading);

        animationViewLoading.setVisibility(View.INVISIBLE);
        sp = getSharedPreferences("user", Activity.MODE_PRIVATE);

        signup.setOnClickListener(view -> {
            name = nameBox.getText().toString();
            email = emailBox.getText().toString();
            password = passwordBox.getText().toString();
            if(!name.equals("") && !email.equals("") && !password.equals("")) {
                animationViewLoading.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        uid = auth.getUid();
                        User user = new User(uid, password, email, name);
                        dbref.child(uid).setValue(user);
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
                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
                Toast.makeText(SignupActivity.this, "Enter name, email & password", Toast.LENGTH_SHORT).show();
        });

        loginAccount.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });
    }
}