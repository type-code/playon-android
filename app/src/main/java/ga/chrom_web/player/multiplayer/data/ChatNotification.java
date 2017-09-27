package ga.chrom_web.player.multiplayer.data;


import android.content.Context;
import android.graphics.Color;

import org.jetbrains.annotations.Nullable;

import ga.chrom_web.player.multiplayer.Manager;
import ga.chrom_web.player.multiplayer.R;

public class ChatNotification extends ChatItem {

    private static final String COLOR_JOIN = "#8e44ad";
    private static final String COLOR_LOAD = "#2980b9";
    private static final String COLOR_PAUSE = "#e67e22";
    private static final String COLOR_PLAY = "#1f9c3b";
    private static final String COLOR_REWIND = "#16a085";
    private static final String COLOR_DISCONNECT = "#34495e";

    private String event;
    @Nullable
    private String additionalInfo;

    public ChatNotification() {
    }

    public ChatNotification(String nick, String event) {
        this.nick = nick;
        this.event = event;
    }

    public ChatNotification(String nick, String event, @Nullable String additionalInfo) {
        this.event = event;
        this.nick = nick;
        this.additionalInfo = additionalInfo;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getFullNotification(Context context) {
        // TODO: think about it
        switch (event) {
            case Manager.EVENT_JOIN:
                return nick + " " + context.getString(R.string.chat_notification_joined);
            case Manager.EVENT_LOAD:
                return nick + " " + context.getString(R.string.chat_notification_load);
            case Manager.EVENT_PAUSE:
                return nick + " " + context.getString(R.string.chat_notification_pause);
            case Manager.EVENT_PLAY:
                return nick + " " + context.getString(R.string.chat_notification_play);
            case Manager.EVENT_REWIND:
                return nick + " " + context.getString(R.string.chat_notification_rewind) + " " + additionalInfo;
            case Manager.EVENT_DISCONNECT:
                return nick + " " + context.getString(R.string.chat_notification_disconnect);
            default:
                return nick + " " + event;
        }
    }

    public int getColor() {
        switch (event) {
            case Manager.EVENT_JOIN:
                return Color.parseColor(COLOR_JOIN);
            case Manager.EVENT_LOAD:
                return Color.parseColor(COLOR_LOAD);
            case Manager.EVENT_PAUSE:
                return Color.parseColor(COLOR_PAUSE);
            case Manager.EVENT_PLAY:
                return Color.parseColor(COLOR_PLAY);
            case Manager.EVENT_REWIND:
                return Color.parseColor(COLOR_REWIND);
            case Manager.EVENT_DISCONNECT:
                return Color.parseColor(COLOR_DISCONNECT);
            default:
                return Color.WHITE;
        }
    }

}
