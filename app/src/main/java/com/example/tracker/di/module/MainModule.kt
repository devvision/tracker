package com.example.tracker.di.module

import androidx.lifecycle.ViewModel
import com.example.tracker.di.annotations.ActivityScope
import com.example.tracker.di.annotations.FragmentScope
import com.example.tracker.di.annotations.ViewModelKey
import com.example.tracker.ui.MainActivity
import com.example.tracker.ui.MainFragment
import com.example.tracker.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindMainFragment(): MainFragment

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}