package com.ujjwalkumar.tictactoe;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final Timer timer = new Timer();
    private TimerTask Splash;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Splash = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent in = new Intent();
                        in.setAction(Intent.ACTION_VIEW);

                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            in.setClass(getApplicationContext(), HomeActivity.class);
                        else
                            in.setClass(getApplicationContext(), LoginActivity.class);

                        in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(in);
                        finish();
                    }
                });
            }
        };
        timer.schedule(Splash, 3000);

    }
}