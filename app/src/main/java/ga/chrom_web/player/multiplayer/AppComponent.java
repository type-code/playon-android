package ga.chrom_web.player.multiplayer;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(PlayerManager playerManager);

    void inject(ConnectionManager connectionManager);

    void inject(ChatManager chatManager);

    void inject(Manager manager);
}
