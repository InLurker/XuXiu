package com.projekt.mirage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();

        //Pause for 3 seconds
        handler.postDelayed(() -> {
            Intent ChatroomIntent = new Intent(this, ChatroomActivity.class);
            startActivity(ChatroomIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            this.finish();
        }, 3000);

    }
}