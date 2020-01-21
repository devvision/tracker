package com.example.tracker.di.annotations

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerActivity

@Scope
annotation class FragmentScope

@Scope
annotation class AppScope

@Scope
annotation class ActivityScope

@Scope
annotation class ServiceScope