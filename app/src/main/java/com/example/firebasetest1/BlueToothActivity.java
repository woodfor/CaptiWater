package com.example.firebasetest1;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlueToothActivity extends AppCompatActivity {
    private static final String TAG = "blueTooth";
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    String address = null;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter = 1;
    String respond = "";
    volatile boolean stopWorker;
    private ProgressBar progressBar;
    ArrayList<String> wifiList;
    WifiManager wifiManager;
    BroadcastReceiver wifiScanReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        Intent newint = getIntent();
        address = newint.getStringExtra(HomeActivity.EXTRA_ADDRESS);
        String name = newint.getStringExtra("name");
        TextView textView = findViewById(R.id.textViewBluetooth);
        Button btn_connect = findViewById(R.id.btn_connectWifi);
        Button btn_name = findViewById(R.id.btn_nameTap);
        progressBar = findViewById(R.id.progressBar);
        Context context = BlueToothActivity.this;
        textView.setText(name);
        //connect blueTooth
        new ConnectBT().execute();
        //connect wifi
        wifiManager = (WifiManager)
                context.getSystemService(Context.WIFI_SERVICE);


        btn_connect.setOnClickListener(view -> {
            if (wifiManager.isWifiEnabled()) {
               // if(isLocnEnabled(this)){
                String location = Manifest.permission.ACCESS_FINE_LOCATION;
                if (ActivityCompat.checkSelfPermission(this, location) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    startWifiScan();
                }

      //            }else{
       //              context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
       //          }
            }else {
                //ActivityCompat.requestPermissions(new String[] { locationPermission }, RC_LOCATION);
                Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
                wifiManager.setWifiEnabled(true);
            }

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
                if (!(tmp.isEmpty()))
                {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    String[] array = {"N:" + tmp,"1"};
                    new SendSignal().execute(array);
                    alertDialog.dismiss();
                }
                else
                    edt_quantity.setError("please input a number");

            });
        });
    }

    private void startWifiScan(){
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    unregisterReceiver(this);
                    wifiScanSuccess();
                } else {
                    // scan failure handling
                    msg("WIFI error");
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(wifiScanReceiver, intentFilter);
        boolean success = false;
        // new SendSignal().execute("S");
        if (wifiManager != null) {
            success = wifiManager.startScan();
        } else {
            msg("WIFI error");
        }
        if (!success) {
            // scan failure handling
            msg("WIFI error");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode == 1) {
            if (results[0] == PackageManager.PERMISSION_GRANTED) {
                startWifiScan();
            } else {
                // user rejected permission request
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, results);
        }
    }
    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class SendSignal extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... strings) {

            respond = "";
            counter = 0;
            while(respond.isEmpty()){ // respond identifier
                counter ++;
                if ( btSocket != null ) {
                    try {
                        mmOutputStream.write(strings[0].getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    publishProgress(counter);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
                if (counter>10){
                    return null;
                }
            }

            return respond.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if (result != null){
                if (result.equals("1")){
                    msg("Set name complete");
                }else if (result.equals("-2")){
                    msg("No wifi device found");
                }else if(result.equals("3")){

                    msg("Connect successfully");
                }else if(result.equals("-3")){
                    msg("Connect failed");
                }
            }
            else {
                msg("Setting error, please try again");
                progressBar.setVisibility(View.GONE);
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
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
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    mmDevice = myBluetooth.getRemoteDevice(address.trim());
                    btSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                    mmOutputStream = btSocket.getOutputStream();
                    mmInputStream = btSocket.getInputStream();

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
                beginListenForData();
            }

            //progress.dismiss();
        }
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !stopWorker)
            {
                try
                {
                    int bytesAvailable = mmInputStream.available();
                    if(bytesAvailable > 0)
                    {
                        byte[] packetBytes = new byte[bytesAvailable];
                        mmInputStream.read(packetBytes);
                        for(int i=0;i<bytesAvailable;i++)
                        {
                            byte b = packetBytes[i];
                            if(b == delimiter)
                            {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;
                                respond = data;
                               /* if (respond.isEmpty() && !respond.equals("-2") && !respond.equals("1")){
                                    wifiList.add(respond);
                                }*/
                                //handler.post(() -> msg(data));
                            }
                            else
                            {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                }
                catch (IOException ex)
                {
                    stopWorker = true;
                }
            }
        });

        workerThread.start();
    }
//open dialog for select
private void wifiScanSuccess(){
    ArrayList<String> list = new ArrayList<>();
    for(ScanResult scanResult:wifiManager.getScanResults()){
        list.add(scanResult.SSID);
    }
  showDialog(this,list);
}

public void showDialog(Activity activity, ArrayList list){

        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview);
        TextView textView = dialog.findViewById(R.id.tv_dialogTitle);
        textView.setText("Wifi list:");
        Button btndialog = dialog.findViewById(R.id.btnDialog);
        btndialog.setOnClickListener(v -> dialog.dismiss());

        ListView listView =  dialog.findViewById(R.id.dialogListView);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.list_item, R.id.tv, list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            //textView.setText("You have clicked : "+list.get((int)id));
            String wifiName = (String) list.get((int) id);
            final EditText edt_password = new EditText(this);
            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Please input password")
                    .setMessage("length greater than 7")
                    .setView(edt_password)
                    .setPositiveButton("Ok",null)
                    .setNegativeButton("No",null)
                    .show();
            Button btn_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn_positive.setOnClickListener(v -> {
                String tmp = edt_password.getText().toString().trim();
                if (!(tmp.isEmpty()))
                {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    String[] array = {"C:"+ wifiName + "/"+ tmp,"2"};
                    new SendSignal().execute(array);
                    alertDialog.dismiss();
                }
                else
                    edt_password.setError("please input password");
            });

            dialog.dismiss();

        });

        dialog.show();

    }

    public static boolean isLocnEnabled(Context context) {
        List locnProviders = null;
        try {
            LocationManager lm =(LocationManager) context.getApplicationContext().getSystemService(Activity.LOCATION_SERVICE);
            locnProviders = lm.getProviders(true);

            return (locnProviders.size() != 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (BuildConfig.DEBUG) {
                if ((locnProviders == null) || (locnProviders.isEmpty()))
                    Log.d(TAG, "Location services disabled");
                else
                    Log.d(TAG, "locnProviders: " + locnProviders.toString());
            }
        }
        return(false);
    }
protected void onDestroy(){
        super.onDestroy();
        if (btSocket !=null){
            try {
                btSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


}

}
