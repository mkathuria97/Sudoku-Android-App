package com.example.mayankkathuria.sudoku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HighScore extends AppCompatActivity {

    private FirebaseDatabase database;
    private SharedPreferences settings;
    private int globalLowestHours;
    private int globalLowestMinutes;
    private int globalLowestSeconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        database = FirebaseDatabase.getInstance();
        settings = getSharedPreferences("game", MODE_PRIVATE);
        String level = settings.getString("difficultyLevel", "easy");
        int currentHours = getIntent().getIntExtra("hours", 24);
        int currentMinutes = getIntent().getIntExtra("minutes", 60);
        int currentSeconds = getIntent().getIntExtra("seconds", 60);
        int lowestHours = settings.getInt(level + "hours", 24);
        int lowestMinutes = settings.getInt(level + "minutes", 60);
        int lowestSeconds = settings.getInt(level + "seconds", 60);
        if(updateScore(currentHours,currentMinutes,currentSeconds,lowestHours, lowestMinutes, lowestSeconds)){
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(level + "hours", lowestHours);
            editor.putInt(level + "minutes", lowestMinutes);
            editor.putInt(level + "seconds", lowestSeconds);
            TextView lowestTime;
            if(level.equals("easy")) {
                lowestTime = (TextView) findViewById(R.id.easy);
            }else if(level.equals("intermediate")){
                lowestTime = (TextView) findViewById(R.id.intermediate);
            }else{
                lowestTime = (TextView) findViewById(R.id.difficult);
            }
            lowestTime.setText(lowestHours + " hours " + lowestMinutes + " minutes " + lowestSeconds + " seconds");
            editor.apply();
        }

        DatabaseReference hoursRef = database.getReference(level + "/hours");
        DatabaseReference minutesRef = database.getReference(level + "/minutes");
        DatabaseReference secondsRef = database.getReference(level + "/seconds");
        hoursRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                globalLowestHours = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        minutesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                globalLowestMinutes = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        secondsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                globalLowestSeconds = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        if(updateScore(lowestHours, lowestMinutes, lowestSeconds, globalLowestHours, globalLowestMinutes, globalLowestSeconds)){
            hoursRef.setValue(lowestHours);
            minutesRef.setValue(lowestMinutes);
            secondsRef.setValue(lowestSeconds);
            TextView globalLowestTime;
            if(level.equals("easy")) {
                globalLowestTime = (TextView) findViewById(R.id.globalEasy);
            }else if(level.equals("intermediate")){
                globalLowestTime = (TextView) findViewById(R.id.globalIntermediate);
            }else{
                globalLowestTime = (TextView) findViewById(R.id.globalDifficult);
            }
            globalLowestTime.setText(globalLowestHours + " hours " + globalLowestMinutes + " minutes " + globalLowestSeconds + " seconds");
        }
    }

    public boolean updateScore(int currentHours, int currentMinutes, int currentSeconds, int lowestHours,
    int lowestMinutes, int lowestSeconds) {

        boolean highestScoreChanged = false;

        if (currentHours < lowestHours) {
            highestScoreChanged = true;
        } else if ((currentHours == lowestHours) && (currentMinutes < lowestMinutes)) {
            highestScoreChanged = true;
        } else if ((currentHours == lowestHours) && (currentMinutes == lowestMinutes) &&
                (currentSeconds < lowestSeconds)) {
            highestScoreChanged = true;
        }

        return highestScoreChanged;
    }
}
