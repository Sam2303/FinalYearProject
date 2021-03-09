package com.example.pl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class howTo extends AppCompatActivity {
    String dbUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);
        Bundle bundle = getIntent().getExtras();
        dbUser = bundle.getString("dbUser");
        Log.i("DBUSER", dbUser);
    }

    public void backBtn(View view) {
        Intent myIntent = new Intent(howTo.this, MainActivity.class);
        startActivity(myIntent);
    }

    public void startBtn(View view) {
        Intent myIntent = new Intent(howTo.this, question.class);
        myIntent.putExtra("dbUser", dbUser);
        startActivity(myIntent);
    }
}