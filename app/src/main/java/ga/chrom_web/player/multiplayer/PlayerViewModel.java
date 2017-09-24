package ga.chrom_web.player.multiplayer;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.os.Looper;

public class PlayerViewModel extends AndroidViewModel {

//    private MutableLiveData<VideoData> videoData;
    private MutableLiveData<Boolean> mShouldPlay;
    private MutableLiveData<String> mVideoLink;
    private MutableLiveData<Integer> mVideoTime;
    private ConnectionManager mConnectionManager;
    private PlayerManager mPlayerManager;
    private Handler mHandler;

    public PlayerViewModel(Application application) {
        super(application);
        // TODO: di
        mHandler = new Handler(Looper.getMainLooper());
        mPlayerManager = new PlayerManager();
//        videoData = new MutableLiveData<>();
        mShouldPlay = new MutableLiveData<>();
        mVideoLink = new MutableLiveData<>();
        mVideoTime = new MutableLiveData<>();

        mConnectionManager = new ConnectionManager();
        mConnectionManager.setConnectionListener(new ConnectionManager.ConnectionListener() {
            @Override
            public void connected(ConnectionData connectionData) {
                System.out.println("connected");
                mHandler.post(() -> {
                    mVideoTime.setValue(connectionData.getTimeInMilli());
                    mVideoLink.setValue(connectionData.getVideo());
                    mShouldPlay.setValue(connectionData.isPlaying());
                });

                mConnectionManager.join("GEORGE");
            }

            @Override
            public void joined() {
                System.out.println("joined");
            }
        });

        mPlayerManager.setPlayerListener(new PlayerManager.PlayerListener() {
            @Override
            public void onPlay(VideoData playData) {
                mHandler.post(() -> {
                    mVideoTime.setValue(playData.getTimeInMilli());
                    mShouldPlay.setValue(true);
                });
            }

            @Override
            public void onPause() {
                mHandler.post(() -> {
                    mShouldPlay.setValue(false);
                });
            }

            @Override
            public void onRewind(int rewindTo) {
                mHandler.post(() -> {
                    mVideoTime.setValue(rewindTo * 1000);
                });
            }
        });
    }

    public MutableLiveData<String> getVideoLink() {
        return mVideoLink;
    }

    public MutableLiveData<Integer> getVideoTime() {
        return mVideoTime;
    }

    public MutableLiveData<Boolean> getShouldPlay() {
        return mShouldPlay;
    }

    public void rewindTo(int milliSecondsFromStart) {
        mPlayerManager.rewind(milliSecondsFromStart / 1000);
    }

    public void play() {
        mPlayerManager.play();
    }

    public void pause() {
        mPlayerManager.pause();
    }

    public void playerInitialized() {
        mConnectionManager.connect();
    }
}
