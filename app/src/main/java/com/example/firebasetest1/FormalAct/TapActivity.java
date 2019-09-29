package com.example.firebasetest1.FormalAct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

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
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.Area;
import com.example.firebasetest1.RestClient.Model.Tap;
import com.example.firebasetest1.RestClient.RestClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.example.firebasetest1.General.tools.toast_long;

public class TapActivity extends AppCompatActivity {

    private ListView lv;
    private Area area;
    private Tap tap;
    private ProgressBar pg;
    private Context mContext = this;
    List<Tap> taps = null;
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
    //wifi
    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;
    //firebase
    List<HashMap<String, String>> tapListArray;
    SimpleAdapter arrayAdapter;
    private FirebaseDatabase database;
    private DatabaseReference statusRef;
    private DatabaseReference myRef;
    //Rest
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap);
        TextView tv_title = findViewById(R.id.tv_titleName);
        lv = findViewById(R.id.lv);
        tap = new Tap();
        FloatingActionButton fab_addTap = findViewById(R.id.fab_addTap);
        pg = findViewById(R.id.pb);
        queue = Volley.newRequestQueue(getApplicationContext());
        //setup
        setTitle("Tap List");
        try {
            area = (Area) tools.getArea(getApplicationContext());

            tv_title.setText(area.getName());
        }catch (Exception e){
            e.printStackTrace();
            tools.toast_long(getApplicationContext(),"Can't get info of area");
            finish();
        }
        //bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            tools.toast_long(getApplicationContext(),"Please open bluetooth");
            finish();
        } else if (!myBluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        wifiManager = (WifiManager) getSystemService(this.WIFI_SERVICE);

        //firebase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        statusRef = myRef.child("turn");
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(Integer.class) == 1 ? "ON" : "OFF";
                if (tapListArray!=null){
                    for ( HashMap<String, String> map : tapListArray){
                        map.put("status",value);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Tap", "Failed to read value.", error.toException());
            }
        });

        getTaps();


        fab_addTap.setOnClickListener(view -> {
            Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
            ArrayList list = new ArrayList();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice bt : pairedDevices) {
                    list.add(bt.getName() + "\n" + bt.getAddress());

                }
                showBluetoothDialog(list);

            } else {
                Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void btBeginListenForData() {
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
                                btRespond = data;
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



    private class ConnectBT extends AsyncTask<String, Void, Void> {
        private boolean ConnectSuccess = true;

        // private ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            pg.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (btSocket == null || !isBtConnected) {
                    mmDevice = myBluetooth.getRemoteDevice(strings[0]);
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
            pg.setVisibility(View.GONE);
            if (!ConnectSuccess) {
                tools.toast_long(getApplicationContext(), "Connection Failed. Is it a SPP Bluetooth? Try again.");

            } else {
                if (!isBtConnected) {
                    btBeginListenForData();
                    isBtConnected = true;
                }

            }
        }
    }

    protected class ConnectWIFI extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pg.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isBtConnected) {
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (flag) {
                if (wifiManager.isWifiEnabled()) {
                    tools.toast_long(getApplicationContext(),"Bluetooth connected, please setup its wifi");
                    // if(isLocnEnabled(this)){
                    String location = Manifest.permission.ACCESS_FINE_LOCATION;
                    if (ActivityCompat.checkSelfPermission(TapActivity.this, location) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(TapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else {
                        wifiScanReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context c, Intent intent) {
                                boolean success = intent.getBooleanExtra(
                                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                                if (success) {
                                    TapActivity.this.unregisterReceiver(this);
                                    ArrayList<String> list = new ArrayList<>();
                                    //show dialog
                                    for (ScanResult scanResult : wifiManager.getScanResults()) {
                                        list.add(scanResult.SSID);
                                    }
                                    showWifiDialog(list);

                                } else {
                                    // scan failure handling
                                    tools.toast_long(mContext,"WIFI error");
                                }
                            }
                        };
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                        mContext.registerReceiver(wifiScanReceiver, intentFilter);
                        boolean success = false;
                        if (wifiManager != null) {
                            success = wifiManager.startScan();
                        } else {
                            tools.toast_long(mContext,"WIFI error");
                        }
                        if (!success) {
                            // scan failure handling
                            tools.toast_long(mContext,"WIFI error");
                        }
                    }

                    //            }else{
                    //              context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    //          }
                } else {
                    //ActivityCompat.requestPermissions(new String[] { locationPermission }, RC_LOCATION);
                    Toast.makeText(TapActivity.this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
                    wifiManager.setWifiEnabled(true);
                }
            }
            pg.setVisibility(View.GONE);
        }
    }

    private void showWifiDialog( ArrayList list) {

        final Dialog dialog = new Dialog(mContext);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview);
        TextView textView = dialog.findViewById(R.id.tv_dialogTitle);
        textView.setText("Wifi list:");
        Button btndialog = dialog.findViewById(R.id.btnDialog);
        btndialog.setOnClickListener(v -> dialog.dismiss());

        ListView listView = dialog.findViewById(R.id.dialogListView);
        ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.list_item, R.id.tv, list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            //textView.setText("You have clicked : "+list.get((int)id));
            String wifiName = (String) list.get((int) id);
            final EditText edt_password = new EditText(mContext);
            final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setTitle("Please input password")
                    .setMessage("length greater than 7")
                    .setView(edt_password)
                    .setPositiveButton("Ok", null)
                    .setNegativeButton("No", null)
                    .show();
            Button btn_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn_positive.setOnClickListener(v -> {
                String tmp = edt_password.getText().toString().trim();
                if (!(tmp.isEmpty())) {

                    String[] array = {"C:" + wifiName + "/" + tmp, "2"};
                    new SendBTSignal().execute(array);
                    alertDialog.dismiss();
                } else
                    edt_password.setError("please input password");
            });

            dialog.dismiss();

        });

        dialog.show();

    }

    private void showBluetoothDialog( ArrayList list){
        final Dialog dialog = new Dialog(this);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview);
        TextView textView = dialog.findViewById(R.id.tv_dialogTitle);
        textView.setText("Bluetooth list:");
        Button btndialog = dialog.findViewById(R.id.btnDialog);
        btndialog.setOnClickListener(v -> dialog.dismiss());

        ListView listView = dialog.findViewById(R.id.dialogListView);
        //ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.list_item, R.id.tv, list);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.list_item, R.id.tv, list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            //textView.setText("You have clicked : "+list.get((int)id));
            String info = (String) list.get((int) id);
            String name = info.substring(0, info.length() - 17);
            String address = info.substring(info.length() - 17).trim();
            tap.setBtaddress(address);
            new ConnectBT().execute(address);
            new ConnectWIFI().execute();
            dialog.dismiss();

        });

        dialog.show();
    }

    private class SendBTSignal extends AsyncTask<String, Integer, String> {

        char identifier;

        @Override
        protected void onPreExecute() {
            pg.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            identifier = strings[0].charAt(0);

            btRespond = "";
            int counter = 0;
            while (!isBtConnected) {

            }
            counter = 0;
                while (btRespond.isEmpty()) { // respond identifier
                    counter++;
                    if (btSocket != null) {
                        try {
                            mmOutputStream.write(strings[0].getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }

                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                    if (counter > 10) {
                        return null;
                    }
                }

            return btRespond == null ? btRespond : btRespond.trim();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (result.equals("1")) {
                //    new AreaFragment.getArea().execute();
                    toast_long(mContext, "Set name complete");

                } else if (result.equals("UC")) {
                    toast_long(mContext, "Update name complete");
                   // new AreaFragment.updateTap().execute(tapname);
                } else if (result.equals("-2")) {
                    msg("No wifi found");
                } else if (result.equals("3")) {
                    tools.toast_long(mContext,"Connect successfully");
                    // set name for the tap
                    final EditText edt_name = new EditText(mContext);
                    final AlertDialog setNameDialog = new AlertDialog.Builder(mContext)
                            .setTitle("Add Tap")
                            .setMessage("Please input tap name.")
                            .setView(edt_name)
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("No", null)
                            .show();
                    setNameDialog.setCancelable(false);
                    Button btn_positive = setNameDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    btn_positive.setOnClickListener(v -> {
                        String tmp = edt_name.getText().toString().trim();
                        if (!(tmp.isEmpty())) {
                            tap.setName(tmp);

                            addTap();

                            setNameDialog.dismiss();
                        } else
                            edt_name.setError("please input tap name");
                    });
                    setNameDialog.show();

                } else if (result.equals("-3")) {
                    msg("wifi Connect failed");
                }
            } else {
                if (identifier == 'N'){
                    delTap(tap.getTid());

                }
                msg("Setting error, please try again");

            }
            pg.setVisibility(View.GONE);
        }


    }

private void delTap(long tid){
    String url = RestClient.BASE_URL + "tap/" + tid;
    StringRequest delRequest = new StringRequest(Request.Method.DELETE,url,
            response -> {
                getTaps();
            },
            error -> {
                Log.d("del Tap error:", "delTap: " +  error.toString());
            }
            );
    queue.add(delRequest);
}

private void addTap(){
    FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("Token failure", "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();
                String url = RestClient.BASE_URL + "tap/" + area.getAid() + "/" + tap.getName()+"/"+ tap.getBtaddress()+"/"+token;
                JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, null,
                        response -> {
                            if (response.toString().equals("[]") || response.toString().isEmpty()) {
                                tools.toast_long(this, "Add tap failure, Please try again");
                            } else {
                                Tap tmptap = new Gson().fromJson(response.toString(),Tap.class);
                                tap = tmptap;
                                String array = "N:"  + tap.getName()+"/"+area.getAid()+ "/" + tmptap.getTid();
                                new SendBTSignal().execute(array);
                                getTaps();
                            }

                        },
                        error -> {
                            try {
                                Log.d("Error.Response", error.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                tools.toast_long(getApplicationContext(), "Please check your Internet");

                            }

                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Accept", "application/json");
                        return params;
                    }
                };
                queue.add(putRequest);
                // Log and toast
                //new putREST().execute(token);
                //Log.d("Token:", msg);

            });


}
    private void getTaps(){

        String url = RestClient.BASE_URL + "tap/" + area.getAid();
        JsonArrayRequest putRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.toString().equals("[]") || response.toString().isEmpty()) {
                        tools.toast_long(this, "No tap present, please add some");
                    } else {

                        Type listType = new TypeToken<List<Tap>>() {
                        }.getType();
                        taps = new Gson().fromJson(response.toString(),listType);
                        if (taps != null) {

                            statusRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    tapListArray = new ArrayList<>();
                                    HashMap<String,String> map;
                                    String status= dataSnapshot.getValue(Integer.class) == 1 ? "ON" : "OFF";
                                    for (Tap a : taps) {
                                        map=new HashMap<>();
                                        map.put("name",a.getName());
                                        map.put("status", status);
                                        tapListArray.add(map);
                                    }
                                    String[] colHEAD = new String[] {"name","status"};
                                    int[] dataCell = new int[] {R.id.text_tap_list,R.id.text_tap_control};
                                    arrayAdapter = new SimpleAdapter(mContext,tapListArray, R.layout.listview_tap, colHEAD,dataCell){
                                        @Override
                                        public View getView(int position, View convertView, ViewGroup parent){
                                            // Get the current item from ListView
                                            View view = super.getView(position,convertView,parent);
                                            ImageView option = view.findViewById(R.id.imageView_popup_tap);
                                            option.setOnClickListener(view1 -> {
                                                PopupMenu popupMenu = new PopupMenu(mContext, option);
                                                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_tap, popupMenu.getMenu());
                                                popupMenu.setOnMenuItemClickListener(menuItem -> {
                                                    switch (menuItem.getItemId()) {
                                                        case R.id.lock_tap:
                                                            statusRef.setValue(0);
                                                            break;
                                                        case R.id.unlock_tap:
                                                            statusRef.setValue(1);
                                                            break;
                                                        case R.id.remove_tap:
                                                            delTap(taps.get(position).getTid());
                                                            break;
                                                        case R.id.rename_tap:
                                                            break;
                                                        case R.id.settings_tap:
                                                            tools.saveObject(getApplicationContext(),"tap","tap",taps.get(position));
                                                            Intent intent = new Intent(TapActivity.this,TapSettingActivity.class);
                                                            startActivity(intent);

                                                    }
                                                    return true;
                                                });
                                                popupMenu.show();

                                            });
                                            //set colour for status
                                            TextView tv_status = view.findViewById(R.id.text_tap_control);
                                            if (tv_status.getText().toString().equals("ON")){
                                                tv_status.setTextColor(Color.parseColor("#4CAF50"));
                                            }else {
                                                tv_status.setTextColor(Color.parseColor("#D81B60"));
                                            }
                                            return view;
                                        }
                                    };
                                    lv.setAdapter(arrayAdapter);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    tools.toast_long(getApplicationContext(),"can't get status");
                                }
                            });

                        }
                    }

                },
                error -> {
                    try {
                        Log.d("Error.Response", error.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        tools.toast_long(getApplicationContext(), "Please check your Internet");

                    }

                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                return params;
            }
        };
        queue.add(putRequest);

    }
    private void msg(String text){
        tools.toast_long(getApplicationContext(),text);
    }


  /*  private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
*/



    @Override
    public void onPause() {
        super.onPause();
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

