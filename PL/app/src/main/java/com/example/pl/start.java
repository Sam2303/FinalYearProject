package com.example.pl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class start extends AppCompatActivity {
    String dbUser;
    JsonObject dbUserJson;

    String counterDB;
    String highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Bundle bundle = getIntent().getExtras();
        dbUser = bundle.getString("dbUser");
        Log.i("DBUSER", dbUser);
        dbUserJson = Json.parse(dbUser).asObject();

    }

    public void startBtn(View view){
        getCounterAndScore();
    }
    public void howToBtn(View view) {
        Intent backIntent = new Intent(start.this, howTo.class);
        backIntent.putExtra("dbUser", dbUser);
        startActivity(backIntent);
    }
    public void leaderboardBtn(View view){
        Intent myIntent = new Intent(start.this, leaderboard.class);
        startActivity(myIntent);
    }

    public void getCounterAndScore(){
        String userName = dbUserJson.getString("userName", "Unknown Value");

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("userName", userName)
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/getCounterAndScore")
                .post(formBody)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.i("myResponse", myResponse);
                JsonObject counterAndScore = Json.parse(myResponse).asObject();
                counterDB = counterAndScore.getString("counter", "Unknown Value");
                highScore = counterAndScore.getString("highScore", "Unknown Value");

                Intent myIntent = new Intent(start.this, question.class);
                myIntent.putExtra("dbUser", dbUser);
                myIntent.putExtra("counter", counterDB);
                myIntent.putExtra("highScore", highScore);
                startActivity(myIntent);


            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("ERROR FROM POST","did not send");
                e.printStackTrace();
            }
        });
    }
}