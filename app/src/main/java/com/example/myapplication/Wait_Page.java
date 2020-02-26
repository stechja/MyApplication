package com.example.myapplication;

import android.content.Context;
import android.content.Intent;

import Objects.Investor;
import Objects.Manager;
import Objects.Session;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import Objects.Market;
import Objects.Player;

public class Wait_Page extends AppCompatActivity {

    Integer player_count;

    String count_database;
    Market market;
    DatabaseReference ref_def ;
    DatabaseReference ref_count;
    DatabaseReference player_id_list_ref;
    Session sess;
    Context context;
    Method callback;


    Integer player_count_definition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        context = (Context)this;
        try{callback= Wait_Page.class.getMethod("assign_player_roles", Method.class);}catch(NoSuchMethodException e){
           Toast.makeText(context, "No callback function defined", Toast.LENGTH_LONG);
        };

        setContentView(R.layout.activity_wait__page);
        Intent intent = getIntent();
        String player_id = intent.getStringExtra("player_id");
        String player_count_database_def="player_count";
        String player_id_list_database_def = "player_list";

        player_count_definition=1;
        sess = new Session(player_count_definition);
        count_database = "player_counter";

        player_id_list_ref = FirebaseDatabase.getInstance().getReference(player_id_list_database_def);
        ref_def = FirebaseDatabase .getInstance().getReference(player_count_database_def);
        ref_count = FirebaseDatabase .getInstance().getReference(count_database);
        ref_count.setValue(0);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        Player current = new Player(player_id);

        Query markets = db.getReference("markets");
        Query player_list = db.getReference(player_id_list_database_def);


        markets.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(context,"getting market data...", Toast.LENGTH_LONG).show();
                market = dataSnapshot.getValue(Market.class);
                if (market.getType().compareTo("P")==0){
                    sess.setPractice(market);
                }
                if (market.getType().compareTo("BOOM")==0){
                    sess.setBoom(market);
                }
                if (market.getType().compareTo("BUST")==0){
                    sess.setBust(market);}
                when_Session_configured();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                market = dataSnapshot.getValue(Market.class);
                if (market.getType().compareTo("P")==0){
                    sess.setPractice(market);
                }
                if (market.getType().compareTo("BOOM")==0){
                    sess.setBoom(market);
                }
                if (market.getType().compareTo("BUST")==0){
                    sess.setBust(market);}
                }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                market = dataSnapshot.getValue(Market.class);
                if (market.getType().compareTo("P")==0){
                    sess.setPractice(null);
                }
                if (market.getType().compareTo("BOOM")==0){
                    sess.setBoom(null);
                }
                if (market.getType().compareTo("BUST")==0){
                    sess.setBust(null);}



            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        player_list.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(context," Adding players...", Toast.LENGTH_LONG).show();
                updatePlayerCountAdd(ref_count);
                when_Session_configured();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                updatePlayerCountSubtract(ref_count);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.getReference(player_id_list_database_def).child(player_id).setValue(current);






    }


    private void when_Session_configured(){
        if (sess!=null){
        if (sess.isValid()&&player_count!=null &&player_count_definition!=null){
            Toast.makeText(context,"Assigning player roles...", Toast.LENGTH_LONG).show();
            assign_player_roles(player_id_list_ref);


        }}
    }


    private void assign_player_roles(DatabaseReference player_id_list_database){

        Query get_all_players = player_id_list_database;
        get_all_players.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Random random = new Random();
                ArrayList<Player> player_list_extra = new ArrayList<>();
                int num_man = random.nextInt(1)+3;
                int the_count = 0;

                for (DataSnapshot s: dataSnapshot.getChildren()){
                    Player p = s.getValue(Player.class);
                    if (the_count%num_man==0){
                        p.setType("M");
                        Manager m = (Manager) p;
                        FirebaseDatabase.getInstance().getReference("Managers").child(p.getID()).setValue(m);
                    }
                    else {
                        p.setType("I");
                        Investor i = (Investor) p;
                        FirebaseDatabase.getInstance().getReference("Investors").child(p.getID()).setValue(i);
                    }

                    the_count+=1;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void updatePlayerCountSubtract(DatabaseReference player_counter) {
        player_counter.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer p;
                if ( mutableData==null) {
                    return Transaction.success(mutableData);
                }else {
                     p = mutableData.getValue(Integer.class);

                    p-=1;
                    if (p<0){p=0;}
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                player_count = p;
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                player_count= dataSnapshot.getValue(Integer.class);
                // Transaction completed
                //Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void updatePlayerCountAdd(DatabaseReference player_counter) {
        player_counter.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                if (mutableData==null) {
                    return Transaction.success(mutableData);
                }
                Integer p = mutableData.getValue(Integer.class);
                if (p == null) {
                    p=1;
                }else {
                    p+=1;

                }
                // Set value and report transaction success
                mutableData.setValue(p);
                player_count = p;
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                player_count= dataSnapshot.getValue(Integer.class);
                // Transaction completed
                //Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            return true;
        }
        return false;
    }

}
