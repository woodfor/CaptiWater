package com.example.firebasetest1.General;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

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
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }


}
