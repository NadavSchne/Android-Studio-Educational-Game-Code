package com.example.quizgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HighScoreActivity extends AppCompatActivity {

    private ListView scoresList;
    private ScoresAdapter scoresAdapter;
    private Spinner spinnerCategory;

    QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);                                      //
    private  List<Score> scores;
    private   List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        scoresList = findViewById(R.id.list_scores);                                                // LIST VIEW TO FILL
        spinnerCategory = findViewById(R.id.spinner_category);

        String[] difficultyLevels = Question.getAllDifficultyLevels(this);                               // call the function from Question *class*

        loadCategories();


        scores = dbHelper.getAllScores();                                                           // use the function to get all scores from DB


        String lang = getResources().getConfiguration().locale == Locale.ENGLISH ? "en" : "he";
        categories = dbHelper.getAllCategories(lang);

//        dbHelper.addScore(new Score(24, "Nadav", 0 ,2));

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {            // choosing what to show on each spinner selection
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // update the scores
                List<Score> filteredScores = new ArrayList<>();                                     // creating a new list

                // filter the right  scores
                for(Score score : scores){                                                           // adding to the new list values by "spinner position" ( by category)
                    if(score.getCategory() == position){
                        filteredScores.add(score);
                    }
                }

                // sort from high to low score
                filteredScores.sort((scoreA, scoreB) -> scoreB.getScore() - scoreA.getScore());

                scoresAdapter = new ScoresAdapter(HighScoreActivity.this, filteredScores.toArray(new Score[0]));
                scoresList.setAdapter(scoresAdapter);                                                                               // setting the list the with the scoreAdapter
//                scoresAdapter.updateScores(filteredScores.toArray(new Score[0]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {                                  // defult spinner select shows category "0" ( SPORT )
                // update the scores
                List<Score> filteredScores = new ArrayList<>();

                // filter the right  scores
                for(Score score : scores){
                    if(score.getCategory() == 0){
                        filteredScores.add(score);
                    }
                }


                scoresAdapter = new ScoresAdapter(HighScoreActivity.this, filteredScores.toArray(new Score[0]));
                scoresList.setAdapter(scoresAdapter);                                                                                    // setting the list the with the scoreAdapter
//                scoresAdapter.updateScores(filteredScores.toArray(new Score[0]));
            }
        });

//        spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // read the right *Difficulity* scores\
//
//
//                // update the scores
//                Score[] scores = new Score[]{};
//                scoresAdapter.updateScores(SCORES2);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        /// the initial scores (Sport/Easy)
        scoresAdapter = new ScoresAdapter(this, new Score[]{});
        scoresList.setAdapter(scoresAdapter);

    }

    private void loadCategories() {                                                                     // setting categories on spinner of activity_high_score_XML


        // todo: read from Locale
        String lang = getResources().getConfiguration().locale == Locale.ENGLISH ? "en" : "he";

        List<Category> categories = dbHelper.getAllCategories(lang);                                  // creating a category list

        List<String> categoriesNames = new ArrayList<>();
        for(int i=0; i< categories.size(); i++){
            categoriesNames.add(categories.get(i).getName(this));
        }
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this,                  // create array adapter
                android.R.layout.simple_spinner_item, categoriesNames);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    // set drop down item spinner
        spinnerCategory.setAdapter(adapterCategories);                                               // pass the adapter to the spinner
    }

}