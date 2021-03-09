package com.example.pl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.eclipsesource.json.JsonObject;

public class start extends AppCompatActivity {
    String dbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Bundle bundle = getIntent().getExtras();
        dbUser = bundle.getString("dbUser");
        Log.i("DBUSER", dbUser);
    }

    public void startBtn(View view){
        Intent myIntent = new Intent(start.this, question.class);
        myIntent.putExtra("dbUser", dbUser);
        startActivity(myIntent);
    }
    public void howToBtn(View view) {
        Intent backIntent = new Intent(start.this, howTo.class);
        backIntent.putExtra("dbUser", dbUser);
        startActivity(backIntent);
    }
}