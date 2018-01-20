package com.example.mayankkathuria.sudoku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

public class Settings extends AppCompatActivity {

    private SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = getSharedPreferences("game", MODE_PRIVATE);
        //settings = PreferenceManager
          //      .getDefaultSharedPreferences("gameData");
        //Log.d("mayank", "" + "mayank");
        String level = settings.getString("difficultyLevel", "easy");
        RadioButton button;

        if (level.equals("easy")) {
            button = (RadioButton) findViewById(R.id.easy);
        } else if (level.equals("intermediate")) {
            button = (RadioButton) findViewById(R.id.globalIntermediate);
        } else {
            button = (RadioButton) findViewById(R.id.hard);
        }

        button.setChecked(true);
    }

    /**
     *
     * @param view RadioGroup for size
     */
    public void onRadioButtonClicked(View view) {

        SharedPreferences.Editor editor = settings.edit();

        //checks whether the button is checked or not
        boolean checked = ((RadioButton) view).isChecked();

        //check which radio button was clicked
        switch(view.getId()) {
            case R.id.easy:
                if (checked) {
                    editor.putString("difficultyLevel", "easy");
                }
                break;

            case R.id.globalIntermediate:
                if (checked) {
                    editor.putString("difficultyLevel", "intermediate");
                }
                break;

            case R.id.hard:
                if (checked) {
                    editor.putString("difficultyLevel", "difficult");
                }
                break;
        }

        editor.apply();
    }
}
