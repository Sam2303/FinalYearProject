package com.example.pl;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class question extends AppCompatActivity {
// Username/ counter/ score
    String dbUser;
    JsonObject dbUserJson;
    TextView userNameText;
    TextView highScoreText;
    TextView currentScoreText;
    String userName;
    int score;
    int highScore;
    int seconds;

// All the Text edits and Bars that need to get got or to be edited
    EditText UserAnswerEditText;
    TextView qText;
    ProgressBar progressBar;
    TextView lvlText;
// To make the timer work
    TextView countDownText;
    long timeLeft = 60000;
    CountDownTimer countDownTimer;
    String timeLeftText;
// JSON file to get from the server
    String myJSONFile = "";
// Numbers for the counting levels and percentages
    int counter;
    int randNoL;
    int percentage = 0;
    int questionListSize;
// For calculating the random number inputs
    int min = 2; int max = 20;
    int x; int y; int z; int t; int c;
// Question and answer to be applied to the correct spaces on the app
    String question; String answerStr;
// For randomising
    Random rand = new Random();
// List for the questions to be added to.
    ArrayList<String> questionList = new ArrayList<>();
// Function to decide what is declared and ran when the page loads.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        UserAnswerEditText = (EditText) findViewById(R.id.Q_Input);
        qText = (TextView) findViewById(R.id.qText);
        lvlText = (TextView) findViewById(R.id.lvlText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        countDownText = (TextView) findViewById(R.id.countDownTimer);

        // Setting values for the top part, from database, username and scoring.
        Bundle bundle = getIntent().getExtras();
        dbUser = bundle.getString("dbUser");
        Log.i("DBUSER", dbUser);
        // Getting the Boxes in the xml to set:
        dbUserJson = Json.parse(dbUser).asObject();
        userNameText = (TextView) findViewById(R.id.UserNameText);
        highScoreText = (TextView) findViewById(R.id.highScore);
        currentScoreText = (TextView) findViewById(R.id.currentScore);
        Log.i("DBUSER", String.valueOf(dbUser));
        // Setting the text boxes
        userNameText.setText(dbUserJson.asObject().getString("userName", "user"));
        userName = dbUserJson.asObject().getString("userName", "user");
        //Setting the counter for specific user
        counter = Integer.parseInt(bundle.getString("counter"));
        // Setting the Users highScore so its visible
        highScoreText.setText("High Score: " + bundle.getString("highScore"));
        highScore = Integer.parseInt(bundle.getString("highScore"));

        getFile();

    }
// Function to see if they got the question right or not and also what do to depending on this
    public void submitBtn(View view) {

        String userAnswerStr = UserAnswerEditText.getText().toString();
        Boolean answerCorrectStr = answerStr.equalsIgnoreCase(userAnswerStr);
        Log.i("ANSWER CORRECT", String.valueOf(answerCorrectStr));

        if (answerCorrectStr){
            scoring(true);
            // removes question from list
            questionList.remove(randNoL);
            if(questionList.isEmpty()){counter = counter + 5;   runQuestions(myJSONFile);}
            displayCorrectPopUp(view);
            //gets new question
            countDownTimer.cancel();
            runFunctions();
            qText.setText(question);
            UserAnswerEditText.setText("");
        }else{
            scoring(false);
            displayIncorrectPopUp(view);
            // gets new question
            countDownTimer.cancel();
            runFunctions();
            qText.setText(question);
            UserAnswerEditText.setText("");
        }
    }
// Function to Calculate the percentage they are through the level
    private void levelCounter() {

            int questionListSizeAfterRemoval = questionList.size();
            if(questionListSizeAfterRemoval == 0){return;}
            else {
                percentage = (100 / questionListSizeAfterRemoval);
//                Log.i("PERCENTAGE", String.valueOf(percentage));
                progressBar.setProgress(percentage);
//                Log.i("PERCENTAGE", String.valueOf(percentage));
            }
    }
// Function to make the CorrectPopUp window show when they get the question right
    public void displayCorrectPopUp(View view){
        //Plays Sound
        MediaPlayer correctSound = MediaPlayer.create(question.this,R.raw.correctsound);
        correctSound.start();
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.correctanswer_popup, null);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
// Function to make IncorrectPopUp window show when they get the question wrong
    public void displayIncorrectPopUp(View view){
        MediaPlayer incorrectSounds = MediaPlayer.create(question.this,R.raw.incorrectsound);
        incorrectSounds.start();
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.incorectlayout_popup, null);
        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
// Function to make the timerRunOut_PopUp show when the user runs out of time on a question
    public void getFile(){
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/test";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client
                .newCall(request)
                .enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i("SERVER", "ERROR");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    myJSONFile = response.body().string();
                    question.this.runOnUiThread(new Runnable() {
                        @Override
                       public void run() {
                            runQuestions(myJSONFile);
                            runFunctions();

                        }
                    });
                }
           }
        });
    }
// Function to send the question to the web server to be run in the python file
    public void sendQuestion() {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("question", question)
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/sendQuestion")
                .post(formBody)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.i("myResponse", myResponse);
                JsonObject answerJSON = Json.parse(myResponse).asObject();
                answerStr = answerJSON.asObject().getString("answerStr", "Unknown Value");
                Log.i("ANSWER", answerStr);
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("ERROR FROM POST","did not send");
                e.printStackTrace();
            }
        });


    }
// put the questions in the array.
    public void runQuestions(String data) {
// Getting the Levels from the JSON passed in as data
        JsonObject obj = Json.parse(data).asObject();
        JsonArray Lvl1 = obj.get("Lvl1").asArray();
        JsonArray Lvl2 = obj.get("Lvl2").asArray();
        JsonArray Lvl3 = obj.get("Lvl3").asArray();
        JsonArray Lvl4 = obj.get("Lvl4").asArray();
        JsonArray Lvl5 = obj.get("Lvl5").asArray();
        JsonArray Lvl6 = obj.get("Lvl6").asArray();
        JsonArray Lvl7 = obj.get("Lvl7").asArray();
        JsonArray Lvl8 = obj.get("Lvl8").asArray();
        JsonArray Lvl9 = obj.get("Lvl9").asArray();
        JsonArray Lvl10 = obj.get("Lvl10").asArray();

// Each Level has a question from each level before it
        if (counter < 5){
            for (JsonValue value : Lvl1){
                String q = value.asObject().getString("question", "Unknown Item");
                questionList.add(q);
                lvlText.setText("Level 1");
            }
        }
        if(counter < 10 && counter >= 5){
            sendDataDB();
            for (JsonValue value: Lvl2) {
                String q = value.asObject().getString("question", "Unknown Value");
                questionList.add(q);
                lvlText.setText("Level 2");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
        }
        if (counter >=10 && counter < 15){
            sendDataDB();
            for(JsonValue value : Lvl3){
                String q = value.asObject().getString("question", "Unknown Value");
                questionList.add(q);
                lvlText.setText("Level 3");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd2 = (Lvl2.asArray().get(rand.nextInt(Lvl2.size())));
            questionList.add(qAdd2.asObject().getString("question", "Unknown Value"));
        }
        if (counter >=15 && counter < 20){
            sendDataDB();
            for(JsonValue value : Lvl4){
                String q = value.asObject().getString("question", "Unknown Value");
                questionList.add(q);
                lvlText.setText("Level 4");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd2 = (Lvl2.asArray().get(rand.nextInt(Lvl2.size())));
            questionList.add(qAdd2.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd3 = (Lvl3.asArray().get(rand.nextInt(Lvl3.size())));
            questionList.add(qAdd3.asObject().getString("question", "Unknown Value"));
        }
        if (counter >=20 && counter < 25){
            sendDataDB();
            for(JsonValue value : Lvl5){
                String q = value.asObject().getString("question", "Unknown Value");
                questionList.add(q);
                lvlText.setText("Level 5");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd2 = (Lvl2.asArray().get(rand.nextInt(Lvl2.size())));
            questionList.add(qAdd2.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd3 = (Lvl3.asArray().get(rand.nextInt(Lvl3.size())));
            questionList.add(qAdd3.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd4 = (Lvl4.asArray().get(rand.nextInt(Lvl4.size())));
            questionList.add(qAdd4.asObject().getString("question", "Unknown Value"));
        }
        if (counter >=25 && counter < 30){
            sendDataDB();
            for (JsonValue value : Lvl6){
                String q = value.asObject().getString("question", "Unknown Item");
                questionList.add(q);
                lvlText.setText("Level 6");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd2 = (Lvl2.asArray().get(rand.nextInt(Lvl2.size())));
            questionList.add(qAdd2.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd3 = (Lvl3.asArray().get(rand.nextInt(Lvl3.size())));
            questionList.add(qAdd3.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd4 = (Lvl4.asArray().get(rand.nextInt(Lvl4.size())));
            questionList.add(qAdd4.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd5 = (Lvl5.asArray().get(rand.nextInt(Lvl5.size())));
            questionList.add(qAdd5.asObject().getString("question", "Unknown Value"));
        }
        if(counter >=30 && counter < 35){
            sendDataDB();
            for (JsonValue value: Lvl7) {
                String q = value.asObject().getString("question", "Unknown Value");
                questionList.add(q);
                lvlText.setText("Level 7");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd2 = (Lvl2.asArray().get(rand.nextInt(Lvl2.size())));
            questionList.add(qAdd2.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd3 = (Lvl3.asArray().get(rand.nextInt(Lvl3.size())));
            questionList.add(qAdd3.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd4 = (Lvl4.asArray().get(rand.nextInt(Lvl4.size())));
            questionList.add(qAdd4.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd5 = (Lvl5.asArray().get(rand.nextInt(Lvl5.size())));
            questionList.add(qAdd5.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd6 = (Lvl6.asArray().get(rand.nextInt(Lvl6.size())));
            questionList.add(qAdd6.asObject().getString("question", "Unknown Value"));
        }
        if (counter >=35 && counter < 40){
            sendDataDB();
            for(JsonValue value : Lvl8){
                String q = value.asObject().getString("question", "Unknown Value");
                questionList.add(q);
                lvlText.setText("Level 8");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd2 = (Lvl2.asArray().get(rand.nextInt(Lvl2.size())));
            questionList.add(qAdd2.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd3 = (Lvl3.asArray().get(rand.nextInt(Lvl3.size())));
            questionList.add(qAdd3.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd4 = (Lvl4.asArray().get(rand.nextInt(Lvl4.size())));
            questionList.add(qAdd4.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd5 = (Lvl5.asArray().get(rand.nextInt(Lvl5.size())));
            questionList.add(qAdd5.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd6 = (Lvl6.asArray().get(rand.nextInt(Lvl6.size())));
            questionList.add(qAdd6.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd7 = (Lvl7.asArray().get(rand.nextInt(Lvl7.size())));
            questionList.add(qAdd7.asObject().getString("question", "Unknown Value"));
        }
        if (counter >=40 && counter < 45){
            sendDataDB();
            for(JsonValue value : Lvl9){
                String q = value.asObject().getString("question", "Unknown Value");
                questionList.add(q);
                lvlText.setText("Level 9");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd2 = (Lvl2.asArray().get(rand.nextInt(Lvl2.size())));
            questionList.add(qAdd2.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd3 = (Lvl3.asArray().get(rand.nextInt(Lvl3.size())));
            questionList.add(qAdd3.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd4 = (Lvl4.asArray().get(rand.nextInt(Lvl4.size())));
            questionList.add(qAdd4.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd5 = (Lvl5.asArray().get(rand.nextInt(Lvl5.size())));
            questionList.add(qAdd5.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd6 = (Lvl6.asArray().get(rand.nextInt(Lvl6.size())));
            questionList.add(qAdd6.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd7 = (Lvl7.asArray().get(rand.nextInt(Lvl7.size())));
            questionList.add(qAdd7.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd8 = (Lvl8.asArray().get(rand.nextInt(Lvl8.size())));
            questionList.add(qAdd8.asObject().getString("question", "Unknown Value"));
        }
        if (counter >=45 && counter < 50){
            sendDataDB();
            for(JsonValue value : Lvl10){
                String q = value.asObject().getString("question", "Unknown Value");
                questionList.add(q);
                lvlText.setText("Level 10");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd2 = (Lvl2.asArray().get(rand.nextInt(Lvl2.size())));
            questionList.add(qAdd2.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd3 = (Lvl3.asArray().get(rand.nextInt(Lvl3.size())));
            questionList.add(qAdd3.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd4 = (Lvl4.asArray().get(rand.nextInt(Lvl4.size())));
            questionList.add(qAdd4.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd5 = (Lvl5.asArray().get(rand.nextInt(Lvl5.size())));
            questionList.add(qAdd5.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd6 = (Lvl6.asArray().get(rand.nextInt(Lvl6.size())));
            questionList.add(qAdd6.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd7 = (Lvl7.asArray().get(rand.nextInt(Lvl7.size())));
            questionList.add(qAdd7.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd8 = (Lvl8.asArray().get(rand.nextInt(Lvl8.size())));
            questionList.add(qAdd8.asObject().getString("question", "Unknown Value"));
            JsonValue qAdd9 = (Lvl9.asArray().get(rand.nextInt(Lvl9.size())));
            questionList.add(qAdd9.asObject().getString("question", "Unknown Value"));
        }
        questionListSize = questionList.size();
    }
// Function randomise inputs
    public void randomQuestion(){
        x = rand.nextInt(10-1)+1;
        y = rand.nextInt(20-10)+10;
        z = rand.nextInt(max-min)+min;
        t = rand.nextInt(max-min)+min;
        int low = 0; int high = 3;
        c = rand.nextInt(high-low)+low;

        List<String> words = Collections.unmodifiableList(
                Arrays.asList("word", "portsmouth", "ability", "action", "university", "against", "yummy", "bacon", "sausage", "girl", "boy", "gender", "users", "numbers", "removing"));

        randNoL = rand.nextInt(questionList.size());

//        Log.i("questions in list", questionList.toString());

        String qL = questionList.get(randNoL);

        qL = qL.replace("~", words.get(rand.nextInt(words.size())));

        String q1 = qL.replace("£", String.valueOf(x));
        String q2 = q1.replace("$", String.valueOf(y));
        String q3 = q2.replace("&", String.valueOf(z));
        String q4 = q3.replace("!", String.valueOf(t));
        question = q4.replace("?", String.valueOf(c));

    }
// Function to run functions in order
    public void runFunctions() {
        levelCounter();
        if (questionList.size() != 0) {
            randomQuestion();
            sendQuestion();
            qText.setText(question);
            startTimer();
        }
        else{
            Intent myIntent = new Intent(question.this, MainActivity.class);
            startActivity(myIntent);
        }
    }
// Functions to make the counter count down
    public void startTimer(){
        timeLeft = 60000;
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long l) {
                timeLeft = l;
                updateTimer();
            }
            @Override
            public void onFinish() {}
        }.start();
    }
    public void updateTimer() {
        int minute = (int) timeLeft / 60000;
        seconds = (int) timeLeft  % 60000 / 1000;
        timeLeftText = "" + minute;
        timeLeftText += ":";
        if(seconds < 10){timeLeftText += "0";}
        timeLeftText += seconds;
        countDownText.setText(timeLeftText);

        if (timeLeftText.equals("0:00")){
            countDownTimer.cancel();
            runFunctions();
        }
    }
// Function to update the database after the user has completed a level
    public void sendDataDB(){
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("userName", dbUserJson.asObject().getString("userName", "user"))
                .add("counter", String.valueOf(counter))
                .add("highScore", String.valueOf(score))
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/sendCounterAndScore")
                .post(formBody)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.i("myResponse", myResponse);
                JsonObject answerJSON = Json.parse(myResponse).asObject();
                Log.i("RESPONSE AFTER LEVEL", String.valueOf(answerJSON));

            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("ERROR FROM POST","did not send");
                e.printStackTrace();
            }
        });
    }
// Function to calculate the scoring system
    public void scoring(Boolean correct){
        if(correct){
            score = score + seconds;
            currentScoreText.setText(String.valueOf(score));
        }
        else{
            int scoreMinus = 60 - seconds;
            score = score - scoreMinus;
            currentScoreText.setText(String.valueOf(score));
        }
        sendHighScoreToDb();
    }
// check current score is higher than high score, if so update database.
    public void sendHighScoreToDb(){
        if(score > highScore){
            Log.i("score upadte", "updating post request has started" + userName + String.valueOf(score));
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("userName", userName)
                    .add("highScore", String.valueOf(score))
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/updateHighScore")
                    .post(formBody)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String myResponse = response.body().string();
                    Log.i("myResponse", myResponse);
                    JsonObject answerJSON = Json.parse(myResponse).asObject();
                    Log.i("RESPONSE AFTER LEVEL", String.valueOf(answerJSON));

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("ERROR FROM POST","did not send");
                    e.printStackTrace();
                }
            });
        }else{Log.i("SCORING", "SCORE WASNT HIGHER THAN HIGH SCORE");}
    }
}
