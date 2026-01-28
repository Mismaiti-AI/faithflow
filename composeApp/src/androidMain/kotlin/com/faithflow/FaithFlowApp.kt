package com.faithflow

import android.app.Application
import co.touchlab.kermit.Logger

class FaithFlowApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.withTag("FaithFlowApp").d("onCreate")
    }
}