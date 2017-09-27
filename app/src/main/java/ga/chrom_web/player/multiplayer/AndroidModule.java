package ga.chrom_web.player.multiplayer;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
}
