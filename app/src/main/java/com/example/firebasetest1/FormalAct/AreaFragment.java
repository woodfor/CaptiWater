package com.example.firebasetest1.FormalAct;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.ListViewAdapter.AreaListAdapter;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.Area;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.RestClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AreaFragment extends Fragment {

    private View vArea;
    private Context appContext;
    private Context mContext;
    private ListView lv_area;
    private ArrayAdapter arrayAdapter;
    private House house;

    private FirebaseDatabase database;

    private DatabaseReference myRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vArea = inflater.inflate(R.layout.fragment_area, container, false);

        lv_area = vArea.findViewById(R.id.lv_area);
        FloatingActionButton lt_addArea = vArea.findViewById(R.id.abtn_addArea);

        house = WaterUsageFragment.house;
//        house = (House) tools.getHouse(getActivity().getApplicationContext());
        if (house == null) {
            tools.toast_long(getActivity().getApplicationContext(), "House object error");
            getActivity().finish();
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //set list
        getAreas();

        //add area

        lt_addArea.setOnClickListener(view -> {
            final EditText edt = new EditText(mContext);
            final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setTitle("Add area")
                    .setMessage("Please set a name")
                    .setView(edt)
                    .setPositiveButton("Ok", null)
                    .setNegativeButton("No", null)
                    .setCancelable(true)
                    .show();
            Button btn_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn_positive.setOnClickListener(v -> {
                String tmp = edt.getText().toString().trim();
                if (!(tmp.isEmpty())) {
                    addArea(tmp);

                    alertDialog.dismiss();
                } else
                    edt.setError("please input password");
            });

            alertDialog.show();

        });

        return vArea;
    }

    private void addArea(String areaName) {

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = RestClient.BASE_URL + "area/" + house.getHid() + "/" + areaName;
        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, null,
                response -> {
                    if (response.toString().equals("[]") || response.toString().isEmpty()) {
                        tools.toast_long(appContext, "Add area failure, Please try again");
                    } else {
                        getAreas();
                    }

                },
                error -> {
                    try {
                        Log.d("Error.Response", error.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        tools.toast_long(getActivity().getApplicationContext(), "Please check your Internet");

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

    private void getAreas() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = RestClient.BASE_URL + "area/" + house.getHid();

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {

                    List<Area> areas = null;
                    if (response.toString().equals("[]") || response.toString().isEmpty()) {
                        tools.toast_long(mContext, "No areas, please add some.");
                    } else {
                        Type listType = new TypeToken<List<Area>>() {}.getType();
                        areas = new Gson().fromJson(response.toString(), listType);
                        if (areas != null) {
                            AreaListAdapter adapter = new AreaListAdapter(mContext,R.layout.listview_area,areas);
                            lv_area.setAdapter(adapter);
                            List<Area> finalAreas = areas;
                            lv_area.setOnItemClickListener((adapterView, view, i, l) -> {
                                tools.saveObject(appContext,"area","area", finalAreas.get(i));
                                Intent intent = new Intent(getActivity(),TapActivity.class);
                                getActivity().startActivity(intent);

                            });

                        }
                        // display response
                    }
                },
                error -> {
                    try {
                        Log.d("Error.Response", error.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        tools.toast_long(getActivity().getApplicationContext(), "Please check your Internet");

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
        queue.add(getRequest);
    }







    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }






}

