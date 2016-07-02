package app.com.example.android.sunshine;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Sunshine Settings activity
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
               .commit();

    }

}
