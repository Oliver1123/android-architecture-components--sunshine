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

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.android.sunshine.R
import com.example.android.sunshine.ui.detail.DetailActivity
import kotlinx.android.synthetic.main.activity_forecast.*
import java.util.*


/**
 * Displays a list of the next 14 days of forecasts
 */
class MainActivity : AppCompatActivity() {

    private lateinit var forecastAdapter: ForecastAdapter

    private val position = RecyclerView.NO_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)


        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerview_forecast.layoutManager = layoutManager
        recyclerview_forecast.setHasFixedSize(true)
        forecastAdapter = ForecastAdapter(this@MainActivity, this::onItemClick)

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        recyclerview_forecast.adapter = forecastAdapter
        showLoading()
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param date Date of forecast
     */
    fun onItemClick(date: Date) {
        val weatherDetailIntent = Intent(this@MainActivity, DetailActivity::class.java)
        val timestamp = date.time
        weatherDetailIntent.putExtra(DetailActivity.WEATHER_ID_EXTRA, timestamp)
        startActivity(weatherDetailIntent)
    }

    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private fun showWeatherDataView() {
        // First, hide the loading indicator
        pb_loading_indicator.visibility = View.INVISIBLE
        // Finally, make sure the weather data is visible
        recyclerview_forecast.visibility = View.VISIBLE
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private fun showLoading() {
        // Then, hide the weather data
        recyclerview_forecast.visibility = View.INVISIBLE
        // Finally, show the loading indicator
        pb_loading_indicator.visibility = View.VISIBLE
    }
}
