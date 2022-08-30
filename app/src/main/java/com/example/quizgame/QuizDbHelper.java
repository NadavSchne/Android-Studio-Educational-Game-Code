package com.example.quizgame;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.quizgame.QuizContract.*;
import com.example.quizgame.ScoreContract.*;
import java.util.ArrayList;
import java.util.List;


public class QuizDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuizGame.db";                                      //name of DB
    private static final int DATABASE_VERSION = 10;                                                  //number of version

    private static QuizDbHelper instance;
    private SQLiteDatabase db;                                                                       // refference to the actual DB
    private Context context;

    private QuizDbHelper(Context context) {                                                          // private constructor so we dont create new objets and only return the same one
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized QuizDbHelper getInstance(Context context) {                           // synchronized -> if we want to acess quizDBhelper from multiple threads
        if (instance == null) {                                                                      // only innitiolize a new QuizDbHelper once!
            instance = new QuizDbHelper(context.getApplicationContext());                            // we will only have 1 instance of our DB
        }
        return instance;
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE_EN = "CREATE TABLE " +                                 // SQLite statement to create category table with auto increment ID
                CategoriesTable.TABLE_NAME_EN + "( " +
                CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoriesTable.COLUMN_NAME + " TEXT " +
                ")";

        final String SQL_CREATE_QUESTIONS_TABLE_EN = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME_EN + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NR + " INTEGER, " +
                QuestionsTable.COLUMN_DIFFICULTY + " TEXT, " +
                QuestionsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY(" + QuestionsTable.COLUMN_CATEGORY_ID + ") REFERENCES " +               // make category_ID a foreign key for the category table which references to ID column of category table
                CategoriesTable.TABLE_NAME_EN + "(" + CategoriesTable._ID + ")" + "ON DELETE CASCADE" + // ODC -> if a category is deleted all the question will be deleted asweell ( not in use )
                ")";

        final String SQL_CREATE_CATEGORIES_TABLE_HE = "CREATE TABLE " +                                 // SQLite statement to create category table with auto increment ID
                CategoriesTable.TABLE_NAME_HE + "( " +
                CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoriesTable.COLUMN_NAME + " TEXT " +
                ")";

        final String SQL_CREATE_QUESTIONS_TABLE_HE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME_HE + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NR + " INTEGER, " +
                QuestionsTable.COLUMN_DIFFICULTY + " TEXT, " +
                QuestionsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY(" + QuestionsTable.COLUMN_CATEGORY_ID + ") REFERENCES " +               // make category_ID a foreign key for the category table which references to ID column of category table
                CategoriesTable.TABLE_NAME_HE + "(" + CategoriesTable._ID + ")" + "ON DELETE CASCADE" + // ODC -> if a category is deleted all the question will be deleted asweell ( not in use )
                ")";


        final String SQL_CREATE_SCORES_TABLE = "CREATE TABLE " +
                ScoresTable.TABLE_NAME + " ( " +
                ScoresTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ScoresTable.COLUMN_NAME + " TEXT, " +
                ScoresTable.COLUMN_SCORE + " INTEGER, " +
                ScoresTable.COLUMN_DIFFICULTY + " INTEGER, " +
                ScoresTable.COLUMN_CATEGORY_ID + " INTEGER" +
                ")";


        db.execSQL(SQL_CREATE_CATEGORIES_TABLE_EN);                                                     // actual execute
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE_EN);

        db.execSQL(SQL_CREATE_CATEGORIES_TABLE_HE);                                                     // actual execute
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE_HE);

        db.execSQL(SQL_CREATE_SCORES_TABLE);

        fillCategoriesTable();
        fillQuestionsTableEnglish(context);
        fillQuestionsTableHebrew(context);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {                       // drop table incase of upgrading the db ( OnCreate is only called on first create or after deleting app
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME_EN);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME_EN);
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME_HE);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME_HE);
        db.execSQL("DROP TABLE IF EXISTS " + ScoresTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {                                                     // called every time we open the DB - enable the foreign key
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable() {                                                             // create category objects and insert to the table
        Category c1 = new Category("SPORT");
        addCategory(c1);
        Category c2 = new Category("MUSIC");
        addCategory(c2);
        Category c3 = new Category("MATH");
        addCategory(c3);
    }

    private void addCategory(Category category) {                                                    // insert the category to the table
        ContentValues cv = new ContentValues();                                                      // ContentValues - This class is used to store a set of values
        cv.put(CategoriesTable.COLUMN_NAME, category.getName(context));                                    //want to insert to COLUMN_NAME , value from category.getName
        db.insert(CategoriesTable.TABLE_NAME_EN, null, cv);
        db.insert(CategoriesTable.TABLE_NAME_HE, null, cv);
    }

    private void addQuestion(Question question, String lang) {                                                    // adding the question to the DB
        ContentValues cv = new ContentValues();                                                     // ContentValues - This class is used to store a set of values
        cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        cv.put(QuestionsTable.COLUMN_DIFFICULTY, question.getDifficulty());
        cv.put(QuestionsTable.COLUMN_CATEGORY_ID, question.getCategoryID());

        switch (lang){
            case "en":
                db.insert(QuestionsTable.TABLE_NAME_EN, null, cv);                              //insert the data of our ContextValues object into the DB
                break;

            case "he":
                db.insert(QuestionsTable.TABLE_NAME_HE, null, cv);                              //insert the data of our ContextValues object into the DB
                break;
        }

    }


    public void addScore(Score score) {                                                          // adding a Score[] to the table
        ContentValues cv = new ContentValues();                                                     // ContentValues - This class is used to store a set of values
        cv.put(ScoresTable.COLUMN_NAME, score.getUsername());
        cv.put(ScoresTable.COLUMN_SCORE, score.getScore());
        cv.put(ScoresTable.COLUMN_DIFFICULTY, score.getDifficulty());
        cv.put(ScoresTable.COLUMN_CATEGORY_ID, score.getCategory());

        db.insert(ScoresTable.TABLE_NAME, null, cv);
    }

    public List<Score> getAllScores() {                                                       // create a rawQuery from DB to get all scores
        List<Score> scoreList = new ArrayList<>();                                             // this method read the categories from the DB and returns them as a list
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ScoresTable.TABLE_NAME  , null);
        if (c.moveToFirst()) {
            do {
                Score score = new Score();
                score.setId(c.getInt(c.getColumnIndex(ScoresTable._ID)));
                score.setUsername(c.getString(c.getColumnIndex(ScoresTable.COLUMN_NAME)));
                score.setScore(c.getInt(c.getColumnIndex(ScoresTable.COLUMN_SCORE)));
                score.setDifficulty(c.getInt(c.getColumnIndex(ScoresTable.COLUMN_DIFFICULTY)));
                score.setCategory(c.getInt(c.getColumnIndex(ScoresTable.COLUMN_CATEGORY_ID)));
                scoreList.add(score);
            } while (c.moveToNext());
        }
        c.close();
        return scoreList;
    }

    public List<Category> getAllCategories(String lang) {                                                       // create a rawQuery from DB to get all categories
        List<Category> categoryList = new ArrayList<>();                                             // this method read the categories from the DB and returns them as a list
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " +  (lang.equals("en") ?  CategoriesTable.TABLE_NAME_EN :  CategoriesTable.TABLE_NAME_EN) , null);
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt(c.getColumnIndex(CategoriesTable._ID)));
                category.setName(c.getString(c.getColumnIndex(CategoriesTable.COLUMN_NAME)));
                categoryList.add(category);
            } while (c.moveToNext());
        }
        c.close();
        return categoryList;
    }

    public ArrayList<Question> getAllQuestions(String lang) {                                                   // retriving questions out of the DB ( with no difficulty and category selection )
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();                                                                 //
        Cursor c = db.rawQuery("SELECT * FROM " + (lang.equals("en") ?  QuestionsTable.TABLE_NAME_EN :  QuestionsTable.TABLE_NAME_EN), null); // creating a query out of our DB - selecting all questions
        if (c.moveToFirst()) {                                                                       // moving cursor to the first table entry
            do {                                                                                     // about cursor - This interface provides random read-write access to the result set returned by a database query.
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuestionsTable._ID)));                     //creating a new question object with the data from the DB
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            } while (c.moveToNext());                                                                      // move while theres still an entry to move to
        }
        c.close();
        return questionList;                                                                         //returning an ArrayList after retriving all the questions data from the DB and store them on the ArrayList
    }


    public ArrayList<Question> getQuestions(int categoryID,int difficulty, String lang) {                                    // with difficulty selection
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String selection = QuestionsTable.COLUMN_CATEGORY_ID + " = ? " +
                " AND " + QuestionsTable.COLUMN_DIFFICULTY + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(categoryID), ""+difficulty};
        Cursor c = db.query(                                                                         // query to recive back question by difficulty level and category
                lang.equals("en") ? QuestionsTable.TABLE_NAME_EN : QuestionsTable.TABLE_NAME_HE,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
      //  Cursor c = db.rawQuery("SELECT * FROM " +  QuestionsTable.TABLE_NAME + " WHERE "  +  QuestionsTable.COLUMN_CATEGORY_ID + " = "  + categoryID + " AND " +   QuestionsTable.COLUMN_DIFFICULTY + " = " + difficulty, null);



        if (c.moveToFirst()) {                                                                      // moving cursor to the first table entry
            do {
                Question question = new Question();                                                 //creating a new question object with the data from the DB
                question.setId(c.getInt(c.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            } while (c.moveToNext());
        }
        c.close();
        return questionList;                                                                        //returning a List of questions by category and difficulty level
    }




    private void fillQuestionsTableHebrew(Context context) {
        final int DIFFICULTY_EASY = 0;
        final int DIFFICULTY_MEDIUM = 1;
        final int DIFFICULTY_HARD = 2;// creating new Question object and "send it" to a constructor with the following parameters
        Question q46 = new Question("באיזה ספורט משתמשים בכדור, מחבט ורשת?",           // SPORT EASY START
                "טניס ", "כדורגל", " כדורסל", 1,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q46, "he");

        Question q47 = new Question("באיזה ספורט מוטבע הביטוי 'הום ראן' ",
                "כדורגל", "בייס בול", " שחייה", 2,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q47, "he");

        Question q3 = new Question("באיזה ספורט שיחק מייקל גורדן",
                "אופנים", "איגרוף", "כדורגל", 3,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q3, "he");

        Question q4 = new Question("באיזה ספורט השחקנים משתמשים בראש, "+"\r\n"+" כתפיים ורגליים כדי להזיז כדור?",
                "טניס ", " כדורגל", " כדורסל", 2,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q4, "he");

        Question q5 = new Question("איזה ספורט משחקים בוימבלדון?",
                "טניס ", "גלישה", "ריצה", 1,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q5, "he");                                                                  // SPORT EASY END

        Question q6 = new Question("מנין נבעו המשחקים האולימפים?",                       // SPORT MEDIUM START
                "איטליה", "יוון", "ישראל", 2,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q6, "he");

        Question q7 = new Question("באיזה ספורט קיים הביטוי 'חור באחד'",
                "סנוקר", "כדורגל", "גולף", 3,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q7, "he");

        Question q8 = new Question("איזה ספורט הוא היחידי ששיחקו בירח",
                "גולף", "כדורגל", "באולינג", 1,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q8, "he");

        Question q9 = new Question("באיזה ספורט השתתף מוחמד עלי?",
                "אייגרוף", "כדורגל", "היאבקות", 1,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q9, "he");

        Question q10 = new Question("איזה סוג מרוץ הוא הטור דה פראנס?",
                "טיפוס", "מרוץ אופנועים", "מירוץ אופניים", 2,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q10,"he");                                                                                // SPORT MEDIUM END

        Question q11 = new Question("מיהו הרוכב הישראלי הראשון שהתחרה בטור דה פראנס?",    // SPORT HARD START
                "גיא ניב", "גיא שגיב", "שובי גולד", 1,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q11, "he");

        Question q12 = new Question("באיזה ספורט משתמשים בכדור ומחבט עץ?",
                "הוקי", "בדמינטון", "קרוקט", 3,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q12, "he");

        Question q13 = new Question("מהו קוטר טבעת הכדורסל?",
                "16 אינץ'", "18 אינץ'", "20 אינץ'", 2,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q13, "he");

        Question q14 = new Question(" באיזה ספורט קיים שימוש בביטוי 'דג מסריח'?",
                "סנואו-בורד ", "סקי", "סקייטבורד", 1,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q14, "he");

        Question q15 = new Question("איזה מדינה שיחקה בכל מונדיאל?",
                "אנגליה", "ספרד", "ברזיל", 3,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q15,"he");                                                                              // SPORT HARD END

        Question q16 = new Question("14+24 = ?",                                              // Math EASY START
                "38 ", " 36", " 42", 1,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q16, "he");

        Question q17 = new Question("4 X 3 = ?",
                "7", "12", " 43", 2,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q17, "he");

        Question q18 = new Question("4+2+1+0 = ?",
                "6", "0", " 7", 3,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q18, "he");

        Question q19 = new Question("(5+2)+3 = ?",
                "21 ", " 13", " 10", 3,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q19, "he");

        Question q20 = new Question("10 X 10 + 10 = ?",
                "100 ", "110", "101", 2,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q20, "he");                                                                  // Math EASY END

        Question q21 = new Question(" 2 + 10 X 2 = ?",                       // Math MEDIUM START
                "24", "22", "14", 2,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q21, "he");

        Question q22 = new Question(" 99 -33 + 44 = ?",
                "139", "105", "110", 3,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q22, "he");

        Question q23 = new Question("(100-92-7) X 1 + 4 = ?",
                "5", "20", "10", 1,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q23, "he");

        Question q24 = new Question("8 X 7 - 20 = ?",
                "40", "36", "38", 2,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q24, "he");

        Question q25 = new Question("12 X 10 X 10 = ?",
                "1,220", "1,200 Race", "1.200", 2,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q25,"he");                                                                     // Math MEDIUM END

        Question q26 = new Question("12 X 12 X 12 = ?",    // Math HARD START
                "14,000", "1,728", "4,1200", 1,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q26, "he");

        Question q27 = new Question("20 X 20 - 200= ?",
                "100", "0", "400", 3,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q27, "he");

        Question q28 = new Question("(1+1+1)+(3*3*3)+444 = ?",
                "543", "474", "500", 2,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q28, "he");

        Question q29 = new Question("120/5 + 76 = ?",
                "100 ", "81", "120", 1,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q29, "he");

        Question q30 = new Question("1+2+3+4+5+6+7+8+9+10 = ?",
                "(2 X (10 X (10+1))", "5+5+5+5+5+5+5+5+5+5", "((10+1) X 10 ) / 2", 3,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q30,"he");                                                                              // Math HARD END


        Question q31 = new Question("איזה שם כלי הקשה נקרא על פי צורתו?",                                              // Music EASY START
                "עיגול ", "מרובע", "משולש", 3,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q31, "he");

        Question q32 = new Question("כמה מיתרים יש לכינור?",
                "4", "6", " 1", 1,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q32, "he");

        Question q33 = new Question("כמה מיתרים יש לגיטרה רגילה?",
                "5", "4", " 6", 3,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q33, "he");

        Question q34 = new Question("איזה צבע קלידים יש יותר בפסנתר?",
                "שחור ", "לבן", "אותו דבר", 2,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q34, "he");

        Question q35 = new Question("ממה עשויה קשת של כינור?",
                "בד ", "שיער סוס", "שיער כלב", 2,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q35, "he");                                                                  // Music EASY END

        Question q36 = new Question("כמה קלידים של לפסנתר?",                                      // Music MEDIUM START
                "76", "88", "100", 2,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q36, "he");

        Question q37 = new Question("באיזה סרט מופיע השיר 'LET IT GO'?",
                "מואנה", "מלך האריות", "פרוזן", 3,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q37, "he");

        Question q38 = new Question("מי היה הגיטריסט המוביל הראשון של מטאליקה?",
                "דייב מסטיין", "ג'יימס הטפילד", " קירק האמט", 1,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q38, "he");

        Question q39 = new Question("באיזה מדינה מתקיים פסטיבל 'וואקן'?",
                "גרמניה", "הולנד", "איטליה", 1,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q39, "he");

        Question q40 = new Question("מה היה שמו האמיתי של פרדי מרקורי?",
                "אדי ואן הלן", "פארוק בולסרה", "קונור מקגרגור", 2,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q40,"he");                                                                    // Music MEDIUM END

        Question q41 = new Question("למי נתן פול מקרטני קרדיט על כל אשר ידע בעולם המוזיקה?",                                       // Music HARD START
                "ריצ'ארד הקטן", "ג'ימי הנדריקס", "באך", 1,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q41, "he");

        Question q42 = new Question("איזה צבע של הממתק אמ.אן.אמ שנא אדי ואן הלן?",
                "צהוב", "אדום", "חום", 3,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q42, "he");

        Question q43 = new Question("איזה סרט של מארבל שכה בשני פרסי גרמי?",
                "קפטיין אמריקה", "הפנתר השחור", "ספיידרמן", 2,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q43, "he");

        Question q44 = new Question("איזה אומן הקליט שניים מהאלבומים המצליחים "+"\r\n"+" ביותר שלו מאחורי סורג ובריח?",
                "ג'ון טרבולטה", "אלטון ג'ון", "ג'וני קאש", 3,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q44, "he");

        Question q45 = new Question("למי הוצע השיר WRECKING BALL לפני מיילי סיירוס?",
                "אדל", "בריטני ספירס", "ביונסה", 3,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q45,"he");                                                                              // Music HARD END
    }

    private void fillQuestionsTableEnglish(Context context) {
        final int DIFFICULTY_EASY = 0;
        final int DIFFICULTY_MEDIUM = 1;
        final int DIFFICULTY_HARD = 2;// creating new Question object and "send it" to a constructor with the following parameters

        Question q1 = new Question("What sports uses a racket, ball, and a net?",           // SPORT EASY START
                "tennis ", " soccer", " basketball", 1,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q1, "en");

        Question q2 = new Question("What sport coined the phrase home run?",
                "football", "baseball", " swimming", 2,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q2, "en");

        Question q3 = new Question("What sport was Michael Jordan "+"\r\n"+"a player for?",
                "Cycling", "Boxing", " Basketball", 3,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q3, "en");

        Question q4 = new Question("In what sport do players use their"+"\r\n"+" heads, knees, feet, and elbows"+"\r\n"+" to control the ball?",
                "tennis ", " soccer", " basketball", 2,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q4, "en");

        Question q5 = new Question("What sport is played at Wimbledon?",
                "tennis ", "Surfing", "Running", 1,
                DIFFICULTY_EASY, Category.SPORTS);
        addQuestion(q5, "en");                                                                  // SPORT EASY END

        Question q6 = new Question("Where did the Olympic games"+"\r\n"+" originate?",                       // SPORT MEDIUM START
                "Italy", "Greece", "Israel", 2,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q6, "en");

        Question q7 = new Question("In what sport can you get"+"\r\n"+" a hole in one?",
                "Pool", "Football", "Golf", 3,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q7, "en");

        Question q8 = new Question("What is the only sport to be played"+"\r\n"+"on the moon?",
                "Golf", "Soccer", "Bowling", 1,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q8, "en");

        Question q9 = new Question("In which sport was"+"\r\n"+" Muhammad Ali popular?",
                "Boxing", "Soccer", "Wrestling", 1,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q9, "en");

        Question q10 = new Question("What type of race is"+"\r\n"+" the Tour de France?",
                "Stairs Climbing", "Motorcycling Race", "Bike Race", 2,
                DIFFICULTY_MEDIUM, Category.SPORTS);
        addQuestion(q10,"en");                                                                   // SPORT MEDIUM END

        Question q11 = new Question("Who is the first Israeli cycler who"+"\r\n"+"participated in the Tour de France?",    // SPORT HARD START
                "Guy Niv", "Guy Sagiv", "Shubi Goldstein", 1,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q11, "en");

        Question q12 = new Question("In what sport do you use "+"\r\n"+"a wooden ball and mallet?",
                "Hockey", "Badminton", "Croquet", 3,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q12, "en");

        Question q13 = new Question("How big is the diameter of a "+"\r\n"+"basketball hoop?",
                "16 inches", "18 inches", "20 inches", 2,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q13, "en");

        Question q14 = new Question(" In which sport are the terms "+"\r\n"+"“stale fish” used?",
                "Snowboarding ", "Ski", "Skateboarding", 1,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q14, "en");

        Question q15 = new Question("What is the only country to play "+"\r\n"+"in every World Cup?",
                "England", "Spain", "Brazil", 3,
                DIFFICULTY_HARD, Category.SPORTS);
        addQuestion(q15,"en");                                                                              // SPORT HARD END

        Question q16 = new Question("14+24 = ?",                                              // Math EASY START
                "38 ", " 36", " 42", 1,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q16, "en");

        Question q17 = new Question("4 X 3 = ?",
                "7", "12", " 43", 2,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q17, "en");

        Question q18 = new Question("4+2+1+0 = ?",
                "6", "0", " 7", 3,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q18, "en");

        Question q19 = new Question("(5+2)+3 = ?",
                "21 ", " 13", " 10", 3,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q19, "en");

        Question q20 = new Question("10 X 10 + 10 = ?",
                "100 ", "110", "101", 2,
                DIFFICULTY_EASY, Category.MATH);
        addQuestion(q20, "en");                                                                  // Math EASY END

        Question q21 = new Question(" 2 + 10 X 2 = ?",                       // Math MEDIUM START
                "24", "22", "14", 2,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q21, "en");

        Question q22 = new Question(" 99 -33 + 44 = ?",
                "139", "105", "110", 3,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q22, "en");

        Question q23 = new Question("(100-92-7) X 1 + 4 = ?",
                "5", "20", "10", 1,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q23, "en");

        Question q24 = new Question("8 X 7 - 20 = ?",
                "40", "36", "38", 2,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q24, "en");

        Question q25 = new Question("12 X 10 X 10 = ?",
                "1,220", "1,200 Race", "1.200", 2,
                DIFFICULTY_MEDIUM, Category.MATH);
        addQuestion(q25,"en");                                                                     // Math MEDIUM END

        Question q26 = new Question("12 X 12 X 12 = ?",    // Math HARD START
                "14,000", "1,728", "4,1200", 1,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q26, "en");

        Question q27 = new Question("20 X 20 - 200= ?",
                "100", "0", "400", 3,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q27, "en");

        Question q28 = new Question("(1+1+1)+(3*3*3)+444 = ?",
                "543", "474", "500", 2,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q28, "en");

        Question q29 = new Question("120/5 + 76 = ?",
                "100 ", "81", "120", 1,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q29, "en");

        Question q30 = new Question("1+2+3+4+5+6+7+8+9+10 = ?",
                "(2 X (10 X (10+1))", "5+5+5+5+5+5+5+5+5+5", "((10+1) X 10 ) / 2", 3,
                DIFFICULTY_HARD, Category.MATH);
        addQuestion(q30,"en");                                                                              // Math HARD END


        Question q31 = new Question("Which percussion instrument is named "+"\r\n"+"after its shape?",                                              // Music EASY START
                "circle ", "square", "triangle", 3,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q31, "en");

        Question q32 = new Question("How many strings does a violin have?",
                "4", "6", " 1", 1,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q32, "en");

        Question q33 = new Question("How many strings does"+"\r\n"+" a regular guitar have?",
                "5", "4", " 6", 3,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q33, "en");

        Question q34 = new Question("Are there more black keys or white keys "+"\r\n"+"on a piano?",
                "Black ", "White", "the same", 2,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q34, "en");

        Question q35 = new Question("What is the bow of a violin "+"\r\n"+"usually made from?",
                "fabric ", "Horse hair", "Dog hair", 2,
                DIFFICULTY_EASY, Category.MUSIC);
        addQuestion(q35, "en");                                                                  // Music EASY END

        Question q36 = new Question("How many keys does a piano have?",                                      // Music MEDIUM START
                "76", "88", "100", 2,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q36, "en");

        Question q37 = new Question("Which film features the song"+"\r\n"+" 'Let It Go'?",
                "Moana", "Lion King", "Frozen", 3,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q37, "en");

        Question q38 = new Question("Who was the first lead guitarist"+"\r\n"+" of Metallica?",
                "Dave Mustain", "James Hetfield", "Kirk Hammet", 1,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q38, "en");

        Question q39 = new Question("In which country does 'Wacken' "+"\r\n"+"festival takes place in?",
                "Germany", "Holland", "Italy", 1,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q39, "en");

        Question q40 = new Question("What was Freddie Mercury‘s"+"\r\n"+" real name?",
                "Eddie van Halen", "Farrokh Bulsara", "Connor Mcgregor", 2,
                DIFFICULTY_MEDIUM, Category.MUSIC);
        addQuestion(q40,"en");                                                                    // Music MEDIUM END

        Question q41 = new Question("Paul McCartney credits which artist for"+"\r\n"+" teaching him everything he knows?",                                       // Music HARD START
                "Little Richard", "Jimi Hendrix", "Bach", 1,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q41, "en");

        Question q42 = new Question("Van Halen famously banned what color"+"\r\n"+" M&Ms in their rider?",
                "Yellow", "Red", "Brown", 3,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q42, "en");

        Question q43 = new Question("Which Marvel movie’s soundtrack"+"\r\n"+" won two Grammys?",
                "Captain America", "Black Panter", "Spider-Man", 2,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q43, "en");

        Question q44 = new Question("What artists recorded"+"\r\n"+" two of their bestselling albums"+"\r\n"+" while they were behind bars?",
                "John Travolta", "Elton John", "Johnny Cash", 3,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q44, "en");

        Question q45 = new Question("Before Miley Cyrus recorded"+"\r\n"+" 'Wrecking Ball,"+"\r\n"+" it was offered to which singer?",
                "Adele", "Britny Spears", "Beyonce", 3,
                DIFFICULTY_HARD, Category.MUSIC);
        addQuestion(q45,"en");                                                                              // Music HARD END

    }


}