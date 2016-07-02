package app.com.example.android.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Settings fragment class
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_location_key))) {

            Preference pref = findPreference(getString(R.string.pref_location_key));
            pref.setSummary(sharedPreferences.getString(key, ""));

        } else if (key.equals(getString(R.string.pref_unit_key))) {

            Preference pref = findPreference(getString(R.string.pref_unit_key));
            pref.setSummary(sharedPreferences.getString(key, ""));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }
}
