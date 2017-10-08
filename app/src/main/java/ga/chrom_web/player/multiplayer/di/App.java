package ga.chrom_web.player.multiplayer.di;

import android.app.Application;



public class App extends Application {

    public static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: turn off crashlytics for a while
//        Fabric.with(this, new Crashlytics());
        component = buildComponent();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();
    }

    public static AppComponent getComponent() {
        return component;
    }
}
