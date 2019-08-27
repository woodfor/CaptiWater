package com.example.firebasetest1.FormalAct;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.firebasetest1.General.tools;
import com.example.firebasetest1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Fragment waterusgfrag;
    Fragment areaFrag;
    Fragment houseFrag;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = waterusgfrag;
                        break;
                    case R.id.navigation_house:

                        selectedFragment = houseFrag;
                        break;
                    case R.id.navigation_area:

                        selectedFragment = areaFrag;
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
        ActionBar bar = getSupportActionBar();
       // bar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.app_logo);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
       // navigation.setSelectedItemId(R.id.navigation_home);

        String uuid = tools.id(getApplicationContext());
        Bundle bundle = new Bundle();
        bundle.putString("uuid",uuid);
        waterusgfrag = new WaterUsageFragment();
        areaFrag = new AreaFragment();
        houseFrag = new HouseFragment();
        waterusgfrag.setArguments(bundle);
        areaFrag.setArguments(bundle);
        houseFrag.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.frame_container,
                houseFrag).commit();
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
            case R.id.navigation_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_help:
                Intent intent2 = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent2);
                break;
            case R.id.notification:
                Intent intent3 = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
