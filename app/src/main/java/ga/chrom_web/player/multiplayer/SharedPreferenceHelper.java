package ga.chrom_web.player.multiplayer;


import android.content.SharedPreferences;
import android.graphics.Color;

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
        // TODO: maybe save color in String ???
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_NICK, nick);

        editor.putInt(PREF_COLOR, removeAlpha(color));
        editor.apply();
    }

    private int removeAlpha(int color) {
        String hexColor = Integer.toHexString(color);
        if (hexColor.length() <= 6) {
            return color;
        }
        String colorWithoutAlpha = "#" + hexColor.substring(2);
        return Color.parseColor(colorWithoutAlpha);
    }

    public String getNick() {
        return prefs.getString(PREF_NICK, "DEFAULT");
    }

    public int getColor() {
        return prefs.getInt(PREF_COLOR, 0);
    }

    public String getHexColor() {
        return "#" + Integer.toHexString(getColor());
    }
}
