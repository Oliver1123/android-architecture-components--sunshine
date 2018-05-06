/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.ui.list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.sunshine.R
import com.example.android.sunshine.data.database.WeatherEntry
import com.example.android.sunshine.utilities.SunshineDateUtils
import com.example.android.sunshine.utilities.SunshineWeatherUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.forecast_list_item.*
import java.util.*

/**
 * Creates a ForecastAdapter.
 *
 * @param context      Used to talk to the UI and app resources
 * @param callback The on-click handler for this adapter. This single handler is called
 * when an item is clicked.
 */
internal class ForecastAdapter(
        private val context: Context,
        private val callback: (date: Date) -> Unit) : RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>() {


    private val VIEW_TYPE_TODAY = 0
    private val VIEW_TYPE_FUTURE_DAY = 1
    /*
         * Flag to determine if we want to use a separate view for the list item that represents
         * today. This flag will be true when the phone is in portrait mode and false when the phone
         * is in landscape. This flag will be set in the constructor of the adapter by accessing
         * boolean resources.
         */
    private val useTodayLayout: Boolean = context.resources.getBoolean(R.bool.use_today_layout)
    private var forecast: MutableList<WeatherEntry> = mutableListOf()

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
     * can use this viewType integer to provide a different layout. See
     * [android.support.v7.widget.RecyclerView.Adapter.getItemViewType]
     * for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ForecastAdapterViewHolder {

        val layoutId = getLayoutIdByType(viewType)
        val view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false)
        view.isFocusable = true
        return ForecastAdapterViewHolder(view)
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder The ViewHolder which should be updated to represent the
     * contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ForecastAdapterViewHolder, position: Int) {
        holder.bind(position, forecast[position])
    }

    /**
     * Converts the weather icon id from Open Weather to the local image resource id. Returns the
     * correct image based on whether the forecast is for today(large image) or the future(small image).
     *
     * @param weatherIconId Open Weather icon id
     * @param position      Position in list
     * @return Drawable image resource id for weather
     */
    private fun getImageResourceId(weatherIconId: Int, position: Int): Int {
        val viewType = getItemViewType(position)

        return when (viewType) {
            VIEW_TYPE_TODAY -> SunshineWeatherUtils
                    .getLargeArtResourceIdForWeatherCondition(weatherIconId)
            VIEW_TYPE_FUTURE_DAY -> SunshineWeatherUtils
                    .getSmallArtResourceIdForWeatherCondition(weatherIconId)
            else -> throw IllegalArgumentException("Invalid view type, value of $viewType")
        }
    }

    override fun getItemCount(): Int {
        return forecast.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (useTodayLayout && position == 0) {
            VIEW_TYPE_TODAY
        } else {
            VIEW_TYPE_FUTURE_DAY
        }
    }

    /**
     * Swaps the list used by the ForecastAdapter for its weather data. This method is called by
     * [MainActivity] after a load has finished. When this method is called, we assume we have
     * a new set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newForecast the new list of forecasts to use as ForecastAdapter's data source
     */
    fun swapForecast(newForecast: List<WeatherEntry>) {
        forecast.clear()
        forecast.addAll(newForecast)
        notifyDataSetChanged()
    }

    private fun getLayoutIdByType(viewType: Int): Int {
        return when (viewType) {
            VIEW_TYPE_TODAY -> R.layout.list_item_forecast_today
            VIEW_TYPE_FUTURE_DAY -> R.layout.forecast_list_item
            else -> throw IllegalArgumentException("Invalid view type, value of $viewType")
        }
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a forecast item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    internal inner class ForecastAdapterViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(index: Int, weatherEntry: WeatherEntry) {
            val (weatherIconId, date, lowInCelsius, highInCelsius) = weatherEntry

            val weatherImageResourceId = getImageResourceId(weatherIconId, index)
            weather_icon.setImageResource(weatherImageResourceId)

            /****************
             * Weather Date *
             */
            val dateInMillis = date?.time ?: 0
            /* Get human readable string using our utility method */
            val dateString = SunshineDateUtils.getFriendlyDateString(context, dateInMillis, false)

            /* Display friendly date string */
            date_text.text = dateString

            /***********************
             * Weather Description *
             */
            val description = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherIconId)
            /* Create the accessibility (a11y) String from the weather description */
            val descriptionA11y = context.getString(R.string.a11y_forecast, description)

            /* Set the text and content description (for accessibility purposes) */
            weather_description.text = description
            weather_description.contentDescription = descriptionA11y

/*
          * If the user's preference for weather is fahrenheit, formatTemperature will convert
          * the temperature. This method will also append either °C or °F to the temperature
          * String.
          */
            val highString = SunshineWeatherUtils.formatTemperature(context, highInCelsius)
            /* Create the accessibility (a11y) String from the weather description */
            val highA11y = context.getString(R.string.a11y_high_temp, highString)

            /* Set the text and content description (for accessibility purposes) */
            high_temperature.text = highString
            high_temperature.contentDescription = highA11y

/*
          * If the user's preference for weather is fahrenheit, formatTemperature will convert
          * the temperature. This method will also append either °C or °F to the temperature
          * String.
          */
            val lowString = SunshineWeatherUtils.formatTemperature(context, lowInCelsius)
            val lowA11y = context.getString(R.string.a11y_low_temp, lowString)

            /* Set the text and content description (for accessibility purposes) */
            low_temperature.text = lowString
            low_temperature.contentDescription = lowA11y
        }
    }
}