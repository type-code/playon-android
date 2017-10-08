package ga.chrom_web.player.multiplayer.ui.signin;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Color;

import javax.inject.Inject;

import ga.chrom_web.player.multiplayer.di.App;
import ga.chrom_web.player.multiplayer.SharedPreferenceHelper;


public class LoginViewModel extends AndroidViewModel {

    @Inject
    SharedPreferenceHelper prefs;
    public MutableLiveData<Integer> color;

    public LoginViewModel(Application application) {
        super(application);
        App.getComponent().inject(this);
        color = new MutableLiveData<>();
        color.setValue(Color.BLACK);
    }

    public void login(String nick) {
        if (!checkLogin(nick)) {
            return;
        }
        prefs.saveUser(nick, color.getValue());
    }

    public boolean checkLogin(String nick) {
        if (nick == null || nick.isEmpty()) {
            return false;
        }
        // TODO: add more conditions
        return true;
    }

    public void setColor(int color) {
        this.color.setValue(color);
    }

    public MutableLiveData<Integer> getColor() {
        return color;
    }
}
