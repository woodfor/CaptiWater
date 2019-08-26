package com.example.firebasetest1.FormalAct;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasetest1.R;

import java.util.UUID;


public class StartActivity extends AppCompatActivity {

    private static int TIME_OUT = 1000;
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        Context appContext = getApplicationContext();
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = appContext.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            //delete this if no need to test question
            uniqueID = null;
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(PREF_UNIQUE_ID, uniqueID);
            editor.apply();
            //
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {  // First using
                uniqueID = UUID.randomUUID().toString();
                //SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(StartActivity.this, QuestionActivity.class);
                    startActivity(intent);
                    finish();
                }, TIME_OUT);

            }
            else{
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }, TIME_OUT);
            }
        }


    }
}
