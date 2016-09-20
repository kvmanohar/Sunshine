package app.com.example.android.sunshine.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Weather Database table definitions
 */
public class WeatherContract {

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        //normalize the start date to beginning of the UTC day.

        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Wind speed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";
    }


    /* Inner class that defines the table contents of the location table     */
    public static final class LocationEntry implements BaseColumns {

        public static final String TABLE_NAME = "location";

        //The location settings string is what will be sent to OpenWeatherMap as location query
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        //Human readable location string, provided by the API. Because for styling, "Mountain view"
        // is more recognizable than the postcode
        public static final String COLUMN_CITY_NAME = "city_name";

        //In order to uniquely pinpint the location on the map when we launch the map intent, we
        //store the latitude and longitude as returned by openWeatherMap.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

    }

}
