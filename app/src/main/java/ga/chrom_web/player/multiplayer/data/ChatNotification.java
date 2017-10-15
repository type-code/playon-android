package ga.chrom_web.player.multiplayer.data;


import android.content.Context;
import android.graphics.Color;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import ga.chrom_web.player.multiplayer.SocketManager;
import ga.chrom_web.player.multiplayer.R;

public class ChatNotification extends ChatItem implements Serializable {

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

    public ChatNotification(String nick, String event, @Nullable String additionalInfo) {
        this.event = event;
        this.setNick(nick);
        this.additionalInfo = additionalInfo;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getFullNotification(Context context) {
        // TODO: think about it
        switch (event) {
            case SocketManager.EVENT_JOIN:
                return getNick() + " " + context.getString(R.string.chat_notification_joined);
            case SocketManager.EVENT_LOAD:
                return getNick() + " " + context.getString(R.string.chat_notification_load);
            case SocketManager.EVENT_PAUSE:
                return getNick() + " " + context.getString(R.string.chat_notification_pause);
            case SocketManager.EVENT_PLAY:
                return getNick() + " " + context.getString(R.string.chat_notification_play);
            case SocketManager.EVENT_REWIND:
                return getNick() + " " + context.getString(R.string.chat_notification_rewind) + " " + additionalInfo;
            case SocketManager.EVENT_DISCONNECT:
                return getNick() + " " + context.getString(R.string.chat_notification_disconnect);
            default:
                return getNick() + " " + event;
        }
    }

    public int getColor() {
        switch (event) {
            case SocketManager.EVENT_JOIN:
                return Color.parseColor(COLOR_JOIN);
            case SocketManager.EVENT_LOAD:
                return Color.parseColor(COLOR_LOAD);
            case SocketManager.EVENT_PAUSE:
                return Color.parseColor(COLOR_PAUSE);
            case SocketManager.EVENT_PLAY:
                return Color.parseColor(COLOR_PLAY);
            case SocketManager.EVENT_REWIND:
                return Color.parseColor(COLOR_REWIND);
            case SocketManager.EVENT_DISCONNECT:
                return Color.parseColor(COLOR_DISCONNECT);
            default:
                return Color.WHITE;
        }
    }

    @Override
    public String toString() {
        return "ChatNotification{" +
                "event='" + event + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
