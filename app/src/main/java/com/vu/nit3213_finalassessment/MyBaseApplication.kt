package com.vu.nit3213_finalassessment

import android.app.Application
import android.util.Log

class MyBaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("Testlfc", "Application class initialised: ")
    }
}