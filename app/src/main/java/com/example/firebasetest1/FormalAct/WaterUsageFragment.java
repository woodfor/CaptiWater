package com.example.firebasetest1.FormalAct;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firebasetest1.R;


public class WaterUsageFragment extends Fragment implements View.OnClickListener{
    View vWaterUsage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        vWaterUsage = inflater.inflate(R.layout.fragment_water_usage, container, false);

        ImageView leftBtn = (ImageView) vWaterUsage.findViewById(R.id.left_btn);
        ImageView rightBtn = (ImageView) vWaterUsage.findViewById(R.id.right_btn);

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
}