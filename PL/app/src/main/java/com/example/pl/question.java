package com.example.pl;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class question extends AppCompatActivity {
// All the Text edits and Bars that need to get got or to be edited
    EditText UserAnswerEditText;
    TextView qText;
    ProgressBar progressBar;
    TextView lvlText;
// JSON file to get from the server
    String myJSONFile = "";
// Numbers for the counting levels and percentages
    int counter = 0;
    int randNoL;
    int percentage = 0;
    int questionListSize;
// For calculating the random number inputs
    int min = 2; int max = 20;
    int x; int y; int z; int t; int c;
// Question and answer to be applied to the correct spaces on the app
    String question; int answer;
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
        getFile();

    }
// Function to see if they got the question right or not and also what do to depending on this
    public void submitBtn(View view) {
        String userAnswerStr = UserAnswerEditText.getText().toString();
        if (userAnswerStr.equals("")){return;}
        int userAnswer = Integer.parseInt(userAnswerStr);
        Boolean answerCorrect = answer == userAnswer;
        if (answerCorrect){
            //Plays Sound
            MediaPlayer correctSound = MediaPlayer.create(question.this,R.raw.correctsound);
            correctSound.start();
            // removes question from list
            questionList.remove(randNoL);
            if(questionList.isEmpty()){counter = counter + 5;   runQuestions(myJSONFile);}
            displayCorrectPopUp(view);
            //gets new question
            runFunctions();
            qText.setText(question);
            UserAnswerEditText.setText("");
        }else{
            MediaPlayer incorrectSounds = MediaPlayer.create(question.this,R.raw.incorrectsound);
            incorrectSounds.start();
            displayIncorrectPopUp(view);
            // gets new question
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
                Log.i("PERCENTAGE", String.valueOf(percentage));
                progressBar.setProgress(percentage);
                Log.i("PERCENTAGE", String.valueOf(percentage));
            }
    }
// Function to make the CorrectPopUp window show when they get the question right
    public void displayCorrectPopUp(View view){
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
// Function to get the list of questions and then to replace those questions
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
                Log.i("myReponse", myResponse);
                JsonObject answerJSON = Json.parse(myResponse).asObject();
                answer = answerJSON.asObject().getInt("answer", 0);
                Log.i("ANSWER", String.valueOf(answer));

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
            for (JsonValue value: Lvl2) {
                String q = value.asObject().getString("question", "Unknown Value");
                questionList.add(q);
                lvlText.setText("Level 2");
            }
            JsonValue qAdd = (Lvl1.asArray().get(rand.nextInt(Lvl1.size())));
            questionList.add(qAdd.asObject().getString("question", "Unknown Value"));
        }
        if (counter >=10 && counter < 15){
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

        Log.i("questions in list", questionList.toString());

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
        }
        else{
            Intent myIntent = new Intent(question.this, MainActivity.class);
            startActivity(myIntent);
        }
    }
}
