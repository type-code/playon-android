package ga.chrom_web.player.multiplayer;


import android.content.SharedPreferences;

import javax.inject.Inject;

public class SharedPreferenceHelper {

    private static final String PREF_NICK = "nick";
    private static final String PREF_COLOR = "color";

    @Inject
    SharedPreferences prefs;

    public SharedPreferenceHelper() {
         App.getComponent().inject(this);
    }

    public void saveUser(String nick, int color) {
        // TODO: save color without alpha
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_NICK, nick);
        editor.putInt(PREF_COLOR, color);
        editor.apply();
    }

    public String getNick() {
        return prefs.getString(PREF_NICK, "DEFAULT");
    }

    public int getColor() {
        return prefs.getInt(PREF_COLOR, 0);
    }
}
