package com.example.mayankkathuria.sudoku;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Sudoku extends AppCompatActivity {

    final int SQUARE_HEIGHT = 150;
    final int SQUARE_WIDTH = 100;
    final int NUM_OF_SQUARES = 9;
    final int[][] game = new int[9][9];
    int flag = 0;
    ArrayList<Integer> missingSquaresId = new ArrayList<Integer>();
    HashMap<Integer, Integer> columnClues = new HashMap<Integer, Integer>();

    private Timer timer;

    //seconds taken by the user to solve the game
    private int seconds;

    //minutes taken by ths user to solve the game
    private int minutes;

    //hours taken by the user to solve the game
    private int hours;

    SharedPreferences settings;
    private String level;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        settings = getSharedPreferences("game", Context.MODE_PRIVATE);
        level = settings.getString("difficultyLevel", "easy");
        buildSudoku();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void buildSudoku() {
        //view that contain other views
        final ConstraintLayout container = (ConstraintLayout) findViewById(R.id.container);
        for (int i = 0; i < 81; i++) {
            final EditText square = new EditText(this);
            square.setWidth(SQUARE_WIDTH);
            square.setHeight(SQUARE_HEIGHT);
            int xPos = i % NUM_OF_SQUARES;
            int yPos = (int) Math.floor(i / NUM_OF_SQUARES);
            square.setX((xPos + 2) * SQUARE_WIDTH);
            square.setY((yPos + 2) * SQUARE_HEIGHT);
            int id = Integer.parseInt(yPos + "" + xPos);
            square.setId(id);
            square.setTextColor(Color.BLACK);
            square.setGravity(Gravity.CENTER);
            if(yPos == 0) {
                square.setBottom(10);
            }
            square.setEnabled(false);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.GRAY);
            //gd.setCornerRadius(5);
            gd.setStroke(1, Color.BLACK);
            //square.setBackground(getResources().getDrawable(R.drawable.shape));
            square.setBackgroundDrawable(gd);
            square.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String n = square.getText().toString();
                    square.setText(n);
                }
            });
            container.addView(square);
        }
        generateGame(0);
        if (level.equals("easy")) {
            removeClues(3, 3, 3);
        } else if(level.equals("intermediate")){
            removeClues(2, 3, 2);
        }else{
            removeClues(1, 4, 1);
        }
        final Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean p = checkSolution();
                if(p){
                    if (timer != null) {
                        timer.cancel();
                    }
                    Intent intent = new Intent();
                    intent.putExtra("hours", 24);
                    intent.putExtra("minutes", 60);
                    intent.putExtra("seconds", 60);
                    startActivity(intent);

                }
            }
        });

    }

    public void generateGame(int index) {
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.container);
        if (index < 81) {
            int x = index % 9;
            int y = index / 9;
            ArrayList<Integer> numbers = new ArrayList<Integer>();
            for (int i = 1; i <= 9; i++) {
                numbers.add(i);
            }
            Collections.shuffle(numbers);
            int number = getNextPossibleNumber(numbers, x, y);
            if (number != -1) {
                game[y][x] = number;
                int id = Integer.parseInt(y + "" + x);
                EditText tv = (EditText) container.findViewById(id);
                tv.setText("" + number);
                generateGame(index + 1);
            } else {
                int flag = 0;
                int i = 0;
                while(i < x && flag != 1){
                    int temp = game[y][i];
                    game[y][i] = 0;
                    if (checkRow(y, x, temp)) {
                        int id = Integer.parseInt(y + "" + x);
                        EditText et = (EditText) container.findViewById(id);
                        et.setText("" + temp);
                        game[y][x] = temp;
                        ArrayList<Integer> array = missing(y, i);
                        change(y, i, temp, array);
                        flag = 1;
                    } else {
                        game[y][i] = temp;
                    }
                    i++;

                }
            }
            generateGame(index + 1);
            startTimer();
        }

    }


    public void change(int y, int i, int prev, ArrayList<Integer> array) {
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.container);
        flag = 0;
        for(int j = 0; j < array.size(); j++){
            //stop if flag is 1
            int n = array.get(j);
            if(checkRow(y, i, n)) {
                flag = 1;
                int id = Integer.parseInt(y + "" + i);
                EditText et = (EditText) container.findViewById(id);
                et.setText("" + n);
                game[y][i] = n;
            }
        }

        int k = 0;
        while(k < 9 && flag != 1){
            if (i != k && game[y][k] != 0 && game[y][k] != prev) {
                int temp = game[y][k];
                game[y][k] = 0;

                if (checkRow(y, i, temp)) {
                    int id = Integer.parseInt(y + "" + i);
                    EditText et = (EditText) container.findViewById(id);
                    et.setText("" + temp);
                    game[y][i] = temp;
                    change(y, k, temp, array);
                } else {
                    game[y][k] = temp;
                }
            }
            k++;


        }


    }

    public ArrayList<Integer> missing(int row, int column){
        ArrayList<Integer> a = new ArrayList<Integer>();
        for(int i = 1; i <= 9; i++){
            a.add(i);
        }
        for(int i = 0; i < 9; i++){
            if(i != column && game[row][i] != 0) {
                int index = a.indexOf(game[row][i]);
                if(index != -1) {
                    a.remove(index);
                }
            }
        }

        Collections.shuffle(a);
        return a;
    }


    public int getNextPossibleNumber(ArrayList<Integer> numbers, int x, int y) {
        while (numbers.size() > 0) {
            int num = numbers.remove(0);
            if (checkRow(y, x,  num)) {
                return num;
            }
        }
        return -1;
    }


    public boolean checkRow(int row, int column, int n) {
        for (int i = 0; i < 9; i++) {
            if (game[row][i] == n || game[i][column] == n || game[((row/3) * 3 + (i/3))][(column/3) * 3 + (i%3)] == n) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void removeClues(int min, int max, int minCol){
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.container);
        for(int j = 0; j < 9; j++){
            columnClues.put(j, 9);
        }

        for (int i = 0; i < 9; i++) {
            int random = (int) (Math.random() * max) + min;
            int p = 9 - random;
            while (p > 0) {
                int randomColumn = (int) (Math.random() * 9);
                int id = Integer.parseInt(i + "" + randomColumn);
                if (!missingSquaresId.contains(id) && (columnClues.get(randomColumn) > minCol)) {
                    EditText et = (EditText) container.findViewById(id);
                    game[i][randomColumn] = -1;
                    et.setText("");
                    et.setEnabled(true);
                    missingSquaresId.add(id);
                    et.setBackgroundColor(Color.WHITE);
                    et.setBackground(getResources().getDrawable(R.drawable.shape));
                    columnClues.put(randomColumn, columnClues.get(randomColumn) - 1);
                    p--;
                }
            }

        }


    }

    public boolean checkSolution(){
        ConstraintLayout container = (ConstraintLayout) findViewById(R.id.container);
        for(int i = 0; i < missingSquaresId.size() ; i++){
            int row = missingSquaresId.get(i)/10;
            int column = missingSquaresId.get(i)%10;
            int id = Integer.parseInt(row + "" + column);
            EditText et = (EditText) container.findViewById(id);
            int n = Integer.parseInt(String.valueOf(et.getText()));
            if(!checkRow(row, column, n)){
              return false;
            }


        }
        return true;

    }

    public void startTimer(){

        if (timer != null) {
            timer.cancel();
        }

        seconds = 0;
        minutes = 0;
        hours = 0;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            int iClicks = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        TextView timerTextView = (TextView) findViewById(R.id.timer);

                        // task to be done every 1000 milliseconds
                        iClicks = iClicks + 1;
                        seconds = iClicks % 60;
                        minutes = (iClicks / 60);
                        hours = (minutes / (60));

                        timerTextView.setText(String.valueOf(String.format("%d: %d: %d",
                                hours, minutes, seconds)));
                    }
                });

            }
        }, 0, 1000);
    }

}
