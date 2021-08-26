package com.ujjwalkumar.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    private ImageView imageViewAccount, imageViewSetting;
    private MaterialCardView cardView1, cardView2, cardView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imageViewAccount = findViewById(R.id.imageViewAccount);
        imageViewSetting = findViewById(R.id.imageViewSetting);
        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);

        imageViewAccount.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), AccountActivity.class);
            startActivity(in);
        });

        imageViewSetting.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), SettingActivity.class);
            startActivity(in);
        });

        cardView1.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), SinglePlayerActivity.class);
            startActivity(in);
        });

        cardView2.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), TwoPlayerActivity.class);
            startActivity(in);
        });

        cardView3.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), RoomActivity.class);
            startActivity(in);
        });
    }
}