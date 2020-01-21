package com.example.tracker.di.module

import com.example.tracker.di.annotations.ServiceScope
import com.example.tracker.ui.TrackingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

    @ServiceScope
    @ContributesAndroidInjector
    abstract fun bindTrackingService(): TrackingService
}