package ga.chrom_web.player.multiplayer;


import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import ga.chrom_web.player.multiplayer.data.VideoData;
import io.socket.client.Ack;

public class PlayerManager extends Manager {

    private static final Object EMPTY_ARGS = new Object();

    private PlayerListener playerListener;

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    void subscribeOnEvents() {
        socket.on(EVENT_PLAY, args -> {
            if (playerListener != null) {
                playerListener.onPlay(JsonUtil.jsonToObject(args[0], VideoData.class));
            }
        });
        socket.on(EVENT_PAUSE, args -> {
            if (playerListener != null) {
                playerListener.onPause(JsonUtil.jsonToObject(args[0], VideoData.class));
            }
        });
        socket.on(EVENT_REWIND, args -> {
            if (playerListener != null) {
                playerListener.onRewind(JsonUtil.jsonToObject(args[0], VideoData.class));
            }
        });
        socket.on(EVENT_LIGHT, args -> {
            if (playerListener != null) {
                playerListener.onLightToggle(JsonUtil.parseToggleLight(args[0]));
            }
        });
        socket.on(EVENT_LOAD, args -> {
            if (playerListener != null) {
                playerListener.onNewVideoLoaded(JsonUtil.jsonToObject(args[0], VideoData.class));
            }
        });
    }

    public void play() {
        printEmit(EVENT_PLAY);
        socket.emit(EVENT_PLAY, EMPTY_ARGS);
    }

    public void pause() {
        printEmit(EVENT_PAUSE);
        socket.emit(EVENT_PAUSE, EMPTY_ARGS);
    }

    public void rewind(int secondsFromStart) {
        printEmit(EVENT_REWIND);
        HashMap<String, String> map = new HashMap<>();
        map.put("time", String.valueOf(secondsFromStart));
        socket.emit(EVENT_REWIND, new JSONObject(map));
    }

    public void load(String link) {
        // TODO: argument [boolean] playlist
        printEmit(EVENT_LOAD);
        HashMap<String, String> map = new HashMap<>();
        map.put("link", link);
        map.put("playlist", "false");
        socket.emit(EVENT_LOAD, new JSONObject(map));

    }

    /**
     * Asks server for current video and time
     */
    public void videoTime() {
        printEmit(EVENT_VIDEO_TIME);
        socket.emit(EVENT_VIDEO_TIME, EMPTY_ARGS);
    }

    public void toggleLight() {
        printEmit(EVENT_LIGHT);
        socket.emit(EVENT_LIGHT, EMPTY_ARGS);
    }

    public interface PlayerListener {

        void onPlay(VideoData videoData);

        void onPause(VideoData videoData);

        void onRewind(VideoData videoData);

        void onLightToggle(boolean isWhite);

        void onNewVideoLoaded(VideoData videoData);
    }

}
