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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

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

        collapsible1.setVisibility(View.VISIBLE);
        collapsible2.setVisibility(View.GONE);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            id = deepLink.getQueryParameter("id");
                            password = deepLink.getQueryParameter("pass");

                            collapsible1.setVisibility(View.GONE);
                            collapsible2.setVisibility(View.VISIBLE);
                            editTextID.setText(id);
                            editTextPassword.setText(password);
                        }
                        // Handle the deep link. For example, open the linked content,
                        // or apply promotional credit to the user's account.
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RoomActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

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

        linearLayoutStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsible1.setVisibility(View.VISIBLE);
                collapsible2.setVisibility(View.GONE);

//                ConstraintSet set1 = new ConstraintSet();
//                set1.connect(imageViewBack.getId(), ConstraintSet.TOP, cardView2.getId(), ConstraintSet.BOTTOM);
//                set1.applyTo(cardView1);
            }
        });

        linearLayoutJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsible1.setVisibility(View.GONE);
                collapsible2.setVisibility(View.VISIBLE);
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStartButton) {
                    id = dbref.push().getKey();
                    Game game = new Game(id, FirebaseAuth.getInstance().getUid());
                    password = game.getPass();
                    dbref.child(id).setValue(game);
                    textViewID.setText(id);
                    textViewPassword.setText(password);
                    buttonStart.setText("Invite");
                    buttonStart.setBackgroundResource(R.drawable.button3);
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
                    String tmp = id;
                    Intent ind = new Intent(android.content.Intent.ACTION_SEND);
                    ind.setType("text/plain");
                    ind.putExtra(android.content.Intent.EXTRA_TEXT, tmp);
                    startActivity(Intent.createChooser(ind, "Share room details"));
                }
            }
        });

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = editTextID.getText().toString();
                password = editTextPassword.getText().toString();
                if (!id.equals("") && !password.equals("")) {
                    dbref.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
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
                        }
                    });
                } else {
                    Toast.makeText(RoomActivity.this, "Empty text fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}