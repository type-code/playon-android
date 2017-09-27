package ga.chrom_web.player.multiplayer;

import javax.inject.Singleton;

import dagger.Component;
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
