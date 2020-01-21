package com.example.tracker.di.component

import android.app.Application
import com.example.tracker.base.BaseApplication
import com.example.tracker.di.annotations.AppScope
import com.example.tracker.di.module.*
import com.example.tracker.ui.TrackingService
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    AndroidSupportInjectionModule::class,
    AppModule::class,
    MainModule::class,
    DataSourceModule::class,
    RepositoryModule::class,
    ServiceModule::class))
interface ApplicationComponent : AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}