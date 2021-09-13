package au.com.starships.di

import android.app.Application
import android.os.Build
import androidx.viewbinding.BuildConfig
import au.com.starships.model.Ship
import au.com.starships.rest.ShipSerializer
import au.com.starships.service.RestApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class RestModule(val application: Application) {
    @Provides
    @Singleton
    fun application(): Application {
        return application
    }

    @Provides
    @Singleton
    @Named("baseOkhttp3")
    fun providesOkHttpClient3(): OkHttpClient {
        val builder = okhttp3.OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(logging)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    @Named("gson")
    fun providesGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Ship::class.java, ShipSerializer())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
    }

    @Provides
    @Singleton
    @Named("baseRetrofit")
    fun provideBaseRetrofit(
        @Named("baseOkhttp3") okHttpClient: OkHttpClient?,
        @Named("gson") gson: Gson?
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://swapi.dev/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesRestApiService(@Named("baseRetrofit") retrofit: Retrofit): RestApiService {
        return retrofit.create(RestApiService::class.java)
    }
}