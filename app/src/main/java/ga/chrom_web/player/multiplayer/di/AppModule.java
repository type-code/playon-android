package ga.chrom_web.player.multiplayer.di;

import java.net.URI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ga.chrom_web.player.multiplayer.ChatSocketManager;
import ga.chrom_web.player.multiplayer.ConnectionSocketManager;
import ga.chrom_web.player.multiplayer.PlayerSocketManager;
import ga.chrom_web.player.multiplayer.SharedPreferenceHelper;
import io.socket.client.IO;
import io.socket.client.Socket;

@Module
public class AppModule {

    private static final String HOST = "http://player.chrom-web.ga:8080";
//    private static final String HOST = "http://93.175.221.13:8080";
//    private static final String HOST = "http://1:1:1:1:1080";

    @Provides
    @Singleton
    public Socket providesSocket() {
        return IO.socket(URI.create(HOST));
    }

    @Singleton
    @Provides
    public PlayerSocketManager providesPlayerManager() {
        return new PlayerSocketManager();
    }

    @Singleton
    @Provides
    public ConnectionSocketManager providesConnectionManager() {
        return new ConnectionSocketManager();
    }

    @Singleton
    @Provides
    public ChatSocketManager providesChatManager() {
        return new ChatSocketManager();
    }

    @Singleton
    @Provides
    public SharedPreferenceHelper providesSharedPreferenceHelper() {
        return new SharedPreferenceHelper();
    }
}
