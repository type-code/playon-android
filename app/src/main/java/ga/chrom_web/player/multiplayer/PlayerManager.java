package ga.chrom_web.player.multiplayer;


import javax.inject.Inject;

import io.socket.client.Socket;

public class PlayerManager {

    @Inject
    public Socket socket;

    private PlayerListener playerListener;

    public PlayerManager() {
        App.getComponent().inject(this);
        subscribeOnEvents();
    }

    private void subscribeOnEvents() {
        socket.on("play", args -> {
            // TODO: play
            if (playerListener != null) {
                playerListener.onPlay(ParseUtil.parseOnPlay(args[0]));
            }
        });
        socket.on("pause", args -> {
            if (playerListener != null) {
                playerListener.onPause();
            }
        });
    }

    public void play() {
        socket.emit("play", new Object());
    }

    public void pause() {
        socket.emit("pause", new Object());
    }

    public interface PlayerListener {

        void onPlay(PlayData playData);

        void onPause();
    }

}
