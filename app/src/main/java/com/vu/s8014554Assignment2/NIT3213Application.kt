package com.vu.s8014554Assignment2

import android.app.Application
import android.util.Log

class NIT3213Application: Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("Testlfc", "Application class initialised: ")
    }
}