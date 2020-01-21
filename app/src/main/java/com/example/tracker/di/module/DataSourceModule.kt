package com.example.tracker.di.module

import android.content.Context
import androidx.room.Room
import com.example.tracker.data.locadatasource.LocalDataSource
import com.example.tracker.data.localdb.AppDb
import com.example.tracker.data.preference.AppPreference
import com.example.tracker.data.preference.AppPreferenceImpl
import com.example.tracker.di.annotations.AppScope
import com.example.tracker.di.annotations.ApplicationContext
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class DataSourceModule {

    @Binds
    abstract fun provideAppPreference(preference: AppPreferenceImpl): AppPreference

    @Module
    companion object {

        @Provides
        @Singleton
        @JvmStatic
        fun provideLocalDb(@ApplicationContext context: Context): AppDb {
            return Room.databaseBuilder(context, AppDb::class.java, "app_database")
                .build()
        }

//        @Provides
//        fun provideLocalDataSource(db: AppDb): LocalDataSource {
//            return LocalDataSource(db)
//        }
    }
}