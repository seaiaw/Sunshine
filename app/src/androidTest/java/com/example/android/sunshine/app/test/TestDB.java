package com.example.android.sunshine.app.test;

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.test.AndroidTestCase;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.android.sunshine.data.WeatherDbHelper;
import com.example.android.sunshine.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.data.WeatherContract.LocationEntry;


/**
 * Created by seaiaw on 26/9/14.
 */
public class TestDB extends AndroidTestCase {
    public static final String LOG_TAG = TestDB.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext)
                                .getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        String testName = "North Pole";
        String testLocationSetting = "99705";
        double testLatitude = 64.722;
        double testLongitude = -147.355;

        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, testName);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);

        long locationRowId = db.insert(LocationEntry.TABLE_NAME,
                                        null,
                                        values);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New Row Id: " + locationRowId);

        String[] columns = {
                LocationEntry._ID,
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_LOCATION_SETTING,
                LocationEntry.COLUMN_COORD_LAT,
                LocationEntry.COLUMN_COORD_LONG
        };

        Cursor cursor = db.query(LocationEntry.TABLE_NAME,
                                    columns,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);

        if (cursor.moveToFirst()) {
            int locationIndex = cursor.getColumnIndex(LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);

            int nameIndex = cursor.getColumnIndex(LocationEntry.COLUMN_CITY_NAME);
            String name = cursor.getString(nameIndex);

            int latIndex = cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LAT);
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex(LocationEntry.COLUMN_COORD_LONG);
            double longitude = cursor.getDouble(longIndex);

            assertEquals(location, testLocationSetting);
            assertEquals(name, testName);
            assertEquals(latitude, testLatitude);
            assertEquals(longitude, testLongitude);


            // Fantastic.  Now that we have a location, add some weather!
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
            weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
            weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
            weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
            weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
            weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
            weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
            weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
            weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

            long weatherRowId = db.insert(WeatherEntry.TABLE_NAME,
                    null,
                    weatherValues);

            assertTrue(weatherRowId != -1);
            Log.d(LOG_TAG, "New Row Id: " + weatherRowId);

            String[] weatherColumns = {
                    WeatherEntry._ID,
                    WeatherEntry.COLUMN_LOC_KEY,
                    WeatherEntry.COLUMN_DATETEXT,
                    WeatherEntry.COLUMN_DEGREES,
                    WeatherEntry.COLUMN_HUMIDITY,
                    WeatherEntry.COLUMN_PRESSURE,
                    WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherEntry.COLUMN_MIN_TEMP,
                    WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherEntry.COLUMN_WIND_SPEED,
                    WeatherEntry.COLUMN_WEATHER_ID
            };

            Cursor weatherCursor = db.query(WeatherEntry.TABLE_NAME,
                    weatherColumns,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (weatherCursor.moveToFirst()) {

                int dateIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DATETEXT);
                String date = weatherCursor.getString(dateIndex);

                int degreeIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_DEGREES);
                double degree = weatherCursor.getDouble(degreeIndex);

                int humidityIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
                double humidity = weatherCursor.getDouble(humidityIndex);

                int pressureIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
                double pressure = weatherCursor.getDouble(pressureIndex);

                int maxTempIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
                double maxTemp = weatherCursor.getDouble(maxTempIndex);

                int minTempIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
                double minTemp = weatherCursor.getDouble(minTempIndex);

                int descIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC);
                String desc = weatherCursor.getString(descIndex);

                int windSpeedIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED);
                double windSpeed = weatherCursor.getDouble(windSpeedIndex);

                int weatherIdIndex = weatherCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID);
                int weatherId = weatherCursor.getInt(weatherIdIndex);
            } else {
                fail("No weather data returned!");
            }

            weatherCursor.close();
        } else {
            fail("No values returned :(");
        }



        dbHelper.close();
    }
}
