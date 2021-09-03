package com.ujjwalkumar.tictactoe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class RoomActivity extends AppCompatActivity {

    String id, password;
    boolean isStartButton = true;

    ImageView imageViewBack;
    LinearLayout linearLayoutStart, linearLayoutJoin, collapsible1, collapsible2;
    CardView cardView1, cardView2;
    TextView textViewID, textViewPassword;
    EditText editTextID, editTextPassword;
    Button buttonStart, buttonJoin;

    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = fbdb.getReference("games");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        imageViewBack = findViewById(R.id.imageViewBack);
        linearLayoutStart = findViewById(R.id.linearLayoutStart);
        linearLayoutJoin = findViewById(R.id.linearLayoutJoin);
        collapsible1 = findViewById(R.id.collapsible1);
        collapsible2 = findViewById(R.id.collapsible2);
        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        textViewID = findViewById(R.id.textViewID);
        textViewPassword = findViewById(R.id.textViewPassword);
        editTextID = findViewById(R.id.editTextID);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonStart = findViewById(R.id.buttonStart);
        buttonJoin = findViewById(R.id.buttonJoin);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        password = intent.getStringExtra("password");
        if(id==null || password==null) {
            id = "";
            password = "";
            collapsible1.setVisibility(View.VISIBLE);
            collapsible2.setVisibility(View.GONE);
        }
        else {
            collapsible1.setVisibility(View.GONE);
            collapsible2.setVisibility(View.VISIBLE);
            editTextID.setText(id);
            editTextPassword.setText(password);
        }

        imageViewBack.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), HomeActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        linearLayoutStart.setOnClickListener(view -> {
            collapsible1.setVisibility(View.VISIBLE);
            collapsible2.setVisibility(View.GONE);

//                ConstraintSet set1 = new ConstraintSet();
//                set1.connect(imageViewBack.getId(), ConstraintSet.TOP, cardView2.getId(), ConstraintSet.BOTTOM);
//                set1.applyTo(cardView1);
        });

        linearLayoutJoin.setOnClickListener(view -> {
            collapsible1.setVisibility(View.GONE);
            collapsible2.setVisibility(View.VISIBLE);
        });

        buttonStart.setOnClickListener(view -> {
            if (isStartButton) {
                id = dbref.push().getKey();
                Game game = new Game(id, FirebaseAuth.getInstance().getUid());
                password = game.getPass();
                dbref.child(id).setValue(game);
                textViewID.setText(id);
                textViewPassword.setText(password);
                buttonStart.setText("Invite");
                buttonStart.setBackgroundResource(R.drawable.background_pink_gradient);
                isStartButton = false;
                Toast.makeText(RoomActivity.this, "Waiting for other player", Toast.LENGTH_SHORT).show();

                dbref.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Game game = snapshot.getValue(Game.class);
                        if (!game.getUid2().equals("")) {
                            Intent in = new Intent();
                            in.setAction(Intent.ACTION_VIEW);
                            in.setClass(getApplicationContext(), TwoPlayerOnlineActivity.class);
                            in.putExtra("id", game.getId());
                            in.putExtra("uid1", game.getUid1());
                            in.putExtra("uid2", game.getUid2());
                            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(in);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                createDynamicLink(id, password);
            }
        });

        buttonJoin.setOnClickListener(view -> {
            id = editTextID.getText().toString();
            password = editTextPassword.getText().toString();
            if (!id.equals("") && !password.equals("")) {
                dbref.child(id).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Game game = task.getResult().getValue(Game.class);
                        if (game != null && password.equals(game.getPass())) {
                            game.setUid2(FirebaseAuth.getInstance().getUid());
                            game.setStatus(Game.STATUS_RUNNING);
                            dbref.child(id).setValue(game);

                            Intent in = new Intent();
                            in.setAction(Intent.ACTION_VIEW);
                            in.setClass(getApplicationContext(), TwoPlayerOnlineActivity.class);
                            in.putExtra("id", game.getId());
                            in.putExtra("uid1", game.getUid1());
                            in.putExtra("uid2", game.getUid2());
                            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(in);
                            finish();
                        } else {
                            Toast.makeText(RoomActivity.this, "Invalid ID or password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RoomActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(RoomActivity.this, "Empty text fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDynamicLink(String param1, String param2) {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/?id=" + param1 + "&pass=" + param2))
                .setDomainUriPrefix("https://tictactoe8577.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters( new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Join Game")
                        .setDescription("Play Tic tac toe with your friend")
                        .setImageUrl(Uri.parse("https://github.com/ujjwalkumar8577/TicTacToe/blob/master/app/src/main/res/mipmap/ic_launcher.PNG"))
                        .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String tmp = task.getResult().getShortLink().toString();
                        Intent ind = new Intent(Intent.ACTION_SEND);
                        ind.setType("text/plain");
                        ind.putExtra(Intent.EXTRA_TEXT, tmp);
                        startActivity(Intent.createChooser(ind, "Share room details"));
                    } else {
                        Toast.makeText(RoomActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Can work offline
//        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(Uri.parse("https://www.example.com/?id=" + param1 + "&pass=" + param2))
//                .setDomainUriPrefix("https://tictactoe8577.page.link")
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                .setSocialMetaTagParameters( new DynamicLink.SocialMetaTagParameters.Builder()
//                        .setTitle("Join Game")
//                        .setDescription("Play Tic tac toe with your friend")
//                        .setImageUrl(Uri.parse("https://github.com/ujjwalkumar8577/TicTacToe/blob/master/app/src/main/res/mipmap/ic_launcher.PNG"))
//                        .build())
//                .buildDynamicLink();
//
//        String tmp = dynamicLink.getUri().toString();
//        Intent ind = new Intent(Intent.ACTION_SEND);
//        ind.setType("text/plain");
//        ind.putExtra(Intent.EXTRA_TEXT, tmp);
//        startActivity(Intent.createChooser(ind, "Share room details"));
    }
}