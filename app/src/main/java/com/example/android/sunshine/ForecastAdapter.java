package com.example.android.sunshine;

/**
 * Created by seaiaw on 25/10/14.
 */
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.example.android.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private boolean mUseTodayLayout;

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0  && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        // Determine layoutId from viewType
        if (viewType == VIEW_TYPE_TODAY)
            layoutId = R.layout.list_item_forecast_today;
        else if (viewType == VIEW_TYPE_FUTURE_DAY)
            layoutId = R.layout.list_item_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        Log.d("ForecastAdapter", "WeatherId: " + weatherId);
        // Use placeholder image for now
        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                viewHolder.iconView.setImageResource(
                        Utility.getArtResourceForWeatherCondition(weatherId));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                viewHolder.iconView.setImageResource(
                        Utility.getIconResourceForWeatherCondition(weatherId));
                break;
            }
        }

        // Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        viewHolder.descriptionView.setText(description);

        // Improve accessibility, voice over description on the image.
        viewHolder.iconView.setContentDescription(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        // Find TextView and set formatted high temperature on it
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        // Find TextView and set formatted low temperature on it
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
    }

    /**
     * Cache for children views for a forecast list item
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView descriptionView;
        public final TextView dateView;
        public final TextView lowTempView;
        public final TextView highTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
        }
    }
}