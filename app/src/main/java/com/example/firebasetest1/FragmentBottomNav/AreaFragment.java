package com.example.firebasetest1.FragmentBottomNav;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.Activity.TapActivity;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.Area;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.RestClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.navigation_area).setChecked(true);
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
                        Type listType = new TypeToken<List<Area>>() {
                        }.getType();
                        areas = new Gson().fromJson(response.toString(), listType);
                        if (areas != null) {
                            AreaListAdapter adapter = new AreaListAdapter(mContext, R.layout.listview_area, areas);
                            lv_area.setAdapter(adapter);
                            List<Area> finalAreas = areas;
                            lv_area.setOnItemClickListener((adapterView, view, i, l) -> {
                                tools.saveObject(appContext, "area", "area", finalAreas.get(i));
                                Intent intent = new Intent(getActivity(), TapActivity.class);
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


    private void delArea(int ID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = RestClient.BASE_URL + "area/" + ID;
        StringRequest delRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    getAreas();
                },
                error -> {
                    tools.toast_long(appContext, "Delete error, " + error.toString());
                }
        );
        queue.add(delRequest);
    }

    private void updateArea(int ID, String name){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = RestClient.BASE_URL + "area/update/" + ID + "/"+name;
        StringRequest delRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    if (response.equals("Success")){
                        getAreas();
                    }else {
                        tools.toast_long(appContext,"Server error");
                    }

                },
                error -> {
                    tools.toast_long(appContext, "Update error, " + error.toString());
                }
        );
        queue.add(delRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }

    private class AreaListAdapter extends ArrayAdapter<Area> {
        private Context context;
        private List<Area> areas;

        public AreaListAdapter(@NonNull Context context, int resource, @NonNull List<Area> areas) {
            super(context, resource, areas);
            this.context = context;
            this.areas = areas;
        }

        @Override
        public Area getItem(int i) {
            return areas.get(i);
        }

        @NotNull
        @Override
        public View getView(int i, View view, @NotNull ViewGroup viewGroup) {
            final ViewHolder holder;
            Area area = getItem(i);
            if (view == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.listview_area, viewGroup, false);
                holder.iv_option = view.findViewById(R.id.imageView_popup_area);
                holder.tv_areaNames = view.findViewById(R.id.text_area_list);
                view.setTag(holder);
            } else {
                // the getTag returns the viewHolder object set as a tag to the view
                holder = (ViewHolder) view.getTag();
            }

            holder.tv_areaNames.setText(area.getName());
            holder.iv_option.setOnClickListener(view1 -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.iv_option);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_area, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.lock_area:
                            myRef.child("turn").setValue(0);
                            break;
                        case R.id.remove_area:
                            final AlertDialog delDialog = new AlertDialog.Builder(mContext)
                                    .setTitle("Delete Area: " + areas.get(i).getName())
                                    .setMessage("Are you sure?")
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("No", null)
                                    .show();
                            Button btn_positive_del = delDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            btn_positive_del.setOnClickListener(view2 -> {
                                delArea((int) area.getAid());
                                delDialog.dismiss();
                            });
                            break;
                        case R.id.rename_area:
                            final EditText editText = new EditText(context);
                            final AlertDialog alertDialog = new AlertDialog.Builder(context)
                                    .setTitle("Rename Area")
                                    .setMessage("Please input the name you want.")
                                    .setView(editText)
                                    .setPositiveButton("Ok", null)
                                    .setNegativeButton("No", null)
                                    .show();
                            Button btn_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            btn_positive.setOnClickListener(v -> {
                                String tmp = editText.getText().toString().trim();

                                if (!(tmp.isEmpty())) {
                                    updateArea((int) area.getAid(),tmp);
                                    alertDialog.dismiss();

                                } else
                                    editText.setError("Please input something");
                            });

                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });


            return view;
        }

        private class ViewHolder {

            private ImageView iv_option;
            private TextView tv_areaNames;
        }
    }


}

