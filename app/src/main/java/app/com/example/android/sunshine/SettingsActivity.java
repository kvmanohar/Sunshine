package app.com.example.android.sunshine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Sunshine Settings activity
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
               .commit();

    }


}
