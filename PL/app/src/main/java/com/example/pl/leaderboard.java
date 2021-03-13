package com.example.pl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class leaderboard extends AppCompatActivity {
    TextView text0;
    TextView text1;
    TextView text2;
    TextView text3;
    TextView text4;
    TextView text5;
    TextView text6;
    TextView text7;
    TextView text8;
    TextView text9;

    int index = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        text0 = (TextView) findViewById(R.id.textView0);
        text1 = (TextView) findViewById(R.id.textView1);
        text2 = (TextView) findViewById(R.id.textView2);
        text3 = (TextView) findViewById(R.id.textView3);
        text4 = (TextView) findViewById(R.id.textView4);
        text5 = (TextView) findViewById(R.id.textView5);
        text6 = (TextView) findViewById(R.id.textView6);
        text7 = (TextView) findViewById(R.id.textView7);
        text8 = (TextView) findViewById(R.id.textView8);
        text9 = (TextView) findViewById(R.id.textView9);

        sendHighScoreToDb();
    }

    public void sendHighScoreToDb(){
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("userName", "userName")
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8080/orderedLeaderboard")
                    .post(formBody)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String myResponse = response.body().string();
                    Log.i("RESPONSE AFTER LEVEL", myResponse);
                    makeBoard(myResponse);
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("ERROR FROM POST","did not send");
                    e.printStackTrace();
                }
            });
        }

    public void makeBoard(String myResponse){


        JsonObject obj = Json.parse(myResponse).asObject();
        JsonArray ary = obj.get("leaderboard").asArray();
        Log.i("obj", String.valueOf(ary));

        for (JsonValue value : ary){
            String userName = value.asObject().getString("userName", "unkown value");
            int highScore = value.asObject().getInt("highScore", 0);


            Log.i("Username", userName);
            Log.i("highScore", String.valueOf(highScore));
            int uNLength = userName.length();

            String dataAdd = (index + 1) +".    " + userName+":     "+ highScore;
            setLeaderboard(index, dataAdd);
            index++;

        }
    }

    private void setLeaderboard(int index, String dataAdd) {
        if(index == 0){
            text0.setText(dataAdd);
        }
        else if(index == 1){
            text1.setText(dataAdd);
        }
        else if(index == 2){
            text2.setText(dataAdd);
        }
        else if(index == 3){
            text3.setText(dataAdd);
        }
        else if(index == 4){
            text4.setText(dataAdd);
        }
        else if(index == 5){
            text5.setText(dataAdd);
        }
        else if(index == 6){
            text6.setText(dataAdd);
        }
        else if(index == 7){
            text7.setText(dataAdd);
        }
        else if(index == 8){
            text8.setText(dataAdd);
        }
        else if(index == 9){
            text9.setText(dataAdd);
        }
    }


}
