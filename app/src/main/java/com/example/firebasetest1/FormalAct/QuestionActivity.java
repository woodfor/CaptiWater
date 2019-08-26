package com.example.firebasetest1.FormalAct;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasetest1.R;

public class QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        //SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        //String firstTime = sharedPreferences.getString("FirstTime", "");

        //if(firstTime.equals("Yes")){
        //    Intent intent = new Intent(QuestionActivity.this, MainActivity.class);
        //    startActivity(intent);
        //} else {
        //    SharedPreferences.Editor editor = sharedPreferences.edit();
        //    editor.putString("FirstTime", "Yes");
        //    editor.apply();
        //} uncomment after finish

        ImageView addButton = (ImageView) findViewById(R.id.add_tap_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add code here
            }
        });
    }
}
