package com.example.pl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText userName;
    EditText password;

    String confirmedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);

    }

    public void login(View view){

        String userNameText = userName.getText().toString();
        String passwordText = password.getText().toString();

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("userName", userNameText)
                .add("password", passwordText)
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/getLogin")
                .post(formBody)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.i("myResponse", myResponse);
                JsonObject loginRes = Json.parse(myResponse).asObject();
                String loginTF = loginRes.asObject().getString("success", "no");
                if(loginTF.equals("false")){
                    Log.i("login success?", "NOT A VLID LOGIN");
                }else {

                    JsonObject loginResTrue = Json.parse(myResponse).asObject();
                    confirmedUser = loginResTrue.asObject().getString("userName", "user");;
                    // define username/ counter/ score in question class


                    Intent startActivity = new Intent(MainActivity.this, start.class);
                    startActivity.putExtra("dbUser", loginResTrue.toString());
                    startActivity(startActivity);

                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("ERROR FROM POST","did not send");
                e.printStackTrace();
            }
        });



    }


}