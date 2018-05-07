package com.shohayeb.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnSectionClickListner {
    private ViewPager viewPager;
    private TabLayout tableLayout;
    private Map<String, Integer> index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        tableLayout = findViewById(R.id.tabs);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SectionsAdapter adapter = new SectionsAdapter(getSupportFragmentManager(), getResources(), prefs);
        viewPager.setAdapter(adapter);
        tableLayout.setupWithViewPager(viewPager);
        index = new HashMap<>();
        for (int i = 0; i < tableLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tableLayout.getTabAt(i);
            index.put(String.valueOf(tab.getText()), i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(String title) {
        if (index.containsKey(title)) {
            TabLayout.Tab tab = tableLayout.getTabAt(index.get(title));
            tab.select();
        }
    }

    @Override
    public void onBackPressed() {
        if (tableLayout.getSelectedTabPosition() != 0) {
            TabLayout.Tab tab = tableLayout.getTabAt(0);
            tab.select();
        } else {
            finish();
        }
    }
}
