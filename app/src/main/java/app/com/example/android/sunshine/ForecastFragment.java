package app.com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 * http://api.openweathermap.org/data/2.5/find?q=London,uk&units=metric&appid=4000c8df847f8f70e1e052a2855da229
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;
    public AdapterView.OnItemClickListener lvOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String forecast = mForecastAdapter.getItem(position);

            Intent intent = new Intent(getActivity(), DetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, forecast);

            startActivity(intent);

        }
    };

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        populateListView(rootView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
//                super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeatherData() {

        //Get postcode of the location from the Shared preference file
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        //Display TOAST message to which the weather data is displayed
        Toast.makeText(getActivity(), "Weather for location: " + location, Toast.LENGTH_LONG).show();

        //Call FetchWeatherTask by passing the postcode string
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(location);

    }

    private void populateListView(View view) {

        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/40",
                "Wed - Cloudy - 72/60",
                "Thu - Asteroids - 72/60",
                "Fri - Heavy Rain - 72/60",
                "Sat - Help Pepped in Weather station - 72/60",
                "Sun - Sunny - 80/68"
        };
        List<String> weekForecastList = new ArrayList<>(
                Arrays.asList(forecastArray));

        //Now that we have dummy forecast data, create Array adapter.
        //The Array adapter takes data from a source (like our dummy forecastArray
        //use it populate the ListView it's attached to.
         mForecastAdapter = new ArrayAdapter<>(
                //the Current context (this fragment's parent activity
                getActivity(),
                //ID of list item layout
                R.layout.list_item_forecast,
                //ID of the textView to populate
                R.id.list_item_forecast_textview,
                // Forecast data
                 weekForecastList);

        // Get a reference to the ListView and attach this adapter to the ListView
        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(lvOnItemClickListener);

    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String formatHighLows(double high, double low, String unitType) {

            if (unitType.equals(getString(R.string.pref_unit_imperial))) {

                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            } else if (!unitType.equals(getString(R.string.pref_unit_metric))) {
                Log.d(LOG_TAG, "Temperature Unit Type invalid : " + unitType);
            }

            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            return roundedHigh + "/" + roundedLow;
        }

        private String buildUriString(String postCode, int numDays) {
//      "http://api.openweathermap.org/data/2.5/forecast/daily?q=SS141FG&mode=json&units=metric&cnt=7";
            String format = "json";
            String units = "metric";
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNIT_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APPID_PARAM = "APPID";

            Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, postCode)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNIT_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .build();
            return buildUri.toString();
        }

        private String extractJsonFromInputStream(InputStream inputStream) {
            BufferedReader bufferedReader;
            String forecastJsonStr = null;
            String line;

            StringBuilder builder = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                if (builder.length() == 0) return null;
                forecastJsonStr = builder.toString();

            } catch (IOException e) {
                Log.e("extractJSON", "", e);
            } finally {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error Closing bufferReader", e);
                }
            }
            return forecastJsonStr;
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            GregorianCalendar gc = new GregorianCalendar();
            gc.add(GregorianCalendar.DATE, -1);

            String[] resultStrs = new String[numDays];

            //Get Temperature units from the Shared preference file
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = prefs.getString(
                    getString(R.string.pref_unit_key),
                    getString(R.string.pref_unit_metric));

            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // Build the Day and date string
                gc.add(GregorianCalendar.DATE, 1);
                Date time = gc.getTime();
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                day = shortenedDateFormat.format(time);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low, unitType);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) return null;

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            int numDays = 7;

            try {
                String weatherUri;
                weatherUri = buildUriString(params[0], numDays);
                Log.v(LOG_TAG, "Build URI " + weatherUri);

                // Create the request to OpenWeatherMap, and open the connection
                URL url = new URL(weatherUri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) return null;

                forecastJsonStr = extractJsonFromInputStream(inputStream);
                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;

            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result !=null){
                mForecastAdapter.clear();
                for (String dayForeCastStr : result){
                    mForecastAdapter.add(dayForeCastStr);
                }
            }
//            super.onPostExecute(strings);
        }
    }
}

