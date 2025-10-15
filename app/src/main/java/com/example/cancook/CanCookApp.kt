package com.example.cancook

import android.app.Application
import com.example.cancook.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CanCookApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CanCookApp)
            modules(
                listOf(
                    appModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}
