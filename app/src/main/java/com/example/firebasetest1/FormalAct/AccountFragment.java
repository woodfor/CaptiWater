package com.example.firebasetest1.FormalAct;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.RestClient;
import com.google.gson.Gson;



public class AccountFragment extends Fragment{
    View vAccount;
    EditText edt_NoP;
    EditText edt_houseName;
    String houseName;
    String nop;
    private Context appContext;
    private Context mContext;
    private House house;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vAccount = inflater.inflate(R.layout.fragment_account, container, false);

        Context mContext = this.getContext();
        house = WaterUsageFragment.house;
//        house = (House) tools.getHouse(getActivity().getApplicationContext());
        if (house == null) {
            tools.toast_long(getActivity().getApplicationContext(), "House object error");
            getActivity().finish();
        }

        edt_NoP = vAccount.findViewById(R.id.edt_No_people);
        edt_NoP.setText(String.valueOf(house.getNop()));

        edt_houseName = vAccount.findViewById(R.id.edt_houseName);
        edt_houseName.setText(house.getName());

        edt_NoP.setOnClickListener(view -> showNumberPicker(mContext, 1, 10));


        Button btn_submit_changes = vAccount.findViewById(R.id.btn_submit_changes);
        btn_submit_changes.setOnClickListener(view -> {
            houseName = edt_houseName.getText().toString();
            nop = edt_NoP.getText().toString();
            //AwesomeValidation validation = new AwesomeValidation(BASIC);
            if (houseName.isEmpty()) {
                edt_houseName.setError("Please input house name");

            }else if (houseName.equals(house.getName()) && nop.equals(house.getNop())){
                tools.toast_long(mContext,"Nothing changed");
            }else {
                updateHouseInfo(houseName,nop,house.getHid()+"");
            }
        });
        return vAccount;
    }

    private void updateHouseInfo(String houseName, String nop, String ID){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = RestClient.BASE_URL + "house/update/" + ID+"/"+houseName+"/"+nop;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    if (response !=null && !response.equals("[]")){
                        try {
                            tools.saveObject(appContext,"house","house",new Gson().fromJson(response,House.class));
                            tools.toast_long(appContext,"House updated");
                        }catch (Exception e){
                            tools.toast_long(appContext,"Server error");
                        }

                    }else {
                        tools.toast_long(appContext,"Server error");
                    }

                },
                error -> {
                    tools.toast_long(appContext, "Update error, " + error.toString());
                }
        );
        queue.add(putRequest);
    }



    private void showNumberPicker(Context context, int min, int max) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Number of people");
        dialog.setContentView(R.layout.dialog_number_picker);
        Button btn_set = dialog.findViewById(R.id.btn_set);
        btn_set.setVisibility(View.VISIBLE);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final NumberPicker np = dialog.findViewById(R.id.numberPicker1);

        np.setMaxValue(10);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }
}
