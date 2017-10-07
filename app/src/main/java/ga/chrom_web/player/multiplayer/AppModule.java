package ga.chrom_web.player.multiplayer;

import java.net.URI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.socket.client.IO;
import io.socket.client.Socket;

@Module
public class AppModule {

    private static final String HOST = "http://player.chrom-web.ga:8080";
//    private static final String HOST = "http://93.175.221.13:8080";

    @Provides
    @Singleton
    public Socket providesSocket() {
        return IO.socket(URI.create(HOST));
    }

    @Singleton
    @Provides
    public PlayerManager providesPlayerManager() {
        return new PlayerManager();
    }

    @Singleton
    @Provides
    public ConnectionManager providesConnectionManager() {
        return new ConnectionManager();
    }

    @Singleton
    @Provides
    public ChatManager providesChatManager() {
        return new ChatManager();
    }

    @Singleton
    @Provides
    public SharedPreferenceHelper providesSharedPreferenceHelper() {
        return new SharedPreferenceHelper();
    }
}
