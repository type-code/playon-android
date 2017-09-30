package ga.chrom_web.player.multiplayer;


import javax.inject.Inject;

import io.socket.client.Socket;

public abstract class Manager {

    public static final String EVENT_CONNECTED = "connected";
    public static final String EVENT_DISCONNECT = "disc";
    public static final String EVENT_JOIN = "join";


    public static final String EVENT_PLAY = "play";
    public static final String EVENT_PAUSE = "pause";
    public static final String EVENT_REWIND = "rewind";
    public static final String EVENT_VIDEO_TIME = "video_time";
    public static final String EVENT_LIGHT = "light";
    public static final String EVENT_LOAD = "light";

    public static final String EVENT_MESSAGE = "message";

    @Inject
    Socket socket;

    public Manager() {
        App.getComponent().inject(this);
        subscribeOnEvents();
    }

    abstract void subscribeOnEvents();

    protected void printEmit(String eventName) {
        System.out.println("EMIT " + eventName);
    }
}
