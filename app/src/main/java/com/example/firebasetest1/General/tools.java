package com.example.firebasetest1.General;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebasetest1.FormalAct.TapActivity;
import com.example.firebasetest1.RestClient.Model.Area;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.Model.Tap;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicReference;

import static android.content.Context.MODE_PRIVATE;

public class tools {
    public static String token;
    public static boolean toast_short(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        return true;
    }

    public static boolean toast_long(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
        return true;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public static AlertDialog alertDialog(Context context, String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> alertDialog.dismiss());
        return alertDialog;
    }

    public synchronized static String getID(Context context) {
        String uniqueID = null;
        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                PREF_UNIQUE_ID, MODE_PRIVATE);
        uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);


        return uniqueID;
    }

    public synchronized static void saveID(Context context,String ID) {
        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                PREF_UNIQUE_ID, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.putString(PREF_UNIQUE_ID, ID);
        prefsEditor.apply();

    }


    public synchronized static Object getObject(Context context, String preName, String storeName, String className) throws ClassNotFoundException {
        Object object = null;
        Class name = Class.forName(className);
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                preName, MODE_PRIVATE);
        String tmp = sharedPrefs.getString(storeName, null);
        if (tmp != null) {
           object = new Gson().fromJson(tmp,name);
        }

        return object;
    }

    public synchronized static Object getHouse(Context context){
        String preName = "house";
        String storeName = "house";
        Object object = null;

        SharedPreferences sharedPrefs = context.getSharedPreferences(
                preName, MODE_PRIVATE);
        String tmp = sharedPrefs.getString(storeName, null);
        if (tmp != null) {
            object = new Gson().fromJson(tmp, House.class);
        }

        return object;
    }

    public synchronized static Object getArea(Context context){
        String preName = "area";
        String storeName = "area";
        Object object = null;

        SharedPreferences sharedPrefs = context.getSharedPreferences(
                preName, MODE_PRIVATE);
        String tmp = sharedPrefs.getString(storeName, null);
        if (tmp != null) {
            object = new Gson().fromJson(tmp, Area.class);
        }

        return object;
    }

    public synchronized static Tap getTap(Context context){
        String preName = "tap";
        String storeName = "tap";
        Tap object = null;

        SharedPreferences sharedPrefs = context.getSharedPreferences(
                preName, MODE_PRIVATE);
        String tmp = sharedPrefs.getString(storeName, null);
        if (tmp != null) {
            object = new Gson().fromJson(tmp, Tap.class);
        }

        return object;
    }

    public synchronized static void saveObject(Context context, String preName, String storeName, Object object) {
        SharedPreferences mPrefs = context.getSharedPreferences(preName, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString(storeName, json);
        prefsEditor.apply();
    }

    public synchronized static void saveString(Context context, String preName, String storeName, String object) {
        SharedPreferences mPrefs = context.getSharedPreferences(preName, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(storeName, object);
        prefsEditor.apply();
    }

//    public static String SingleInputDialog(Context context, String title, String message){
//        final EditText editText = new EditText(context);
//        final AlertDialog alertDialog = new AlertDialog.Builder(context)
//                .setTitle(title)
//                .setMessage(message)
//                .setView(editText)
//                .setPositiveButton("Ok", null)
//                .setNegativeButton("No", null)
//                .show();
//        Button btn_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        btn_positive.setOnClickListener(v -> {
//            String tmp = editText.getText().toString().trim();
//            if (!(tmp.isEmpty())) {
//
//                alertDialog.dismiss();
//
//            } else
//                editText.setError("please input password");
//        });
//    }

//    public static String getToken(){
//
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.w("Token failure", "getInstanceId failed", task.getException());
//                        return;
//                    }
//
//                    // Get new Instance ID token
//                      token = task.getResult().getToken();
//
//                    // Log and toast
//                    //new putREST().execute(token);
//                    //Log.d("Token:", msg);
//
//                });
//        return token;
//    }


}
