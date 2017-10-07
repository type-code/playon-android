package ga.chrom_web.player.multiplayer;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ga.chrom_web.player.multiplayer.data.VideoData;

public class PlayerManager extends Manager {

    private static final Object EMPTY_ARGS = new Object();

    private PlayerListener playerListener;

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    protected void subscribeOnEvents() {
        getSocket().on(EVENT_PLAY, args -> {
            Utils.debugLog("PLAY: " + args[0]);
            if (playerListener != null) {
                playerListener.onPlay(JsonUtil.jsonToObject(args[0], VideoData.class));
            }
        });
        getSocket().on(EVENT_PAUSE, args -> {
            Utils.debugLog("PAUSE: " + args[0]);
            if (playerListener != null) {
                playerListener.onPause(JsonUtil.jsonToObject(args[0], VideoData.class));
            }
        });
        getSocket().on(EVENT_REWIND, args -> {
            Utils.debugLog("REWIND: " + args[0]);
            if (playerListener != null) {
                playerListener.onRewind(JsonUtil.jsonToObject(args[0], VideoData.class));
            }
        });
        getSocket().on(EVENT_LIGHT, args -> {
            Utils.debugLog("LIGHT CHANGED: " + args[0]);
            if (playerListener != null) {
                playerListener.onLightToggle(JsonUtil.parseToggleLight(args[0]));
            }
        });
        getSocket().on(EVENT_LOAD, args -> {
            Utils.debugLog("New video loaded: " + args[0]);
            if (playerListener != null) {
                playerListener.onNewVideoLoaded(JsonUtil.jsonToObject(args[0], VideoData.class));
            }
        });
    }

    public void play() {
        printEmit(EVENT_PLAY);
        getSocket().emit(EVENT_PLAY, EMPTY_ARGS);
    }

    public void pause() {
        printEmit(EVENT_PAUSE);
        getSocket().emit(EVENT_PAUSE, EMPTY_ARGS);
    }

    public void rewind(int secondsFromStart) {
        printEmit(EVENT_REWIND);
        HashMap<String, String> map = new HashMap<>();
        map.put("time", String.valueOf(secondsFromStart));
        getSocket().emit(EVENT_REWIND, new JSONObject(map));
    }

    public void loadVideo(String link) {
        // TODO: argument [boolean] playlist
        printEmit(EVENT_LOAD);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("link", link);
            jsonObject.put("playlist", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSocket().emit(EVENT_LOAD, jsonObject);

    }

    /**
     * Asks server for current video and time
     */
    public void videoTime() {
        printEmit(EVENT_VIDEO_TIME);
        getSocket().emit(EVENT_VIDEO_TIME, EMPTY_ARGS);
    }

    public void toggleLight() {
        printEmit(EVENT_LIGHT);
        getSocket().emit(EVENT_LIGHT, EMPTY_ARGS);
    }

    public interface PlayerListener {

        void onPlay(VideoData videoData);

        void onPause(VideoData videoData);

        void onRewind(VideoData videoData);

        void onLightToggle(boolean isWhite);

        void onNewVideoLoaded(VideoData videoData);
    }

}
