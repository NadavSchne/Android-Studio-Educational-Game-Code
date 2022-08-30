package com.example.quizgame;

import android.provider.BaseColumns;

public final class ScoreContract {                                                                    // contains different constances for the SQLite
    private ScoreContract() {                                                                          //
    }
    public static class ScoresTable implements BaseColumns {                                        // create score table data
        public static final String TABLE_NAME = "quiz_scores";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_DIFFICULTY = "difficulty";
        public static final String COLUMN_CATEGORY_ID = "category_id";

    }
}
