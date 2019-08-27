package com.example.firebasetest1.FormalAct;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.firebasetest1.R;
import com.example.firebasetest1.Room.DailyInfoDatabase;
import com.example.firebasetest1.Room.House;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class WaterUsageFragment extends Fragment implements View.OnClickListener{
    View vWaterUsage;
    House house;
    DailyInfoDatabase db = null;
    TextView tv_totalLiter;
    TextView tv_cost;
    TextView tv_estCost;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        vWaterUsage = inflater.inflate(R.layout.fragment_water_usage, container, false);

        ImageView leftBtn = (ImageView) vWaterUsage.findViewById(R.id.left_btn);
        ImageView rightBtn = (ImageView) vWaterUsage.findViewById(R.id.right_btn);
        TextView tv_houseName = vWaterUsage.findViewById(R.id.tv_houseName);
        tv_totalLiter = vWaterUsage.findViewById(R.id.liter_text);
        tv_cost = vWaterUsage.findViewById(R.id.cost_text);
        tv_estCost = vWaterUsage.findViewById(R.id.estimated_cost_text);
        SharedPreferences mPrefs = getActivity().getApplicationContext().getSharedPreferences("House",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("SelectedHouse","");
        house = gson.fromJson(json,House.class);
        String houseName = house.getName();
        tv_houseName.setText(houseName);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        return vWaterUsage;
    }

    @Override
    public void onClick(View v) {
        TextView timeText = (TextView) vWaterUsage.findViewById(R.id.time_text);
        String keyword = timeText.getText().toString();

        switch (v.getId()) {
            case R.id.left_btn:
                if (keyword.equals("Daily")) {
                    timeText.setText("Yearly");
                } else if (keyword.equals("Monthly")) {
                    timeText.setText("Daily");
                } else if (keyword.equals("Yearly")) {
                    timeText.setText("Monthly");
                }
                break;
            case R.id.right_btn:
                if (keyword.equals("Daily")) {
                    timeText.setText("Monthly");
                } else if (keyword.equals("Monthly")) {
                    timeText.setText("Yearly");
                } else if (keyword.equals("Yearly")) {
                    timeText.setText("Daily");
                }
                break;
        }
    }
    protected class fillData extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            db = Room.databaseBuilder(getActivity().getApplicationContext(),
                    DailyInfoDatabase.class, "dailyInfo_database")
                    .fallbackToDestructiveMigration()
                    .build();
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd");
            String formattedDate = df.format(today);
            int usage = db.InfoDao().getSumUsage(house.getId(),Integer.parseInt(formattedDate));
            return null;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        //db.close();
    }

}