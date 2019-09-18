package com.example.firebasetest1.FormalAct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.Area;
import com.example.firebasetest1.RestClient.Model.Tap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.example.firebasetest1.General.tools.toast_long;

public class TapSettingActivity extends AppCompatActivity {

    private EditText edt_notification;
    private EditText edt_timer;
    private Switch switch_notification;
    private Switch switch_timer;
    private Tap tap;
    //bluetooth
    private boolean isBtConnected = false;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    private String btRespond;
    FirebaseDatabase database;
    DatabaseReference notifyRef;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_setting);
        TextView tv_title = findViewById(R.id.tv_title);

        Context mContext = this;
        edt_notification = findViewById(R.id.edt_notification);
        switch_notification = findViewById(R.id.switch_notification);


        try {
            tap =  tools.getTap(getApplicationContext());

            tv_title.setText(tap.getName());
        }catch (Exception e){
            e.printStackTrace();
            tools.toast_long(getApplicationContext(),"Can't get info of tap");
            finish();
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.child("notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(Integer.class);
                if (value==0){
                    switch_notification.setChecked(false);
                    edt_notification.setVisibility(View.GONE);
                }else {
                    switch_notification.setChecked(true);
                    edt_notification.setText(String.valueOf(value));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tools.toast_long(getApplicationContext(),"Fail to read value,please check your internet");
                TapSettingActivity.this.finish();
            }
        });

        myRef.child("timer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int value = dataSnapshot.getValue(Integer.class);
                if (value==0){
                    switch_timer.setChecked(false);
                    edt_timer.setVisibility(View.GONE);
                }else {
                    switch_timer.setChecked(true);
                    edt_timer.setText(String.valueOf(value));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tools.toast_long(getApplicationContext(),"Fail to read value,please check your internet");
                TapSettingActivity.this.finish();
            }
        });

        switch_notification.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                edt_notification.setVisibility(View.VISIBLE);
            } else {
                myRef.child("notification").setValue(0);
                edt_notification.setVisibility(View.GONE);
            }
        });
        edt_notification.setOnClickListener(view -> showNumberPickerNotification(mContext, 1, 60));

        edt_timer = findViewById(R.id.edt_timer);
        switch_timer = findViewById(R.id.switch_timer);
        switch_timer.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                edt_timer.setVisibility(View.VISIBLE);
            } else {
                myRef.child("timer").setValue(0);
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

        np.setMaxValue(max);
        np.setMinValue(min);
        btn_set.setOnClickListener(view -> {
            myRef.child("timer").setValue(np.getValue());
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

        np.setMaxValue(max);
        np.setMinValue(min);
        btn_set.setOnClickListener(view -> {
            myRef.child("notification").setValue(np.getValue());
            dialog.dismiss();
        });
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }




}
