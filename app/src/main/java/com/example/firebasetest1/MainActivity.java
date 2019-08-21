package com.example.firebasetest1;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firebasetest1.Room.DailyInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.firebasetest1.General.tools.toast_long;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    public static String EXTRA_ADDRESS = "device_address";

    String uuid ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        uuid = id(getApplicationContext());
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // initialization
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView textView = findViewById(R.id.textView);
        Button btn_freshToken = findViewById(R.id.btn_refreshToken);
        Button btn_toTheChart = findViewById(R.id.btn_toTheChart);
        Button btn_addBlueTooth = findViewById(R.id.btn_addBluetooth);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        //ScatterChart scatterChart = (ScatterChart) findViewById(R.id.scatterChart);

        //blueTooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            toast_long(getApplicationContext(),"");
        } else if  (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        textView.setText(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));

        //button register
        btn_addBlueTooth.setText("add bluetooth");
        btn_addBlueTooth.setOnClickListener(view -> {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            ArrayList list = new ArrayList();
            if ( pairedDevices.size() > 0 ) {
                for ( BluetoothDevice bt : pairedDevices ) {
                    list.add(bt.getName() + "\n" + bt.getAddress());

                }
                showDialog(this,list);
            } else {
                Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
            }
        });

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        btn_toTheChart.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,
                    LineChart.class);
            startActivity(intent);
        });

        btn_freshToken.setOnClickListener((v) -> {

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    });

        });




    }

    private class addGraphChart extends AsyncTask<Void,Void, List<DailyInfo>>{



        @Override
        protected List<DailyInfo> doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(List<DailyInfo> dailyInfos) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog(Activity activity, ArrayList list){

        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview);

        Button btndialog = (Button) dialog.findViewById(R.id.btnDialog);
        btndialog.setOnClickListener(v -> dialog.dismiss());

        ListView listView = (ListView) dialog.findViewById(R.id.dialogListView);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.list_item, R.id.tv, list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            //textView.setText("You have clicked : "+list.get((int)id));
            String info = (String) list.get((int) id);
            String name = info.substring(0,info.length()-17);
            String address = info.substring(info.length()-17);
            Log.d(TAG, "showDialog: " + address);
            Intent intent = new Intent(MainActivity.this,BlueToothActivity.class);
            intent.putExtra(EXTRA_ADDRESS,address);
            intent.putExtra("name",name);
            dialog.dismiss();
            startActivity(intent);

        });

        dialog.show();

    }
    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }


}
