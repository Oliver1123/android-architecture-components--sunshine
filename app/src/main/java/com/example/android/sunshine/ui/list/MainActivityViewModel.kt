package com.example.android.sunshine.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.android.sunshine.data.SunshineRepository
import com.example.android.sunshine.data.database.ListViewWeatherEntry


class MainActivityViewModel(
        repository: SunshineRepository) : ViewModel() {

    // Weather forecast the user is looking at
    val forecast: LiveData<List<ListViewWeatherEntry>> = repository.getCurrentWeatherForecasts()

}