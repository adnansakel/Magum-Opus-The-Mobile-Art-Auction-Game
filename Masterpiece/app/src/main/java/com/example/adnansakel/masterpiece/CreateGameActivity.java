package com.example.adnansakel.masterpiece;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.adnansakel.masterpiece.model.AppConstants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Adnan Sakel on 3/28/2016.
 */
public class CreateGameActivity extends Activity implements View.OnClickListener{
    Firebase masterpieceRef;
    Button buttonJoinGame;
    ProgressDialog progress;
    TextView textViewGameNumber;
    EditText editTextUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Default call to load previous state
        super.onCreate(savedInstanceState);

        // Set the view for the main activity screen
        // it must come before any call to findViewById method
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_creategame);
        initializeComponent();
        createGame();
    }

    private void initializeComponent(){
        buttonJoinGame = (Button)findViewById(R.id.buttonJoinGame);
        textViewGameNumber = (TextView)findViewById(R.id.textviewGameNumber);
        editTextUserName = (EditText)findViewById(R.id.edittext_userName);
        buttonJoinGame.setOnClickListener(this);

    }

    private void createGame(){
        Firebase.setAndroidContext(this);
        masterpieceRef = new Firebase(AppConstants.FireBaseUri);
        progress = ProgressDialog.show(this,"","creating game ...", true);

        masterpieceRef.child("GameNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String game_number = dataSnapshot.getValue().toString();


                AppConstants.GameID = game_number;
                Map<String, Object> newGameNumber = new HashMap<String, Object>();
                newGameNumber.put(AppConstants.GameNumber, String.valueOf(Integer.valueOf(game_number) + 1));
                masterpieceRef.updateChildren(newGameNumber);
                Map<String, Object> game = new HashMap<String, Object>();
                game.put("Game", String.valueOf(Integer.valueOf(game_number)));
                game.put("Inplay", "True");
                game.put("NumberofPlayers", "1");

                //Map<String,Object>p1 = new HashMap<String, Object>();

                game.put("Players", "");
                game.put("TurnTaker", "P1");
                game.put("TurnAction", "Bank Auction");
                game.put("GamePhase", "Turns");

                Firebase gamesRef = masterpieceRef.child("Games");
                final Firebase newGameRef = gamesRef.push();
                newGameRef.setValue((game), new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        //progress.dismiss();
                        if (firebaseError != null) {
                            textViewGameNumber.setText(firebaseError.getMessage().toString());
                        } else {
                            progress.dismiss();
                            textViewGameNumber.setText(game_number);
                            AppConstants.GameRef = newGameRef.toString();
                        }
                    }


                });
                //System.out.println("New game ref: " + newGameRef.toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //progress.dismiss();
            }
        });

    }

    @Override
    public void onClick(View view){

        if(view == buttonJoinGame){
            Map<String, Object> player = new HashMap<String, Object>();
            String[]paintings = {"1","2","3","4"};
            player.put("Name",editTextUserName.getText().toString());
            player.put("Paintings",paintings);
            player.put("Cash","");
            player.put("BidAmount","");
            progress = ProgressDialog.show(this,"","joining game ...", true);
            new Firebase(AppConstants.GameRef+"/"+"Players").setValue(player, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    //progress.dismiss();
                    if (firebaseError != null) {
                        //textViewGameNumber.setText(firebaseError.getMessage().toString());
                    } else {
                        progress.dismiss();
                        //textViewGameNumber.setText(game_number);
                        //lobby activity should come here
                    }
                }
            });
        }

    }
}
