package com.example.quizgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ScoresAdapter extends ArrayAdapter<Score> {                                               // creating "Score" class adapter for List View

    private Score[] scores;
    private Context context;
    private  String[] difficultyLevels;


    public ScoresAdapter(@NonNull Context context, @NonNull Score[] scores) {
        super(context, -1, scores);
        this.context = context;
        this.scores = scores;

        difficultyLevels = Question.getAllDifficultyLevels(context);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {                          // Function that returns TYPE VIEW - VIEW = type of class of every UI Object
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);     // inflating items to shown on list view
        View rowView = layoutInflater.inflate(R.layout.score_item_layout, parent, false);                  //inflate to create object VIEW from XML
                                                                                                                        //
        TextView nameText = rowView.findViewById(R.id.item_username);                                                   // VIEW = type of class of every UI Object
        TextView scoreText = rowView.findViewById(R.id.item_score);
        TextView diffText = rowView.findViewById(R.id.item_difficulity);

        Score userScore = scores[position];
        nameText.setText(userScore.getUsername());                                                                     // setting data
        scoreText.setText(""+userScore.getScore());
        diffText.setText(difficultyLevels[userScore.getDifficulty()]);                                                  // setting difficulity level from spinner ->

        return rowView;
    }

    public void updateScores( Score[] scores){                                                          //updating list with the new score list
        this.scores = scores;
        notifyDataSetChanged();                                                                         // "refreshing list"
    }
}
