package com.miroslavkacera.rxapp.di

import android.content.Context
import com.miroslavkacera.rxapp.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideContext(app: App): Context = app.applicationContext
}