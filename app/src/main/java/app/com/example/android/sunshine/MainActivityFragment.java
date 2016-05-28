package app.com.example.android.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 * http://api.openweathermap.org/data/2.5/find?q=London,uk&units=metric&appid=4000c8df847f8f70e1e052a2855da229
 */
public class MainActivityFragment extends Fragment {

    private View rootView;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        populateListView();
        //  httpConnectionToGetWeatherData();
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
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("SS141FG");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateListView() {

        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/40",
                "Wed - Cloudy - 72/60",
                "Thu - Asteroids - 72/60",
                "Fri - Heavy Rain - 72/60",
                "Sat - Help Pepped in Weather station - 72/60",
                "Sun - Sunny - 80/68"
        };
        List<String> weekForecast = new ArrayList<>(
                Arrays.asList(forecastArray));

        //Now that we have dummy forecast data, create Array adapter.
        //The Array adapter takes data from a source (like our dummy forecastArray
        //use it populate the ListView it's attached to.
        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<>(
                //the Current context (this fragment's parent activity
                getActivity(),
                //ID of list item layout
                R.layout.list_item_forecast,
                //ID of the textView to populate
                R.id.list_item_forecast_textview,
                // Forecast data
                weekForecast);

        // Get a reference to the ListView and attach this adapter to the ListView
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;

            String weatherUri;
            String forecastJsonStr;  // Store JsonString

            try {

                weatherUri = buildUriString(params[0], 7);
                Log.v(LOG_TAG, "Build URI : " + weatherUri);

                URL url = new URL(weatherUri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                forecastJsonStr = extractJSONFromInputStream(inputStream);
                Log.v("Forecast info : ", forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error :", e);

            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
            return null;
        }

        public String buildUriString(String postCode, int numDays) {

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

        public String extractJSONFromInputStream(InputStream inputStream) {

            BufferedReader bufferedReader;
            String forecastJsonStr = null;
            String line;

            StringBuilder builder = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                if (builder.length() == 0) {
                    return null;
                }
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

    }
}
