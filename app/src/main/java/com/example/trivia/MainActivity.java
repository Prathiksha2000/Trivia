package com.example.trivia;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView questionTextView;
    private TextView questionCounterTextView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex =0;
    private List<Question> questionList;

    private  TextView highestScoreTextView;
    private  TextView scoreTextView;
    private  int scoreCounter = 0;
    private Score score;
    private Prefs prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score(); //instantiated score object

        prefs = new Prefs(MainActivity.this);

        //Log.d("Second","onClick:"+prefs.getHighScore());

        scoreTextView = findViewById(R.id.score_text);
        nextButton=findViewById(R.id.next_button);
        prevButton=findViewById(R.id.prev_button);
        trueButton=findViewById(R.id.true_button);
        falseButton=findViewById(R.id.false_button);
        questionCounterTextView=findViewById(R.id.counter_text);
        questionTextView=findViewById(R.id.question_textView);

        highestScoreTextView = findViewById(R.id.highest_score);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        //setting current score

        scoreTextView.setText(String.format("Current Score:%s", String.valueOf(score.getScore())));

        //get previous state
        currentQuestionIndex = prefs.getState();

        //setting highest score

        highestScoreTextView.setText(String.format("Highest Score:%s", String.valueOf(prefs.getHighScore())));



            questionList =
                    new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());

                questionCounterTextView.setText(currentQuestionIndex + "/"+ questionList.size());
                                                                       // or questionArrayList can be used.
                Log.d("Inside","processFinished"+questionArrayList);

            }
        });

        //Log.d("check","size:"+questionList.size());
       // Log.d("Main","OnCreate: "+questionList);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.prev_button:
                if(currentQuestionIndex > 0 ) {
                    currentQuestionIndex = (currentQuestionIndex + -1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id.next_button:
               // prefs.saveHighScore(scoreCounter);
               // Log.d("Prefs","onClick:"+prefs.getHighScore());
               goNext();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;

        }
    }

    //checks the answer and based on it call the appropriate methods

    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue= questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0 ;
        if(userChooseCorrect == answerIsTrue){
            fadeView();
            addPoints();
            toastMessageId = R.string.correct_answer;

        }else{
            shakeAnimation();
            deductPoints();
            toastMessageId = R.string.wrong_answer;
       }
        Toast.makeText(MainActivity.this,toastMessageId,
                Toast.LENGTH_SHORT)
                .show();
    }

     private  void addPoints(){
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreTextView.setText(String.format("Current Score:%s", String.valueOf(score.getScore())));

       // Log.d("Score:","addPoints:"+score.getScore());

     }
    private  void deductPoints() {

        scoreCounter -= 100;
        if (scoreCounter > 0){
            score.setScore(scoreCounter);
            scoreTextView.setText(String.format("Current Score:%s", String.valueOf(score.getScore())));
        }else{
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTextView.setText(String.format("Current Score:%s", String.valueOf(score.getScore())));
            //Log.d("ScoreBad:","deductPoints:"+score.getScore());
        }


    }




    private void updateQuestion() {
        String question =questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(currentQuestionIndex + "/"+ questionList.size());

    }

    //animation for correct answer

    private void fadeView(){
       final CardView cardView =findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation =new AlphaAnimation(1.0f,0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

}


     //animation for wrong answer

    private void shakeAnimation(){
        Animation  shake  = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView cardView=findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //to move to net question

    private  void goNext(){
        currentQuestionIndex =(currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }


    //when the activity goes to onPause state preferences related to score are saved

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}
