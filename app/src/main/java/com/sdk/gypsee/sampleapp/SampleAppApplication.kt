package com.sdk.gypsee.sampleapp

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class SampleAppApplication:Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}