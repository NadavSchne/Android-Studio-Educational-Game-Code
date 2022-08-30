package com.example.quizgame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QUIZ = 1;                                                     // code for returning highscore resualt from quiz activity
    public static final String EXTRA_DIFFICULTY = "extraDifficulty";                                 // pass difficulty level on new QuizActivity intent
    public static final String EXTRA_CATEGORY_ID = "extraCategoryID";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";
    public static final String EXTRA_USER_NAME = "extraUserName";


    public static final String SHARED_PREFS = "sharedPrefs";                                          // in order to save something in shared prefs
    public static final String KEY_HIGHSCORE = "keyHighscore";                                        // key of where we save our high score in shared prefs

    private TextView textViewHighscore;                                                              // for XML TEXT VIEW

    private Spinner spinnerCategory;
    private Spinner spinnerDifficulty;
    private ImageButton imageButtonOptions;
    private int highscore;                                                                            // shown high score

    private EditText etName;

    private  QuizDbHelper dbHelper;

    private int categoryID;
    private int difficulty;

    MediaPlayer music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        music = MediaPlayer.create(this, R.raw.music);
        music.setLooping(true);
        if(!music.isPlaying())
            music.start();

        textViewHighscore = findViewById(R.id.text_view_highscore);

        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        imageButtonOptions = findViewById(R.id.btn_options);
        etName = findViewById(R.id.et_name);

        loadCategories();
        loadDifficultyLevels();

        ImageView tween=findViewById(R.id.tween);                                                    // animation
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.tween);
        tween.startAnimation(animation);

        loadHighscore();

        Button buttonStartQuiz = findViewById(R.id.button_start_quiz);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();

            }
        });

        dbHelper = QuizDbHelper.getInstance(this);




        imageButtonOptions.setOnClickListener(new View.OnClickListener() {                          // inflate a menu when pressing on "three dots sign"
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, imageButtonOptions);     // creating a pop menu and inflating options_menu we created at menu folder
                popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_music:                                                                     // id from menu folder->options_menu xml
                                if(music.isPlaying())
                                {
                                    music.pause();
                                }
                                else
                                {
                                    music.start();
                                };
                                break;

                            case R.id.action_highscore:
                                Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
                                startActivity(intent);
                        }
                        return true;
                    }
                });

                popupMenu.show();

            }
        });
    }


    private void startQuiz() {                                                                       // we want to pass category valvues to Quiz Activity
     //   Category selectedCategory = (Category) spinnerCategory.getSelectedItem();                    // create category objects -> according to selected item from the spinner
        categoryID = spinnerCategory.getSelectedItemPosition() + 1;                                                  // get the ID of it
        String categoryName = (String) spinnerCategory.getSelectedItem();                                            // get the name of it

        difficulty = spinnerDifficulty.getSelectedItemPosition();                                      // set selected difficulty from spinner

        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryID);
        intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);                                               // putEXTRA with difficulty value
        intent.putExtra(EXTRA_USER_NAME, etName.getText().toString());

        startActivityForResult(intent, REQUEST_CODE_QUIZ);                                           // starting activity to get a result back
    }                                                                                                // request code to know from which activity the result recived

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QUIZ) {                                                      // check if returned result is from the activity we wished to get result back
            if (resultCode == RESULT_OK) {                                                           // check result is ok ( returned form RESULT_OK )
                int score = data.getIntExtra(QuizActivity.NEW_SCORE, 0);                // save returned value in an integer  + defult value =0 in case no score was yet made

                // update score on db
                String name = etName.getText().toString();
                dbHelper.addScore(new Score(score, name, categoryID-1 ,difficulty));        //every quiz finish, before updating main screen's score, add the new score to all games score data base

                if (score > highscore) {
                    updateHighscore(score);
                }
            }
        }
    }
                                                                                                     // array adapter - אthe adapting unit that has all the relevent info
    private void loadCategories() {
        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);                                      // here we have to acess the categories from the DB and not like the function below ( from Question)

        // todo: read from Locale
        String lang = getResources().getConfiguration().locale == Locale.ENGLISH ? "en" : "he";

        List<Category> categories = dbHelper.getAllCategories(lang);   // use the function to get all categories

        List<String> categoriesNames = new ArrayList<>();
        for(int i=0; i< categories.size(); i++){
            categoriesNames.add(categories.get(i).getName(this));
        }
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this,                  // create array adapter
                android.R.layout.simple_spinner_item, categoriesNames);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    // set drop down item spinner
        spinnerCategory.setAdapter(adapterCategories);                                               // pass the adapter to the spinner
    }



    private void loadDifficultyLevels() {
        String[] difficultyLevels = Question.getAllDifficultyLevels(this);                               // call the function from Question *class*
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, difficultyLevels);//create array adapter for the spinner with the String[]difficultyLevels of last line
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);                                             // set the adapter on the spinner
    }


    private void loadHighscore() {                                                                     // load highscore from SP
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highscore = prefs.getInt(KEY_HIGHSCORE, 0);                                         // put value from SP to object
        textViewHighscore.setText(getText(R.string.HighScoreStart)+"" + highscore);                                       //load the HighScore Text
    }

    private void updateHighscore(int highscoreNew) {
        highscore = highscoreNew;
        textViewHighscore.setText(getText(R.string.HighScoreStart)+"" + highscore);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);                   // also save high score in shared prefs!
        SharedPreferences.Editor editor = prefs.edit();                                                // used for modifying values in a SharedPreferences object
        editor.putInt(KEY_HIGHSCORE, highscore);                                                    // use editor in order to save the value
        editor.apply();
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.ClosingActivity))
                .setMessage(getString(R.string.AreYouSure))
                .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        music.stop();
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.No), null)
                .show();
    }
}