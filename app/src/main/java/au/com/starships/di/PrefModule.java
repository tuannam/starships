package au.com.starships.di;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PrefModule {
    public static final String SHARED_PREFERENCES_NAME = "STAR_SHIPS";

    private Context context;

    public PrefModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    SharedPreferences providePreference() {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
