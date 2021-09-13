package au.com.starships

import android.app.Application
import au.com.starships.di.AppComponent
import au.com.starships.di.DaggerAppComponent
import au.com.starships.di.PrefModule
import au.com.starships.di.RestModule
import au.com.starships.service.AppSettings
import au.com.starships.service.RestApiFacade


class MainApplication: Application() {
    lateinit var appComponent: AppComponent

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

         appComponent = DaggerAppComponent.builder()
            .restModule(RestModule(this))
            .prefModule(PrefModule(this))
            .build()
        appComponent.inject(RestApiFacade.instance as RestApiFacade)
        appComponent.inject(AppSettings.instance)
    }
}