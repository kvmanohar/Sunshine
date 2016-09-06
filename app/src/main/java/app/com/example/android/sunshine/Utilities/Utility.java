package app.com.example.android.sunshine.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import app.com.example.android.sunshine.R;

/**
 * This class contains all the utility methods.
 */
public class Utility {

    //Get the LOCATION details from the Shared preference file.
    public static String getPreferredLocation(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(
                context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    //Get the Unit Type details from the Shared preference file.
    public static String getPreferredUnitType(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(
                context.getString(R.string.pref_unit_key),
                context.getString(R.string.pref_unit_metric));
    }

    //Convert the temperature to displayable string.
    public static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if (!isMetric) {
            temp = 9 * temperature / 5 + 32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }


}
