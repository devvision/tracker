package com.example.tracker.di.module

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.tracker.data.preference.AppPreference
import com.example.tracker.data.preference.AppPreferenceImpl
import com.example.tracker.di.annotations.AppScope
import com.example.tracker.di.annotations.ApplicationContext
import com.example.tracker.utils.ViewModelFactory
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    @Singleton
    @ApplicationContext
    abstract fun provideContext(application: Application): Context

    @Binds
    abstract fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}