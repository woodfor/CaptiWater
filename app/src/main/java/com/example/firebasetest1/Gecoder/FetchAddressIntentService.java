package com.example.firebasetest1.Gecoder;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.firebasetest1.General.tools;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FetchAddressIntentService extends IntentService {

    // ...
    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }
    private void deliverResultToReceiver(ResultReceiver resultReceiver, int resultCode, Address message) {
        if (message!=null){
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.RESULT_DATA_KEY, message);
            resultReceiver.send(resultCode, bundle);
        }else {
            Handler mainHandler = new Handler(getMainLooper());
            mainHandler.post(() -> {
                // Do your stuff here related to UI, e.g. show toast
                tools.toast_long(getApplicationContext(), "No information found with this postcode, Please try again");

            });
            // tools.toast_long(getApplicationContext(),"error");
        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if (intent == null) {
            return;
        }
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        String location = intent.getStringExtra(
                Constants.LOCATION_DATA_EXTRA);

        // ...
        ResultReceiver resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(
                    location,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            Log.e(TAG, "Service not found", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e(TAG, "Service error");
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                Log.e(TAG, "no_address_found");
            }
            deliverResultToReceiver(resultReceiver,Constants.FAILURE_RESULT, null);
        } else {
            Address address = addresses.get(0);
          //  ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
          /*  for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }*/
            Log.i(TAG, "address_found");
            deliverResultToReceiver(resultReceiver,Constants.SUCCESS_RESULT,
                   address);
        }
    }
}