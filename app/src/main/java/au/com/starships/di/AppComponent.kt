package au.com.starships.di

import au.com.starships.service.AppSettings
import au.com.starships.service.RestApiFacade
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RestModule::class, PrefModule::class])
public interface AppComponent {
    fun inject(instance: RestApiFacade)
    fun inject(instance: AppSettings)
}