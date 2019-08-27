package com.example.firebasetest1.FormalAct;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.Room.DailyInfoDatabase;
import com.example.firebasetest1.Room.House;
import com.example.firebasetest1.Room.User;

public class QuestionActivity extends AppCompatActivity {
    DailyInfoDatabase db = null;
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

        Button btn_submit = findViewById(R.id.btn_submit);
        EditText edt_houseName = findViewById(R.id.edt_houseName);
        EditText edt_billCycle = findViewById(R.id.edt_billCycle);
        EditText edt_CPL= findViewById(R.id.edt_CPL);
        EditText edt_NoP= findViewById(R.id.edt_No_people);


        btn_submit.setOnClickListener(view ->{
            String houseName = edt_houseName.getText().toString();
            String billCycle = edt_billCycle.getText().toString();
            String cpl = edt_CPL.getText().toString();
            String nop = edt_NoP.getText().toString();
            String[] strings = {houseName,billCycle,cpl,nop};
            new  test().execute(strings);
        } );
    }

    private class test extends AsyncTask<String,Void,Integer>{

        @Override
        protected Integer doInBackground(String... strings) {
            db = Room.databaseBuilder(getApplicationContext(),
                    DailyInfoDatabase.class, "dailyInfo_database")
                    .fallbackToDestructiveMigration()
                    .build();
            String uuid = tools.id(getApplicationContext());
            int id = db.InfoDao().userExists(uuid);
            if (id == 0){
                User user = new User(uuid);
                id = (int) db.InfoDao().insertUser(user);
            }

            House house = new House(strings[0],Integer.parseInt(strings[1]),strings[2],Integer.parseInt(strings[3]),id);
            id = (int) db.InfoDao().insertHouse(house);

            return id;
        }
        @Override
        protected void onPostExecute(Integer result) {
            if (result!=0){
                Intent intent = new Intent(QuestionActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }else{
                tools.toast_long(getApplicationContext(),"Insert not succeed, please try again");
            }
        }
    }

}
