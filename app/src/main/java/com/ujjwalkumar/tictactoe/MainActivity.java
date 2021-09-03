package com.ujjwalkumar.tictactoe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

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

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        String id = deepLink.getQueryParameter("id");
                        String password = deepLink.getQueryParameter("pass");

                        Intent in = new Intent();
                        in.setAction(Intent.ACTION_VIEW);
                        in.putExtra("id", id);
                        in.putExtra("password", password);
                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            in.setClass(getApplicationContext(), RoomActivity.class);
                        else
                            in.setClass(getApplicationContext(), LoginActivity.class);
                        startActivity(in);
                        finish();
                    }
                    else {
                        Splash = new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(() -> {
                                    Intent in = new Intent();
                                    in.setAction(Intent.ACTION_VIEW);
                                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                                        in.setClass(getApplicationContext(), HomeActivity.class);
                                    else
                                        in.setClass(getApplicationContext(), LoginActivity.class);
                                    startActivity(in);
                                    finish();
                                });
                            }
                        };
                        timer.schedule(Splash, 3000);
                    }
                })
                .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}