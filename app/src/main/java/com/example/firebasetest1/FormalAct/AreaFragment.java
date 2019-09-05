package com.example.firebasetest1.FormalAct;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.Room.DailyInfoDatabase;
import com.example.firebasetest1.Room.House;
import com.example.firebasetest1.Room.Tap;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static com.example.firebasetest1.General.tools.toast_long;

public class AreaFragment extends Fragment implements View.OnClickListener {
    View vArea;
    String tapname;
    DailyInfoDatabase db = null;
    Context appContext;
    Context mContext;
    ListView lv_tap;
    List<Tap> taps;
    ArrayAdapter arrayAdapter;
    House house = null;
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
    WifiManager wifiManager;
    BroadcastReceiver wifiScanReceiver;
    private ProgressBar progressBar;
    Tap tap;
    int uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vArea = inflater.inflate(R.layout.fragment_area, container, false);
        TextView tv_houseName = (TextView) vArea.findViewById(R.id.tv_houseName);
        lv_tap = (ListView) vArea.findViewById(R.id.tap_listview);
        ImageView addTapButton = (ImageView) vArea.findViewById(R.id.add_tap_btn);
        SharedPreferences mPrefs = getActivity().getApplicationContext().getSharedPreferences("House", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("SelectedHouse", "");
        if (!json.isEmpty()){
            house = gson.fromJson(json,House.class);
            String houseName = house.getName();
            tv_houseName.setText(houseName);
            new fillList().execute();
        }else {
            tv_houseName.setText("Please select an area");
        }
        progressBar = vArea.findViewById(R.id.pg_area);

        //bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            toast_long(getActivity().getApplicationContext(), "");
        } else if (!myBluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        //wifi
        wifiManager = (WifiManager)
                mContext.getSystemService(Context.WIFI_SERVICE);

        addTapButton.setOnClickListener(view -> {
            if(house!=null){
                Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
                ArrayList list = new ArrayList();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice bt : pairedDevices) {
                        list.add(bt.getName() + "\n" + bt.getAddress());

                    }
                    showDialog(mContext, list);
                } else {
                    Toast.makeText(appContext, "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(appContext, "Please set area first.", Toast.LENGTH_LONG).show();
            }

        });

        return vArea;
    }

    @Override
    public void onClick(View v) {

    }
    protected class updateTap extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            db.InfoDao().updateTap(tap.getId(),strings[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void voids){
            new fillList().execute();
        }
    }
    protected class insertTap extends AsyncTask<String,Void,Long>{

        @Override
        protected Long doInBackground(String... strings) {

            db = Room.databaseBuilder(appContext,
                    DailyInfoDatabase.class, "dailyInfo_database")
                    .fallbackToDestructiveMigration()
                    .build();
            tap = new Tap(strings[0],house.getId(),address);

            return db.InfoDao().insertTap(tap);
        }
        @Override
        protected void onPostExecute(Long id){
            if (id!=0){
                String[] array = {"N:" + tap.getName() +"/"+house.getId()+"/"+id,"1"};
                new SendSignal().execute(array);

            }else {
                msg("Insert failed,please try again");
            }

        }
    }

    protected class fillList extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            db = Room.databaseBuilder(appContext,
                    DailyInfoDatabase.class, "dailyInfo_database")
                    .fallbackToDestructiveMigration()
                    .build();
            taps = db.InfoDao().getTaps(house.getId());
            List<String> strings = new ArrayList<>();
            for (Tap tap : taps) {
                strings.add(tap.getName());
            }
            return strings;
        }

        @Override
        protected void onPostExecute(List<String> tempStrings) {
            arrayAdapter = new ArrayAdapter(mContext, R.layout.list_item, R.id.tv, tempStrings);
            lv_tap.setAdapter(arrayAdapter);
            lv_tap.setOnItemLongClickListener((parent, view, position, id) -> {
                tap = taps.get((int) id);

                final Dialog dialog = new Dialog(mContext);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_edit_tap);
                EditText edt_rename = dialog.findViewById(R.id.edt_rename);
                Button btn_del = dialog.findViewById(R.id.btn_del);
                Button btn_rename = dialog.findViewById(R.id.btn_rename);
                btn_del.setOnClickListener(view1 -> {
                    new delTap().execute(tap.getId());
                    dialog.dismiss();
                });
                btn_rename.setOnClickListener(view1 -> {
                    String tmp = edt_rename.getText().toString().trim();
                    if (!(tmp.isEmpty())) {
                        /*address = tap.getAddress();
                        isBtConnected = false;

                        try {
                            btSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        if (!isBtConnected){
                           new ConnectBT().execute();
                       }
                        tapname = tmp;
                        String[] array = {"UN:" + tmp+"/"+house.getId(),"UN"};
                        new SendSignal().execute(array);


                        dialog.dismiss();
                    }
                    else
                        edt_rename.setError("please input a number");
                });
                dialog.show();
                return false;
            });

        }
    }

    protected class delTap extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            db.InfoDao().deleteTap(integers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {

            new fillList().execute();
        }
    }

    public void showDialog(Context activity, ArrayList list) {

        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview);
        TextView textView = dialog.findViewById(R.id.tv_dialogTitle);
        textView.setText("Bluetooth list:");
        Button btndialog = dialog.findViewById(R.id.btnDialog);
        btndialog.setOnClickListener(v -> dialog.dismiss());

        ListView listView = dialog.findViewById(R.id.dialogListView);
        ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.list_item, R.id.tv, list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            //textView.setText("You have clicked : "+list.get((int)id));
            String info = (String) list.get((int) id);
            String name = info.substring(0, info.length() - 17);
            address = info.substring(info.length() - 17);
            new ConnectBT().execute();
            new connectWIFI().execute();
            dialog.dismiss();

        });

        dialog.show();

    }

    private class ConnectBT extends AsyncTask<String, Void, Void> {
        private boolean ConnectSuccess = true;

        // private ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (btSocket == null || !isBtConnected) {
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
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            if (!ConnectSuccess) {
                tools.toast_long(mContext, "Connection Failed. Is it a SPP Bluetooth? Try again.");

            } else {
                if (!isBtConnected){
                    beginListenForData();
                    isBtConnected = true;
                }

            //progress.dismiss();
        }
    }
    }
    protected class connectWIFI extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while(!isBtConnected){

            }
            return  true;
        }
        @Override
        protected void onPostExecute(Boolean flag){
           if (flag){
               if (wifiManager.isWifiEnabled()) {
                   msg("Bluetooth connected, please setup its wifi");
                   // if(isLocnEnabled(this)){
                   String location = Manifest.permission.ACCESS_FINE_LOCATION;
                   if (ActivityCompat.checkSelfPermission(getActivity(), location) != PackageManager.PERMISSION_GRANTED) {
                       ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                   } else {
                       startWifiScan();
                   }

                   //            }else{
                   //              context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                   //          }
               }else {
                   //ActivityCompat.requestPermissions(new String[] { locationPermission }, RC_LOCATION);
                   Toast.makeText(mContext, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
                   wifiManager.setWifiEnabled(true);
               }
           }
          progressBar.setVisibility(View.GONE);
        }
    }


    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                try {
                    int bytesAvailable = mmInputStream.available();
                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        mmInputStream.read(packetBytes);
                        for (int i = 0; i < bytesAvailable; i++) {
                            byte b = packetBytes[i];
                            if (b == delimiter) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;
                                respond = data;
                               /* if (respond.isEmpty() && !respond.equals("-2") && !respond.equals("1")){
                                    wifiList.add(respond);
                                }*/
                                //handler.post(() -> msg(data));
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                } catch (IOException ex) {
                    stopWorker = true;
                }
            }
        });

        workerThread.start();
    }

    private class SendSignal extends AsyncTask<String,Integer,String>{

        String identifier;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {
            identifier = strings[1];
            respond = "";
            counter = 0;
            while (!isBtConnected){

            }
            if (isBtConnected){
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
            }else {
                respond = null;
            }

            return respond==null? respond : respond.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null){
                if (result.equals("1")){
                    new fillList().execute();
                    toast_long(mContext,"Set name complete");

                }else if (result.equals("UC")){
                    toast_long(mContext,"Update name complete");
                    new updateTap().execute(tapname);
                }
                else if (result.equals("-2")){
                    msg("No wifi found");
                }else if(result.equals("3")){
                    msg("Connect successfully");
                    // set name for the tap
                    final EditText edt_name = new EditText(mContext);
                    final AlertDialog setNameDialog = new AlertDialog.Builder(mContext)
                            .setTitle("Please input name of your Tap")
                            .setMessage("Using area name to label is a good idea")
                            .setView(edt_name)
                            .setPositiveButton("Ok",null)
                            .setNegativeButton("No",null)
                            .show();
                    setNameDialog.setCancelable(false);
                    Button btn_positive = setNameDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    btn_positive.setOnClickListener(v -> {
                        String tmp = edt_name.getText().toString().trim();
                        if (!(tmp.isEmpty()))
                        {
                            tapname = tmp;
                            new insertTap().execute(tapname);


                            setNameDialog.dismiss();
                        }
                        else
                            edt_name.setError("please input password");
                    });
                    setNameDialog.show();

                }else if(result.equals("-3")){
                    msg("Connect failed");
                }
            }
            else {
                if (identifier.equals("1")){
                   // new delTap().execute(tap.getId());
                }
                msg("Setting error, please try again");

            }
            progressBar.setVisibility(View.GONE);
        }


    }

    // WIFI

    private void startWifiScan(){
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    mContext.unregisterReceiver(this);
                    wifiScanSuccess();
                } else {
                    // scan failure handling
                    msg("WIFI error");
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(wifiScanReceiver, intentFilter);
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
    private void wifiScanSuccess(){
        ArrayList<String> list = new ArrayList<>();
        for(ScanResult scanResult:wifiManager.getScanResults()){
            list.add(scanResult.SSID);
        }
        showWifiDialog(mContext,list);
    }

    public void showWifiDialog(Context activity, ArrayList list){

        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview);
        TextView textView = dialog.findViewById(R.id.tv_dialogTitle);
        textView.setText("Wifi list:");
        Button btndialog = dialog.findViewById(R.id.btnDialog);
        btndialog.setOnClickListener(v -> dialog.dismiss());

        ListView listView =  dialog.findViewById(R.id.dialogListView);
        ArrayAdapter arrayAdapter = new ArrayAdapter(appContext,R.layout.list_item, R.id.tv, list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            //textView.setText("You have clicked : "+list.get((int)id));
            String wifiName = (String) list.get((int) id);
            final EditText edt_password = new EditText(mContext);
            final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
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


    private void msg (String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (btSocket != null) {
            try {
                btSocket.close();
                isBtConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}

