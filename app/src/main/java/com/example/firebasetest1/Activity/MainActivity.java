package com.example.firebasetest1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.FragmentBottomNav.AreaFragment;
import com.example.firebasetest1.FragmentBottomNav.RankingFragment;
import com.example.firebasetest1.FragmentBottomNav.WaterUsageFragment;
import com.example.firebasetest1.FragmentDrawer.AboutUsFragment;
import com.example.firebasetest1.FragmentDrawer.AccountFragment;
import com.example.firebasetest1.FragmentDrawer.HelpFragment;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.Model.House;
import com.example.firebasetest1.RestClient.RestClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The class is the host activity for the fragments in the bottom navigation bar.
 *
 * @author Junjie Lu
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment waterusgfrag;
    private Fragment areaFrag;

    boolean doubleBackToExitPressedOnce = false;
    //Fragment rankingFrag;
    /**
     *  On click listener for bottom navigation bar
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                selectedFragment = waterusgfrag;
                break;
            case R.id.navigation_house:
                selectedFragment = new RankingFragment();
                //selectedFragment = houseFrag;
                break;
            case R.id.navigation_area:
                selectedFragment = areaFrag;
                // selectedFragment = houseFrag;
                break;

        }

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frame_container,
                selectedFragment).commit();
        return true;
    };

    /**
     * initial the components.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        // toolbar.setNavigationIcon(R.drawable.app_logo);
        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.app_logo);

        //   toolbar.setLogo(R.drawable.app_logo);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
        //drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        //get house

        //stop here
        String uuid = tools.getID(getApplicationContext());
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uuid);
        waterusgfrag = new WaterUsageFragment();
        areaFrag = new AreaFragment();

        waterusgfrag.setArguments(bundle);
        areaFrag.setArguments(bundle);


        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frame_container,
                waterusgfrag).commit();
    }

    /**
     * Click back twice to close the application.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else  if (doubleBackToExitPressedOnce){
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        tools.toast_long(getApplicationContext(),"Please press back again to exit");
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification:
                Intent intent3 = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_help) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("Token failure", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        House house = (House) tools.getHouse(getApplicationContext());
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        String url = RestClient.BASE_URL + "tap/token/" + house.getHid()+"/"+token;
                        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                                response -> {
                                    tools.toast_long(getApplicationContext(),"update success");
                                },
                                error -> {
                                    try {
                                        Log.d("Error.Response", error.getMessage());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        tools.toast_long(getApplicationContext(), "Please check your Internet");

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


                    });

        }
        Fragment nextFragment = null;
        if (id == R.id.nav_account) {
            nextFragment = new AccountFragment();
            // Handle the camera action
        } else if (id == R.id.nav_help) {
            nextFragment = new HelpFragment();
        } else if (id == R.id.nav_about_us) {
            nextFragment = new AboutUsFragment();
        } else if (id == R.id.draw_nav_home){
            nextFragment = waterusgfrag;
        }
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frame_container, nextFragment).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
