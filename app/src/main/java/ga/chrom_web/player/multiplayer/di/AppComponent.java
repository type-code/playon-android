package ga.chrom_web.player.multiplayer.di;

import javax.inject.Singleton;

import dagger.Component;
import ga.chrom_web.player.multiplayer.ChatManager;
import ga.chrom_web.player.multiplayer.ConnectionManager;
import ga.chrom_web.player.multiplayer.Manager;
import ga.chrom_web.player.multiplayer.PlayerManager;
import ga.chrom_web.player.multiplayer.ui.player.PlayerViewModel;
import ga.chrom_web.player.multiplayer.SharedPreferenceHelper;
import ga.chrom_web.player.multiplayer.ui.signin.LoginViewModel;

@Singleton
@Component(modules = {AppModule.class, AndroidModule.class})
public interface AppComponent {
    void inject(PlayerManager playerManager);

    void inject(ConnectionManager connectionManager);

    void inject(ChatManager chatManager);

    void inject(Manager manager);

    void inject(PlayerViewModel playerViewModel);

    void inject(SharedPreferenceHelper sharedPreferenceHelper);

    void inject(LoginViewModel loginViewModel);
}
