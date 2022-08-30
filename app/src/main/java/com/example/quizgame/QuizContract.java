package com.example.quizgame;
import android.provider.BaseColumns;                                                                // 2 different tables so we can erase all questions with category X if we want to delete a category (not relevant)

public final class QuizContract {                                                                    // contains different constances for the SQLite
    private QuizContract() {                                                                          // we have 1 table for the question and 1 for the category ( with foreign key )
    }

        public static class CategoriesTable implements BaseColumns {                                 //create category table
            public static final String TABLE_NAME_EN = "quiz_categories_english";
            public static final String TABLE_NAME_HE = "quiz_categories_hebrew";  // BaseColumns - adds another coloumn to the table with auto increasing ID number
            public static final String COLUMN_NAME = "name";
        }

    public static class QuestionsTable implements BaseColumns {                                      // create question table
        public static final String TABLE_NAME_EN = "quiz_questions_en";
        public static final String TABLE_NAME_HE = "quiz_questions_he";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_ANSWER_NR = "answer_nr";
        public static final String COLUMN_DIFFICULTY = "difficulty";
        public static final String COLUMN_CATEGORY_ID = "category_id";

    }
}                                                                                                    //only use this class to contain the constances
