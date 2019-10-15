package com.example.firebasetest1.General;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.firebasetest1.RestClient.Model.Area;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.Model.Tap;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Tools to be used in application context
 */
public class tools {
    public static String token;

    public static boolean toast_short(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * show long toast,
     *
     * @param context where to show the toast
     * @param string  message to be presented
     * @return
     */
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

    /**
     * Get UUID stored in shared preference.
     * @param context the context of storing
     * @return
     */
    public synchronized static String getID(Context context) {
        String uniqueID = null;
        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                PREF_UNIQUE_ID, MODE_PRIVATE);
        uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);


        return uniqueID;
    }

    /**
     * Save UUID in shared preference
     * @param context where to store
     * @param ID uuid
     */
    public synchronized static void saveID(Context context, String ID) {
        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                PREF_UNIQUE_ID, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.putString(PREF_UNIQUE_ID, ID);
        prefsEditor.apply();

    }


    /**
     * Get house object from shared preference.
     * @param context the context of storing
     * @return
     */

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

    /**
     * Get area object from shared preference.
     * @param context the context of storing
     * @return
     */

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

    /**
     * Get tap object from shared preference.
     * @param context the context of storing
     * @return
     */

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

    /**
     * Save object in shared preference
     * @param context the context of storing
     * @param preName the name of preference
     * @param storeName the name of entree in preference
     * @param object object to be saved
     */
    public synchronized static void saveObject(Context context, String preName, String storeName, Object object) {
        SharedPreferences mPrefs = context.getSharedPreferences(preName, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString(storeName, json);
        prefsEditor.apply();
    }

    /**
     * Save particular string in shared preference
     * @param context the context of storing
     * @param preName the name of preference
     * @param storeName the name of entree in preference
     * @param object object to be saved
     */
    public synchronized static void saveString(Context context, String preName, String storeName, String object) {
        SharedPreferences mPrefs = context.getSharedPreferences(preName, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(storeName, object);
        prefsEditor.apply();
    }


}
