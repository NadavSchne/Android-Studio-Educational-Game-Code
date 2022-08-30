package com.example.quizgame;                                                                       // ***orientation *** : -> high score is save on SP so its automaticly being set
                                                                                                    // radio button selection - auto save
import androidx.annotation.NonNull;                                                                 //text view : using freezes text
import androidx.appcompat.app.AppCompatActivity;                                                    //

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String NEW_SCORE = "newScore";   // name for returning result value
    private static final long COUNTDOWN_IN_MILLIS = 30000;


    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCategory;
    private TextView textViewDifficulty;
    private TextView textViewCountDown;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;
    private int score;
    private boolean answered;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;            // creat countdown timer
    private long timeLeftInMillis;                    // for CD function - time left

    private long backPressedTime;

    private static final String KEY_SCORE = "keyScore";                                               // keys to save for when we rotate our device
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCategory = findViewById(R.id.text_view_category);
        textViewDifficulty = findViewById(R.id.text_view_difficulty);
        textViewCountDown = findViewById(R.id.text_view_countdown);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();

        Intent intent = getIntent();                                                                 // returns the intent that opened this activity which contains "difficulty" and Category as an EXTRA
        int categoryID = intent.getIntExtra(MainActivity.EXTRA_CATEGORY_ID, 0);          // set category ID and NAME to new instances
        String categoryName = intent.getStringExtra(MainActivity.EXTRA_CATEGORY_NAME);
        int difficulty = intent.getIntExtra(MainActivity.EXTRA_DIFFICULTY, 0);                    // save the chosen difficulty on a string
        username = intent.getStringExtra(MainActivity.EXTRA_USER_NAME);

        textViewCategory.setText(getText(R.string.Category)+ categoryName);                          // set the Quiz text view category to the chosen one on the spinner
        textViewDifficulty.setText( getString(R.string.Difficulty_text) + Question.getAllDifficultyLevels(this)[difficulty]);                          // set the textView by the chosen difficulty

        if (savedInstanceState == null) {                                                            // don't enter if SIS==null - so when screen rotate it won't change the current question
            QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);                                  // initiolziing a QuizDbHelper

            // todo: read from Locale
            String lang = getResources().getConfiguration().locale.getLanguage() == "en" ? "en" : "he";

            questionList = dbHelper.getQuestions(categoryID, difficulty, lang);                            // creating a questionList with category and difficulty level


            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);

            showNextQuestion();
        } else {                                                                                     // restore our previous state - orientation
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);            //restore questions list
            questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);                        //restore question counter
            currentQuestion = questionList.get(questionCounter - 1);                                 // -1 cause question counter is always 1 step ahead of question index
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);
            if (!answered) {
                startCountDown();                                                                    // if questoin wasnt answered -> resume the timer
            } else {
                updateCountDownText();                                                               // if answered -> restore color for timer and answers
                showSolution();
            }
        }

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {                                                                     // if answer= true -> go to else and start nextQuestion()
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {                     //if answer = false -> check if a RB was chosen -> if there is. check answer
                        checkAnswer();
                    } else {                                                                         // else toast the user to choose a question
                        Toast.makeText(QuizActivity.this, getString(R.string.Please_select_an_answer), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });


    }


    private void showNextQuestion() {                                                            // reset RB, showing next question,
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();
        if (questionCounter < questionCountTotal) {                                             // check if we have passed 5/5 question
            currentQuestion = questionList.get(questionCounter);                                 // get new question from Question list
            textViewQuestion.setText(currentQuestion.getQuestion());                              // set TEXT VIEW to next question string
            rb1.setText(currentQuestion.getOption1());                                            // set radio button option to option 1 from Question object
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questionCounter++; // must be before next line - so counter starts at 1 and not 0
            textViewQuestionCount.setText(getText(R.string.Question_only) + "" + questionCounter + "/" + questionCountTotal);
            answered = false; // lock the answer instead of moving to the next question - Question hasnt been answered yet
            buttonConfirmNext.setText(getText(R.string.Confirm_Button)); // set the button text when answer was selected to "confirm"

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();

        } else {
            finishQuiz();                                                                            // if question count > question - finish quiz
        }
    }



    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {               // receives time value and time for each interval (1 sec )
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;                                             //
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                timeLeftInMillis = 0;                                                                // so the timer will show 0 when finished
                updateCountDownText();
                checkAnswer();                                                                       // incase we chose an answer but didn't confirm until time finished - we lock the answer and "auto confirm it"
            }
        }.start();                                                                                   //immediately starts timer after we create it
    }
    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;                                          //in case we want to use minutes and not only secconds
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds); // timer format
        textViewCountDown.setText(timeFormatted);                                                   // set text of timer ( every 1 sec interval )
        if (timeLeftInMillis < 10000) {
            textViewCountDown.setTextColor(Color.RED);                                              // color red when 10 seconds or less
        } else {
            textViewCountDown.setTextColor(textColorDefaultCd);                                     // default color when - above 10 seconds
        }
    }



    private void finishQuiz() {
        Intent resultIntent = new Intent();      // the intent that return resualt
        resultIntent.putExtra(NEW_SCORE, score);   // name of returning value + value
        setResult(RESULT_OK, resultIntent);        // " result succeed"
        finish();
    }

    private void checkAnswer() {                                                                    // in case "confirm button" was pressed and there is a Radio button pressed "with an answer"

        answered = true;                                                                            // set that a radio button was pressed so next time "confirm button" is pressed -> next question will aply
        countDownTimer.cancel();                                                                    // cancel timer after we picked an answer


        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());                   // create new RB and store the ID of the selected RB of the group
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;                                       // create an integer and compare with the selected RB ----- +1 bcz index of radio button (group)  starts from 0
        if (answerNr == currentQuestion.getAnswerNr()) {                                           // compare and check if answer is corrent
            score++;
            textViewScore.setText(getText(R.string.Score_runtime) + "" + score);
        }
        showSolution();                                                                             // after "confirm button" reached its end (an answer was chosen and confirm) we want to show the corrent answer
    }

    private void showSolution() {
        rb1.setTextColor(Color.RED);    // set all RB to red
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        switch (currentQuestion.getAnswerNr()) {                                                     // switch case by the corrent answer + set the TEXT VIEW QUIESTION to which answer is correct
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText(getText(R.string.answer1));
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText(getText(R.string.answer2));
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText(getText(R.string.answer3));
                break;
        }
        if (questionCounter < questionCountTotal) {                                                  // check if all question were answered - determem what "confirm button" shall do (next or finish)
            buttonConfirmNext.setText(getText(R.string.Next));
        } else {
            buttonConfirmNext.setText(getText(R.string.Finish));
        }
    }

    @Override
    public void onBackPressed() {                                                                       // *system.currentTimeMillis = Returns the current time in milliseconds
        if (backPressedTime + 2000 > System.currentTimeMillis()) {                                      // so we give extra 2 seconds to press the back button again
            finishQuiz();
        } else {
            Toast.makeText(this, getText(R.string.PressBack), Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {                                                                // not to get crash if timer wasnt created yet
            countDownTimer.cancel();                                                                 // cancel timer so it won't run in the backround
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {                                  //saving the current data into the bundle so we get restore data from it in case or rotation
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, answered);



        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}