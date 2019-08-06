package com.dev.aaditya.homeautomation

import android.app.Application
import uk.co.chrisjenx.calligraphy.CalligraphyConfig



class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/century_gothic.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
        //....
    }
}