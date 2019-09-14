package com.example.firebasetest1;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AreaSettingsFragment extends Fragment{
    View vAreaSettings;
    EditText edt_notification;
    EditText edt_timer;
    Switch switch_notification;
    Switch switch_timer;
    TextView area_settings_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vAreaSettings = inflater.inflate(R.layout.fragment_area_settings, container, false);

        area_settings_text = vAreaSettings.findViewById(R.id.area_settings_text);
        //area_settings_text.setText("");

        Context mContext = this.getContext();
        edt_notification = vAreaSettings.findViewById(R.id.edt_notification);
        edt_notification.setClickable(false);
        switch_notification = vAreaSettings.findViewById(R.id.switch_notification);
        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    edt_notification.setClickable(true);
                } else {
                    edt_notification.setClickable(false);
                }
            }
        });

        edt_notification.setOnClickListener(view -> showNumberPickerNotification(mContext, 1, 60));

        edt_timer = vAreaSettings.findViewById(R.id.edt_timer);
        edt_timer.setClickable(false);
        switch_timer = vAreaSettings.findViewById(R.id.switch_timer);
        switch_timer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    edt_timer.setClickable(true);
                } else {
                    edt_timer.setClickable(false);
                }
            }
        });
        edt_timer.setOnClickListener(view -> showNumberPickerTimer(mContext, 1, 60));

        return vAreaSettings;
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
