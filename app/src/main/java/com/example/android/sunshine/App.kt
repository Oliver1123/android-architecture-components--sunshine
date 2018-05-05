package com.example.android.sunshine

import android.app.Application
import timber.log.Timber.DebugTree
import timber.log.Timber



public class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initTimber()
    }

    private fun initTimber() {
        Timber.plant(DebugTree())
    }
}