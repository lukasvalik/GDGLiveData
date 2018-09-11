package com.miroslavkacera.rxapp.di

import com.miroslavkacera.rxapp.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    BuildersModule::class,
    HotelsRepositoryModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder
        fun build(): AppComponent
    }

    fun inject(app: App)
}
