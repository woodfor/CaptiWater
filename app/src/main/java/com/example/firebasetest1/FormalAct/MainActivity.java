package com.example.firebasetest1.FormalAct;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.firebasetest1.AboutUsFragment;
import com.example.firebasetest1.AccountFragment;
import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.HelpFragment;
import com.example.firebasetest1.Model.Token;
import com.example.firebasetest1.R;
import com.example.firebasetest1.RestClient.RestClient;
import com.example.firebasetest1.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Fragment waterusgfrag;
    Fragment areaFrag;
    Fragment houseFrag;
    //Fragment rankingFrag;

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
                //selectedFragment = areaFrag;
                selectedFragment = houseFrag;
                break;
        }

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frame_container,
                selectedFragment).commit();
        return true;
    };

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
        houseFrag = new HouseFragment();
        waterusgfrag.setArguments(bundle);
        areaFrag.setArguments(bundle);
        houseFrag.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frame_container,
                waterusgfrag).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

                        // Log and toast
                        new putREST().execute(token);
                        //Log.d("Token:", msg);
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    });

        }
        Fragment nextFragment = null;
        if (id == R.id.nav_account) {
            nextFragment = new AccountFragment();
            // Handle the camera action
        } else if (id == R.id.nav_settings) {
            nextFragment = new SettingsFragment();

        } else if (id == R.id.nav_help) {
            nextFragment = new HelpFragment();
        } else if (id == R.id.nav_about_us) {
            nextFragment = new AboutUsFragment();
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
     //   this.getSharedPreferences("HouseDAO", MODE_PRIVATE).edit().remove("SelectedHouse").apply();
    }

    protected class putREST extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            Token token = new Token(strings[0]);
            queue.add(RestClient.putToken("token", token));
            return null;
        }
    }
}
