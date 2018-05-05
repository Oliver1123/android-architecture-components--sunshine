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

package com.example.android.sunshine.data

import com.example.android.sunshine.AppExecutors
import com.example.android.sunshine.data.database.WeatherDao
import com.example.android.sunshine.data.network.WeatherNetworkDataSource
import timber.log.Timber


/**
 * Handles data operations in Sunshine. Acts as a mediator between [WeatherNetworkDataSource]
 * and [WeatherDao]
 */
class SunshineRepository private constructor(
        private val weatherDao: WeatherDao,
        private val weatherNetworkDataSource: WeatherNetworkDataSource,
        private val executors: AppExecutors) {

    private var isInitialized = false


    init {
        val networkData = weatherNetworkDataSource.getCurrentWeatherForecasts()
        networkData.observeForever { newForecastsFromNetwork ->
            executors.diskIO().execute {
                // Insert our new weather data into Sunshine's database
                if (newForecastsFromNetwork != null) {
                    weatherDao.bulkInsert(*newForecastsFromNetwork)
                    Timber.d("New values inserted")
                }
            }
        }
    }

    /**
     * Checks if there are enough days of future weather for the app to display all the needed data.
     *
     * @return Whether a fetch is needed
     */
    private fun isFetchNeeded(): Boolean {
        // TODO Finish this method when instructed
        return true
    }


    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    @Synchronized
    fun initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (isInitialized) return
        isInitialized = true

        startFetchWeatherService()
    }

    /**
     * Database related operations
     */

    /**
     * Deletes old weather data because we don't need to keep multiple days' data
     */
    private fun deleteOldData() {
        // TODO Finish this method when instructed
    }

    /**
     * Network related operation
     */

    private fun startFetchWeatherService() {
        weatherNetworkDataSource.startFetchWeatherService()
    }

    companion object {
        @Volatile private var INSTANCE: SunshineRepository? = null
        private val LOCK = Any()

        fun getInstance(weatherDao: WeatherDao, weatherNetworkDataSource: WeatherNetworkDataSource,
                        executors: AppExecutors): SunshineRepository {
            return  INSTANCE ?: synchronized(LOCK) {
                INSTANCE ?: SunshineRepository(weatherDao, weatherNetworkDataSource,
                        executors).also { INSTANCE = it }
            }
        }
    }

}