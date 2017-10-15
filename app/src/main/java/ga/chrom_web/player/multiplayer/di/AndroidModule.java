package ga.chrom_web.player.multiplayer.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {

    private Application application;

    public AndroidModule(Application application) {
        this.application = application;
    }

    @Provides
    public SharedPreferences providesSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Named("internalDir")
    public File providesInternalDir() {
        return application.getFilesDir();
    }

    @Provides
    @Named("externalCacheDir")
    public File providesExternalCacheDir() {
        return application.getExternalCacheDir();
    }

}
