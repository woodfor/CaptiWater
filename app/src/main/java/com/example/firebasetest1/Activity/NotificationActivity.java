package com.example.firebasetest1.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.Model.Notification;
import com.example.firebasetest1.RestClient.RestClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class is for showing the ListView in Notification activity.
 *
 * @author Junjie Lu
 */
public class NotificationActivity extends AppCompatActivity {

    House house;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        house = (House) tools.getHouse(getApplicationContext());
        if (house == null) {
            tools.toast_long(getApplicationContext(), "Can't get info of House");
            finish();
        }
        lv = findViewById(R.id.lv_noti);
        getNotifications();

    }

    /**
     * Get notifications by accessing REST web service, and show the data in listview.
     * @exception NoConnectionError throw exception when no Internet connection
     * @exception com.android.volley.ServerError throw exception when return server error
     */
    private void getNotifications() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = RestClient.BASE_URL + "notification/" + house.getHid();
        JsonArrayRequest putRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.toString().equals("[]") || response.toString().isEmpty()) {
                        tools.toast_long(this, "No tap present, please add some");
                    } else {
                        List<Notification> notifications;
                        Type listType = new TypeToken<List<Notification>>() {
                        }.getType();
                        notifications = new Gson().fromJson(response.toString(), listType);
                        if (notifications != null) {
                            NotiListAdapter adapter = new NotiListAdapter(this, R.layout.listview_notification, notifications);
                            lv.setAdapter(adapter);
                        }
                    }

                },
                error -> {
                    if (error instanceof NoConnectionError) {
                        tools.toast_long(getApplicationContext(), "Please connect Internet");
                        finish();
                    } else {
                        tools.toast_long(getApplicationContext(), error.toString());
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

    /**
     * register the return button in toolbar.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    /**
     * The class is for generating customize ListView
     */
    private class NotiListAdapter extends ArrayAdapter {

        Context context;
        List<Notification> notifications;

        public NotiListAdapter(@NonNull Context context, int resource, List<Notification> notifications) {
            super(context, resource);
            this.context = context;
            this.notifications = notifications;
        }

        @Override
        public int getCount() {
            return notifications.size();
        }

        @Override
        public Notification getItem(int i) {
            return notifications.get(i);
        }

        public View getView(int i, View view, @NotNull ViewGroup viewGroup) {
            final ViewHolder holder;
            Notification notification = getItem(i);
            if (view == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.listview_notification, viewGroup, false);
                holder.imageView = view.findViewById(R.id.iv_informMark);
                holder.tv_date = view.findViewById(R.id.tv_dateTime);
                holder.tv_info = view.findViewById(R.id.tv_information);
                view.setTag(holder);
            } else {
                // the getTag returns the viewHolder object set as a tag to the view
                holder = (ViewHolder) view.getTag();
            }
            holder.tv_info.setText("Tap: " + notification.getTapName() + " from area: " + notification.getAreaName() + " has been on for " + notification.getDuration() + " minutes");
            holder.tv_date.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(notification.getDateTime()));
            return view;
        }

        private class ViewHolder {

            private ImageView imageView;
            private TextView tv_info;
            private TextView tv_date;
        }
    }

}
