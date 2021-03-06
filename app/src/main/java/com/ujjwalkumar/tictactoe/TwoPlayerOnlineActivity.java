package com.ujjwalkumar.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TwoPlayerOnlineActivity extends AppCompatActivity {

    String id, uid1, uid2;
    int player;
    boolean b1, b2;
    Game game;
    User user1, user2;

    RatingBar ratingBar1, ratingBar2;
    TextView textViewName1, textViewName2, textViewStatus;
    LottieAnimationView[] view = new LottieAnimationView[9];

    AlertDialog alertDialog;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    SharedPreferences sp;
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("games");
    private final DatabaseReference dbref2 = fbdb.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_online);

        ratingBar1 = findViewById(R.id.ratingBar1);
        ratingBar2 = findViewById(R.id.ratingBar2);
        textViewName1 = findViewById(R.id.textViewName1);
        textViewName2 = findViewById(R.id.textViewName2);
        textViewStatus = findViewById(R.id.textViewStatus);
        view[0] = findViewById(R.id.view0);
        view[1] = findViewById(R.id.view1);
        view[2] = findViewById(R.id.view2);
        view[3] = findViewById(R.id.view3);
        view[4] = findViewById(R.id.view4);
        view[5] = findViewById(R.id.view5);
        view[6] = findViewById(R.id.view6);
        view[7] = findViewById(R.id.view7);
        view[8] = findViewById(R.id.view8);

        textViewStatus.setText("Loading ...");
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.metaldrop);

        sp = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        b1 = sp.getBoolean("sound", true);
        b2 = sp.getBoolean("vibration", true);

        Intent in = getIntent();
        id = in.getStringExtra("id");
        uid1 = in.getStringExtra("uid1");
        uid2 = in.getStringExtra("uid2");
        if (uid1.equals(FirebaseAuth.getInstance().getUid()))
            player = 1;
        else
            player = 2;

        dbref2.child(uid1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user1 = task.getResult().getValue(User.class);
                textViewName1.setText(user1.getName());
                ratingBar1.setRating(calculateRating(user1));
            } else
                Toast.makeText(TwoPlayerOnlineActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        });

        dbref2.child(uid2).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user2 = task.getResult().getValue(User.class);
                textViewName2.setText(user2.getName());
                ratingBar2.setRating(calculateRating(user2));
            } else
                Toast.makeText(TwoPlayerOnlineActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        });

        dbref.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game = snapshot.getValue(Game.class);
                if(b1)
                    mediaPlayer.start();
                if (game != null) {
                    if(alertDialog!=null && alertDialog.isShowing())
                        alertDialog.dismiss();

                    textViewStatus.setText("Player " + game.getTurn() + " turn");
                    for (int i = 0; i < 9; i++) {
                        if (game.getArr(i) == 1)
                            view[i].setAnimation(R.raw.circleanimation);
                        else if (game.getArr(i) == 2)
                            view[i].setAnimation(R.raw.crossanimation);
                        else
                            view[i].setAnimation(R.raw.blankanimation);
                        view[i].playAnimation();
                    }

                    if (game.getStatus() == Game.STATUS_P1_WON) {
                        textViewStatus.setText("Player 1 won");
                        showGameOver("Player 1 won");
                    } else if (game.getStatus() == Game.STATUS_P2_WON) {
                        textViewStatus.setText("Player 2 won");
                        showGameOver("Player 2 won");
                    } else if (game.getStatus() == Game.STATUS_TIE) {
                        textViewStatus.setText("Game Tied");
                        showGameOver("Game Tied");
                    }
                } else
                    textViewStatus.setText("Invalid game ID");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textViewStatus.setText(error.getMessage());
            }
        });

        for (int i = 0; i < 9; i++) {
            final int pos = i;
            view[i].setOnClickListener(view -> {
                if(b2)
                    vibrate();

                if (game.getStatus() == Game.STATUS_RUNNING && game.getTurn() == player && game.getArr(pos) == 0) {
                    game.setArr(pos, player);
                    game.toggleTurn();
                    int winner = game.getWinner();
                    if (winner == 1) {
                        game.setStatus(Game.STATUS_P1_WON);
                        user1.setTotal(user1.getTotal() + 1);
                        user2.setTotal(user2.getTotal() + 1);
                        user1.setWon(user1.won + 1);
                        dbref2.child(uid1).setValue(user1);
                        dbref2.child(uid2).setValue(user2);
                    } else if (winner == 2) {
                        game.setStatus(Game.STATUS_P2_WON);
                        user1.setTotal(user1.getTotal() + 1);
                        user2.setTotal(user2.getTotal() + 1);
                        user2.setWon(user2.won + 1);
                        dbref2.child(uid1).setValue(user1);
                        dbref2.child(uid2).setValue(user2);
                    } else if (winner == 3) {
                        game.setStatus(Game.STATUS_TIE);
                        user1.setTotal(user1.getTotal() + 1);
                        user2.setTotal(user2.getTotal() + 1);
                        user1.setTie(user1.getTie() + 1);
                        user2.setTie(user2.getTie() + 1);
                        dbref2.child(uid1).setValue(user1);
                        dbref2.child(uid2).setValue(user2);
                    }
                    dbref.child(id).setValue(game);
                } else {
                    Toast.makeText(TwoPlayerOnlineActivity.this, "Wait for your turn", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exit = new AlertDialog.Builder(this);
        exit.setTitle("Exit");
        exit.setMessage("Do you want to exit?");
        exit.setPositiveButton("Yes", (dialog, which) -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), HomeActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });
        exit.setNegativeButton("No", (dialog, which) -> {

        });
        exit.create().show();
    }

    public void showGameOver(String message) {
        // get xml view
        LayoutInflater li = LayoutInflater.from(TwoPlayerOnlineActivity.this);
        View promptsView = li.inflate(R.layout.dialog_game_over, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TwoPlayerOnlineActivity.this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);

        // create alert dialog and show it
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        // get user input
        final ImageView imageViewClose = promptsView.findViewById(R.id.imageViewClose);
        final TextView textViewMessage = promptsView.findViewById(R.id.textViewMessage);
        final LottieAnimationView lottieAnimationView = promptsView.findViewById(R.id.lottieAnimationView);
        final Button buttonRestart = promptsView.findViewById(R.id.buttonRestart);
        final Button buttonExit = promptsView.findViewById(R.id.buttonExit);

        textViewMessage.setText(message);
        lottieAnimationView.playAnimation();

        imageViewClose.setOnClickListener(view -> alertDialog.dismiss());

        buttonRestart.setOnClickListener(view -> {
            game.restartGame();
            dbref.child(id).setValue(game);
            alertDialog.dismiss();
        });

        buttonExit.setOnClickListener(view -> {
            alertDialog.dismiss();
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), HomeActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });
    }

    public void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        else
            vibrator.vibrate(300);
    }

    public float calculateRating(User user) {
        if (user.getTotal() == 0)
            return 0;

        return 5.0f * (user.getWon() + 0.5f * user.getTie()) / user.getTotal();
    }
}