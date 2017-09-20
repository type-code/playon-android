package ga.chrom_web.player.multiplayer;

import android.app.Application;


public class App extends Application {

    public static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = buildComponent();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder().build();
    }

    public static AppComponent getComponent() {
        return component;
    }
}
