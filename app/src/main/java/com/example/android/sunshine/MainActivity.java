package com.example.android.sunshine;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager()
                                                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_map) {
            // Get postal code information from SharedPreferences
            String postalCode = Utility.getPreferredLocation(this);
            Uri geoQuery = Uri.parse("geo:0,0")
                            .buildUpon()
                            .appendQueryParameter("q", postalCode)
                            .build();

            Intent mapIntent = new Intent(Intent.ACTION_VIEW).setData(geoQuery);

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "No matching app to handle request.", Toast.LENGTH_LONG)
                        .show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(String date) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putString(DetailActivity.DATE_KEY, date);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                .replace(R.id.weather_detail_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();


        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DATE_KEY, date);
            startActivity(intent);
        }
    }
}
