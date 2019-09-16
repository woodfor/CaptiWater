package com.example.firebasetest1.FormalAct;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.example.firebasetest1.R;

public class TapSettingActivity extends AppCompatActivity {

    View vAreaSettings;
    EditText edt_notification;
    EditText edt_timer;
    Switch switch_notification;
    Switch switch_timer;
    TextView area_settings_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_setting);
        area_settings_text = findViewById(R.id.tv_title);
        Context mContext = this;
        edt_notification = findViewById(R.id.edt_notification);
        switch_notification = findViewById(R.id.switch_notification);

        switch_notification.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b == true) {
                edt_notification.setVisibility(View.VISIBLE);
            } else {
                edt_notification.setVisibility(View.GONE);
            }
        });
        edt_notification.setOnClickListener(view -> showNumberPickerNotification(mContext, 1, 60));

        edt_timer = findViewById(R.id.edt_timer);
        switch_timer = vAreaSettings.findViewById(R.id.switch_timer);
        switch_timer.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b == true) {
                edt_timer.setVisibility(View.VISIBLE);
            } else {
                edt_timer.setVisibility(View.GONE);
            }
        });
        edt_timer.setOnClickListener(view -> showNumberPickerTimer(mContext, 1, 60));

    }
    private void showNumberPickerTimer(Context context, int min, int max) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Set Timer for:");
        dialog.setContentView(R.layout.dialog_number_picker);
        Button btn_set = dialog.findViewById(R.id.btn_set);
        btn_set.setVisibility(View.VISIBLE);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView text_min = dialog.findViewById(R.id.text_min);
        text_min.setVisibility(View.VISIBLE);
        final NumberPicker np = dialog.findViewById(R.id.numberPicker1);

        np.setMaxValue(60);
        np.setMinValue(1);
        btn_set.setOnClickListener(view -> {
            edt_timer.setText(String.valueOf(np.getValue()));
            dialog.dismiss();
        });
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showNumberPickerNotification(Context context, int min, int max) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Notify me after: ");
        dialog.setContentView(R.layout.dialog_number_picker);
        Button btn_set = dialog.findViewById(R.id.btn_set);
        btn_set.setVisibility(View.VISIBLE);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView text_min = dialog.findViewById(R.id.text_min);
        text_min.setVisibility(View.VISIBLE);
        final NumberPicker np = dialog.findViewById(R.id.numberPicker1);

        np.setMaxValue(60);
        np.setMinValue(1);
        btn_set.setOnClickListener(view -> {
            edt_notification.setText(String.valueOf(np.getValue()));
            dialog.dismiss();
        });
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }
}
