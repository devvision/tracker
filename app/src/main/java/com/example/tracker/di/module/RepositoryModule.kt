package com.example.tracker.di.module

import com.example.tracker.data.repo.ExerciseRepository
import com.example.tracker.data.repo.ExerciseRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindExerciseRepository(repo: ExerciseRepositoryImpl): ExerciseRepository
}