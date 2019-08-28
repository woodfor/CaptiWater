package com.example.firebasetest1.General;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class tools {

    public static boolean toast_short(Context context, String string)
    {
        Toast.makeText(context,string, Toast.LENGTH_SHORT).show();
        return true;
    }
    public static boolean toast_long(Context context, String string)
    {
        Toast.makeText(context,string, Toast.LENGTH_LONG).show();
        return true;
    }
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    public static AlertDialog alertDialog(Context context, String title, String message){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> alertDialog.dismiss());
        return alertDialog;
    }

    public synchronized static String id(Context context) {
        String uniqueID=null;
        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }
        return uniqueID;
    }

    public synchronized static void saveObject(Context context,String preName ,String storeName, Object object){
        SharedPreferences  mPrefs = context.getSharedPreferences(preName,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString(storeName, json);
        prefsEditor.apply();
    }



}
