package app.com.example.android.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main,container,false);
        populateListView();

        return rootView;
    }

    public void populateListView(){

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

}
