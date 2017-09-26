package ga.chrom_web.player.multiplayer;


import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class PlayerManager extends Manager {

    private static final String EVENT_PLAY = "play";
    private static final String EVENT_PAUSE = "pause";
    private static final String EVENT_REWIND = "rewind";
    private static final String EVENT_VIDEO_TIME = "video_time";
    private static final String EVENT_LIGHT = "light";

    private PlayerListener playerListener;

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    void subscribeOnEvents() {
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
        socket.on(EVENT_LIGHT, args -> {
            if (playerListener != null) {
                playerListener.onLightToggle(JsonUtil.parseToggleLight(args[0]));
            }
        });
    }

    public void play() {
        printEmit(EVENT_PLAY);
        socket.emit(EVENT_PLAY, new Object(), (Ack) args -> {
            System.out.println("EMIT: PLAY CONFIRMED");
        });
    }

    public void pause() {
        printEmit(EVENT_PAUSE);
        socket.emit(EVENT_PAUSE, new Object());
    }

    public void rewind(int secondsFromStart) {
        printEmit(EVENT_REWIND);
        HashMap<String, String> map = new HashMap<>();
        map.put("second", String.valueOf(secondsFromStart));
        socket.emit(EVENT_REWIND, new JSONObject(map));
    }

    /**
     * Asks server for current video and time
     */
    public void videoTime() {
        printEmit(EVENT_VIDEO_TIME);
        socket.emit(EVENT_VIDEO_TIME, new Object());
    }

    public void toggleLight() {
        printEmit(EVENT_LIGHT);
        socket.emit(EVENT_LIGHT, new Object());
    }

    public interface PlayerListener {

        void onPlay(VideoData playData);

        void onPause();

        void onRewind(int rewindTo);

        void onLightToggle(boolean isWhite);
    }

}
