package com.example.pl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startBtn(View view){
        Intent myIntent = new Intent(MainActivity.this, question.class);
        startActivity(myIntent);
    }
    public void howToBtn(View view) {
        Intent backIntent = new Intent(MainActivity.this, howTo.class);
        startActivity(backIntent);
    }
}