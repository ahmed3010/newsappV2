package com.shohayeb.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.news_per_page_key))) {
            int newsPerPage = Integer.parseInt(sharedPreferences.getString(key, getString(R.string.news_per_page_def)));
            if (newsPerPage > 100 || newsPerPage < 1) {
                sharedPreferences.edit().putString(key, getString(R.string.news_per_page_def)).apply();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(Objects.requireNonNull(upIntent))
                .startActivities();
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference newsPerPage = findPreference(getString(R.string.news_per_page_key));
            bindPreferenceSummaryToValue(newsPerPage);

            Preference orderBy = findPreference(getString(R.string.sort_order_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference sections = findPreference(getString(R.string.sections_key));
            bindPreferenceSummaryToValue(sections);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            if (preference instanceof EditTextPreference) {
                int newsPerPage = Integer.parseInt((String) value);
                if (newsPerPage > 100 || newsPerPage < 1) {
                    Toast.makeText(getContext(), R.string.news_per_page_warning, Toast.LENGTH_SHORT).show();
                    value = getString(R.string.news_per_page_def);
                }
            }
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else if (preference instanceof MultiSelectListPreference) {
                preference.setSummary(((MultiSelectListPreference) preference).getValues().toString());
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (!(preference instanceof MultiSelectListPreference)) {
                String preferenceString = preferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }
    }
}