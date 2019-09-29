package com.example.firebasetest1.FormalAct;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.Gecoder.Constants;
import com.example.firebasetest1.Gecoder.FetchAddressIntentService;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.Model.HouseCreation;
import com.example.firebasetest1.RestClient.Model.State;
import com.example.firebasetest1.RestClient.Model.Suburb;
import com.example.firebasetest1.RestClient.Model.User;
import com.example.firebasetest1.RestClient.RestClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QuestionActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected String lastLocation;
    EditText edt_NoP;
    String uuid;
    String name;
    String address;
    String houseName;
    String nop;
    private AddressResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        resultReceiver = new AddressResultReceiver(new Handler());
        Context mContext = this;
        uuid = tools.getID(getApplicationContext());
        Button btn_submit = findViewById(R.id.btn_submit);
        EditText edt_houseName = findViewById(R.id.edt_houseName);
        edt_NoP = findViewById(R.id.edt_No_people);
        EditText edt_address = findViewById(R.id.edt_postcode);
        EditText edt_name = findViewById(R.id.edt_UserName);
       /* edt_address.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {



        });*/
        edt_NoP.setOnClickListener(view -> showNumberPicker(mContext, 1, 20));

        btn_submit.setOnClickListener(view -> {
            name = edt_name.getText().toString();
            address = edt_address.getText().toString();
            houseName = edt_houseName.getText().toString();
            nop = edt_NoP.getText().toString();
            boolean flag = true;

            // String[] strings = {houseName,nop,address};
            //  new  test().execute(strings);
            lastLocation = address;
            if (name.isEmpty()) {
                edt_name.setError("Please input name");
                flag = false;
            }
            if (houseName.isEmpty()) {
                edt_houseName.setError("Please input house name");
                flag = false;
            }
            if (lastLocation == null || lastLocation.isEmpty()) {
                edt_address.setError("Please input address");
                flag = false;
            }
            if (!Geocoder.isPresent()) {
                tools.toast_long(getApplicationContext(), "Gecoder unavilable");
                flag = false;
            }
            if (flag == true) {
                startIntentService();
            }


        });
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        startService(intent);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void showNumberPicker(Context context, int min, int max) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Please pick a number");
        dialog.setContentView(R.layout.dialog_number_picker);
        Button btn_set = dialog.findViewById(R.id.btn_set);
        btn_set.setVisibility(View.VISIBLE);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final NumberPicker np = dialog.findViewById(R.id.numberPicker1);

        np.setMaxValue(20);
        np.setMinValue(1);
        btn_set.setOnClickListener(view -> {
            edt_NoP.setText(String.valueOf(np.getValue()));
            dialog.dismiss();
        });
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    class AddressResultReceiver extends ResultReceiver {
        TextView edt_address = findViewById(R.id.edt_postcode);

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            boolean flag = true;
            if (resultData == null) {
                return;
            }
            // Display the address string
            // or an error message sent from the intent service.
            Address addressOutput = resultData.getParcelable(Constants.RESULT_DATA_KEY);
            if (addressOutput == null) {
                edt_address.setError("Please check your address, should have contained street name, suburb,postcode and state");
                flag = false;
            } else {
                if (addressOutput.getPostalCode().isEmpty()) {
                    flag = false;
                    edt_address.setError("Please add postcode");
                }
                if (addressOutput.getLocality().isEmpty()) {
                    flag = false;
                    edt_address.setError("Please add suburb");
                }
                if (flag == true) {
                    User user = new User(uuid, name);
                    State state = new State(addressOutput.getAdminArea());
                    Suburb suburb = new Suburb(addressOutput.getLocality(), addressOutput.getPostalCode());
                    House house = new House(houseName, "",Integer.parseInt(nop));
                    HouseCreation houseCreation = new HouseCreation(user, house, state, suburb);
                    new ExecuteAndTransfer().execute(houseCreation);


                }

            }

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                // showToast(getString(R.string.address_found));
            }

        }
    }

    protected class ExecuteAndTransfer extends AsyncTask<HouseCreation, Void, Void> {


        @Override
        protected Void doInBackground(HouseCreation... houseCreations) {
            try {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String jsonInString = new Gson().toJson(houseCreations[0]);
                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, RestClient.BASE_URL + "house", new JSONObject(jsonInString),
                        response -> {
                            Log.d("Response", response.toString());
                            tools.saveID(getApplicationContext(),uuid);
                            tools.saveString(getApplicationContext(),"house","house",response.toString());
                            Intent intent = new Intent(QuestionActivity.this,MainActivity.class);
                            startActivity(intent);


                            // response
                        },
                        error -> {
                            // error
                            tools.saveID(getApplicationContext(),"");
                            try {
                                Log.d("Error.Response", error.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                tools.toast_long(getApplicationContext(),"Connection error, please connect WIFI first");
                            }

                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json; charset=utf-8");
                        params.put("Accept", "application/json");
                        return params;
                    }

                };

                queue.add(postRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }


}

