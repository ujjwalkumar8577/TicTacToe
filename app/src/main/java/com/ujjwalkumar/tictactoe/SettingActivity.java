package com.ujjwalkumar.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

public class SettingActivity extends AppCompatActivity {

    ImageView imageViewBack;
    SwitchCompat switch1, switch2;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        imageViewBack = findViewById(R.id.imageViewBack);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);

        sp = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        boolean b1 = sp.getBoolean("sound", true);
        boolean b2 = sp.getBoolean("vibration", true);
        switch1.setChecked(b1);
        switch2.setChecked(b2);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_VIEW);
                in.setClass(getApplicationContext(), HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(in);
                finish();
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    sp.edit().putBoolean("sound", isChecked).apply();
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    sp.edit().putBoolean("vibration", isChecked).apply();
            }
        });
    }
}