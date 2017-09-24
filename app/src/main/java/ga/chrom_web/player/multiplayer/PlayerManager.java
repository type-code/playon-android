package ga.chrom_web.player.multiplayer;


import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class PlayerManager {

    private static final String EVENT_PLAY = "play";
    private static final String EVENT_PAUSE = "pause";
    private static final String EVENT_REWIND = "rewind";
    private static final String EVENT_VIDEO_TIME = "video_time";

    @Inject
    public Socket socket;

    private PlayerListener playerListener;

    public PlayerManager() {
        App.getComponent().inject(this);
        subscribeOnEvents();
    }

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    private void subscribeOnEvents() {
        socket.on(EVENT_PLAY, args -> {
            if (playerListener != null) {
                playerListener.onPlay(JsonUtil.parseOnPlay(args[0]));
            }
        });
        socket.on(EVENT_PAUSE, args -> {
            if (playerListener != null) {
                playerListener.onPause();
            }
        });
        socket.on(EVENT_REWIND, args -> {
            if (playerListener != null) {
                playerListener.onRewind(JsonUtil.parseRewind(args[0]));
            }
        });
    }

    public void play() {
        printEmit("PLAY");
        socket.emit(EVENT_PLAY, new Object(), (Ack) args -> {
            System.out.println("EMIT: PLAY CONFIRMED");
        });
    }

    public void pause() {
        printEmit("PAUSE");
        socket.emit(EVENT_PAUSE, new Object());
    }

    public void rewind(int secondsFromStart) {
        printEmit("REWIND");
        HashMap<String, String> map = new HashMap<>();
        map.put("second", String.valueOf(secondsFromStart));
        socket.emit(EVENT_REWIND, new JSONObject(map));
    }

    /**
     * Asks server for current video and time
     */
    public void videoTime() {
        printEmit("VIDEO TIME");
        socket.emit(EVENT_VIDEO_TIME, new Object());
    }

    public void printEmit(String eventName) {
        System.out.println("EMIT " + eventName);
    }

    public interface PlayerListener {

        void onPlay(VideoData playData);

        void onPause();

        void onRewind(int rewindTo);
    }

}
