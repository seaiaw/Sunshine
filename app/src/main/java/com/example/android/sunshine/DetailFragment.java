package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;

/**
 * Created by seaiaw on 26/10/14.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = com.example.android.sunshine.DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private static final String DATE_STRING = "DATE";
    private String mForecastStr;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    private ShareActionProvider mShareActionProvider;

    private ImageView mWeatherImageView;
    private TextView mDayTextView;
    private TextView mDateTextView;
    private TextView mForecastTextView;
    private TextView mHighTempTextView;
    private TextView mLowTempTextView;
    private TextView mHumidityTextView;
    private TextView mWindSpeedTextView;
    private TextView mPressureTextView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null && args.containsKey(DetailActivity.DATE_KEY)) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(DetailActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(DetailActivity.DATE_KEY)) {
            return null;
        }

        String date = getArguments().getString(DetailActivity.DATE_KEY);

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                Utility.getPreferredLocation(getActivity()), date);
        Log.v(LOG_TAG, weatherForLocationUri.toString());

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        int weatherId = data.getInt(data.getColumnIndex(
                                        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
        String dateString = Utility.formatDate(
                data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT)));
        String weatherDescription =
                data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));

        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(
                getActivity(),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)),
                                isMetric);
        String low = Utility.formatTemperature(
                getActivity(),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)),
                                isMetric);

        float humidity = data.getFloat(data.getColumnIndex(
                                        WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
        float pressure = data.getFloat(data.getColumnIndex(
                                        WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        float windSpeed = data.getFloat(data.getColumnIndex(
                                        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
        float degrees = data.getFloat(data.getColumnIndex(
                                        WeatherContract.WeatherEntry.COLUMN_DEGREES));

        mForecastStr = String.format("%s - %s - %s/%s",
                dateString, weatherDescription, high, low);

        Log.v(LOG_TAG, "Forecast String: " + mForecastStr);

        mWeatherImageView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        mDayTextView.setText(Utility.getDayName(getActivity(), dateString));
        mDateTextView.setText(dateString);
        mForecastTextView.setText(weatherDescription);
        mHighTempTextView.setText(high);
        mLowTempTextView.setText(low);
        mHumidityTextView.setText(Utility.getFormattedHumidity(getActivity(), humidity));
        mPressureTextView.setText(Utility.getFormattedPressure(getActivity(), pressure));
        mWindSpeedTextView.setText(Utility.getFormattedWind(getActivity(), windSpeed, degrees));

        // Improve accessibility, voice over description on the image.
        mWeatherImageView.setContentDescription(weatherDescription);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mWeatherImageView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDayTextView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDateTextView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mForecastTextView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempTextView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempTextView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityTextView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mPressureTextView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        mWindSpeedTextView = (TextView) rootView.findViewById(R.id.detail_windspeed_textview);
        return rootView;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat
                                                .getActionProvider(shareItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is NULL?!");
        }
    }
}
