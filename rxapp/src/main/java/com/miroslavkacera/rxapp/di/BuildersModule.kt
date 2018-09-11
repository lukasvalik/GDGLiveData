package com.miroslavkacera.rxapp.di

import com.miroslavkacera.rxapp.MainActivity
import com.miroslavkacera.rxapp.ui.presentation.HotelsFragment
import com.miroslavkacera.rxapp.ui.reserve.ReserveFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun bindMyActivity() : MainActivity

    @ContributesAndroidInjector()
    abstract fun bindHotelsFragment(): HotelsFragment

    @ContributesAndroidInjector()
    abstract fun bindReserveFragment(): ReserveFragment
}