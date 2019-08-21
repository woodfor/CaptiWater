package com.example.firebasetest1;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

import static com.example.firebasetest1.MainActivity.id;

public class BlueToothActivity extends AppCompatActivity {

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    String address = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);
        String name = newint.getStringExtra("name");
        TextView textView = findViewById(R.id.textViewBluetooth);
        Button btn_connect = findViewById(R.id.btn_connectWifi);
        Button btn_name = findViewById(R.id.btn_nameTap);
        Context context = BlueToothActivity.this;
        textView.setText(name);

        new ConnectBT().execute();
        btn_connect.setOnClickListener(view -> {
            sendSignal("S");
        });
        btn_name.setOnClickListener(view -> {
            final EditText edt_quantity = new EditText(context);
            final AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Edit tap name")
                    .setMessage("Enter the name below")
                    .setView(edt_quantity)
                    .setPositiveButton("Ok",null)
                    .setNegativeButton("No",null)
                    .show();
            Button btn_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn_positive.setOnClickListener(v -> {
                String tmp = edt_quantity.getText().toString().trim();
                if (!(tmp == null || tmp.isEmpty()))
                {
                   sendSignal("N:" + tmp);
                    msg("Name been set");
                    alertDialog.dismiss();
                }
                else
                    edt_quantity.setError("please input a number");

            });
        });
    }
    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                msg("Error");
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;
      // private ProgressDialog progress;
        @Override
        protected  void onPreExecute () {
           // progress = ProgressDialog.show(getApplicationContext() , "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    String uuid = id(getApplicationContext());
                    if (getIntent().getExtras() != null) {
                        for (String key : getIntent().getExtras().keySet()) {
                            Object value = getIntent().getExtras().get(key);
                        }
                    }
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address.trim());
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    btSocket.close();
                } catch (IOException closeException) {
                    //Log.e(TAG, "Could not close the client socket", closeException);
                }

                ConnectSuccess = false;
                return null;
            }

            return null;
        }


        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }

            //progress.dismiss();
        }
    }

protected void onPause(){
        super.onPause();
        try {
            btSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

}

}
