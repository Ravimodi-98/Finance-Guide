package com.example.financeguide

import android.app.Application
import com.example.financeguide.di.AppContainer

class FinanceGuideApp : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
