package com.bizilabs.streeek.lib.presentation

import android.app.Application
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

abstract class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initKoin()
    }

    private fun initTimber(){
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun initKoin(){
        startKoin {}
    }

}