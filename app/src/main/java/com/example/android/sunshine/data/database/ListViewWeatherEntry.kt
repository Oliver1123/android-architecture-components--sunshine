package com.example.android.sunshine.data.database

import java.util.*

data class ListViewWeatherEntry(
        var weatherIconId: Int,
        var date: Date?,
        var min: Double,
        var max: Double
)