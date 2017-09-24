package ga.chrom_web.player.multiplayer;


public class Player {

    private PlayerManager playerManager;
    private PlayerListener playerListener;

    public Player() {
/*        socketModel = new SocketModel();
        socketModel.setSocketListener(new SocketModel.SocketListener() {
            @Override
            public void connected(ConnectionData connectionData) {
                if (playerListener != null) {
                }
            }

            @Override
            public void onPlay(VideoData play) {

            }

            @Override
            public void onPause() {

            }
        });*/
    }

    public void play() {
//        socketModel.play();
    }

    public void pause() {
//        socketModel.pause();
    }

    public interface PlayerListener {

    }

}
