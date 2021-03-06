package com.ujjwalkumar.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountActivity extends AppCompatActivity {

    String name, email, uid;

    ImageView imageViewBack;
    TextView textViewName, textViewEmail, textViewTotal, textViewWon, textViewTie;
    RatingBar ratingBar;
    Button buttonLogout;
    LottieAnimationView animationViewLoading;
    CardView cardView;

    private SharedPreferences sp;
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        imageViewBack = findViewById(R.id.imageViewBack);
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewTotal = findViewById(R.id.textViewTotal);
        textViewWon = findViewById(R.id.textViewWon);
        textViewTie = findViewById(R.id.textViewTie);
        ratingBar = findViewById(R.id.ratingBar);
        buttonLogout = findViewById(R.id.buttonLogout);
        animationViewLoading = findViewById(R.id.animationViewLoading);
        cardView = findViewById(R.id.cardView);

        cardView.setVisibility(View.GONE);
        sp = getSharedPreferences("user", Activity.MODE_PRIVATE);
        name = sp.getString("name", "User Name");
        email = sp.getString("email", "User Email");
        uid = sp.getString("uid", "UID");
        textViewName.setText(name);
        textViewEmail.setText(email);

        dbref.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);
                textViewName.setText(user.getName());
                ratingBar.setRating(calculateRating(user));

                textViewTotal.setText("Games played : " + user.getTotal());
                textViewWon.setText("Games won : " + user.getWon());
                textViewTie.setText("Games tied : " + user.getTie());
            } else {
                Toast.makeText(AccountActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
            animationViewLoading.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);
        });

        imageViewBack.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), HomeActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        buttonLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            finishAffinity();
        });
    }

    public float calculateRating(User user) {
        if (user.getTotal() == 0)
            return 0;

        return 5.0f * (user.getWon() + 0.5f * user.getTie()) / user.getTotal();
    }
}