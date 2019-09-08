package com.example.firebasetest1.General;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.firebasetest1.RestClient.Model.House;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class tools {

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


}
