package com.example.pl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void startBtn(View view){
        Intent myIntent = new Intent(start.this, question.class);
        startActivity(myIntent);
    }
    public void howToBtn(View view) {
        Intent backIntent = new Intent(start.this, howTo.class);
        startActivity(backIntent);
    }
}