package ga.chrom_web.player.multiplayer.ui.player;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import javax.inject.Inject;

import ga.chrom_web.player.multiplayer.ChatSocketManager;
import ga.chrom_web.player.multiplayer.ConnectionSocketManager;
import ga.chrom_web.player.multiplayer.PlayerSocketManager;
import ga.chrom_web.player.multiplayer.SmilesLoader;
import ga.chrom_web.player.multiplayer.SocketManager;
import ga.chrom_web.player.multiplayer.SharedPreferenceHelper;
import ga.chrom_web.player.multiplayer.Utils;
import ga.chrom_web.player.multiplayer.data.ChatItem;
import ga.chrom_web.player.multiplayer.data.ChatMessage;
import ga.chrom_web.player.multiplayer.data.ChatNotification;
import ga.chrom_web.player.multiplayer.data.PlayerData;
import ga.chrom_web.player.multiplayer.data.VideoData;
import ga.chrom_web.player.multiplayer.di.App;

public class PlayerViewModel extends AndroidViewModel {

    private MutableLiveData<HashMap<String, String>> mSmilesPaths;
    private MutableLiveData<Boolean> mShouldPlay;
    private MutableLiveData<String> mVideoLink;
    private MutableLiveData<Integer> mVideoTime;
    private MutableLiveData<Boolean> mIsLightWhite;
    private MutableLiveData<ChatItem> mMessage;
    private MutableLiveData<PlayerData> mPlayerData;
    public ObservableField<String> messageField;
    @Inject
    ConnectionSocketManager mConnectionManager;
    @Inject
    PlayerSocketManager mPlayerManager;
    @Inject
    ChatSocketManager mChatManager;
    @Inject
    SharedPreferenceHelper prefs;

    public PlayerViewModel(Application application) {
        super(application);
        App.getComponent().inject(this);
        mShouldPlay = new MutableLiveData<>();
        mVideoLink = new MutableLiveData<>();
        mVideoTime = new MutableLiveData<>();
        mIsLightWhite = new MutableLiveData<>();
        mMessage = new MutableLiveData<>();
        mPlayerData = new MutableLiveData<>();
        mSmilesPaths = new MutableLiveData<>();
        messageField = new ObservableField<>();

        mConnectionManager.setConnectionListener(new ConnectionSocketManager.ConnectionListener() {
            @Override
            public void connected(PlayerData playerData) {
                mPlayerData.postValue(playerData);
                mConnectionManager.join(prefs.getNick());
            }

            @Override
            public void joined(String nick) {
                postNotification(nick, SocketManager.EVENT_JOIN);
            }

            @Override
            public void someoneDisconnected(String nick) {
                postNotification(nick, SocketManager.EVENT_DISCONNECT);
            }
        });

        mPlayerManager.setPlayerListener(new PlayerSocketManager.PlayerListener() {
            @Override
            public void onPlay(VideoData videoData) {
                mPlayerData.getValue().setTime(videoData.getTime());
                mPlayerData.getValue().setPlaying(true);

                mVideoTime.postValue(videoData.getTimeInMilli());
                mShouldPlay.postValue(true);
                postNotification(videoData.getNick(), SocketManager.EVENT_PLAY);

            }

            @Override
            public void onPause(VideoData videoData) {
                mPlayerData.getValue().setPlaying(false);

                mShouldPlay.postValue(false);
                postNotification(videoData.getNick(), SocketManager.EVENT_PAUSE);
            }

            @Override
            public void onRewind(VideoData videoData) {
                mPlayerData.getValue().setTime(videoData.getTime());

                mVideoTime.postValue(videoData.getTimeInMilli());
                postNotification(videoData.getNick(), SocketManager.EVENT_REWIND,
                        Utils.formatTimeSeconds(videoData.getTime()));
            }

            @Override
            public void onLightToggle(boolean isWhite) {
                mPlayerData.getValue().setLight(isWhite);

                mIsLightWhite.postValue(isWhite);
            }

            @Override
            public void onNewVideoLoaded(VideoData videoData) {
                mPlayerData.getValue().setVideo(videoData.getVideo());

                mVideoLink.postValue(videoData.getVideo());
                postNotification(videoData.getNick(), SocketManager.EVENT_LOAD);
            }
        });
        mChatManager.setMChatListener(new ChatSocketManager.ChatListener() {
            @Override
            public void onMessage(ChatMessage chatMessage) {
                mMessage.postValue(chatMessage);
            }
        });

        SmilesLoader smilesLoader = new SmilesLoader();
        smilesLoader.getSmilesPaths(new SmilesLoader.OnSmilesPathReadyListener() {
            @Override
            public void onSmilesReady(@NotNull HashMap<String, String> smiles) {
                mSmilesPaths.postValue(smiles);
            }
        });
    }

    void playerInitialized() {
        if (!mConnectionManager.isConnected()) {
            mConnectionManager.connect();
        } else {
            // user is already connected
            // player was reinitialized, so send data one more time
            mPlayerData.postValue(mPlayerData.getValue());
        }
    }

    void setCurrentTime(int currentTime) {
        if (mPlayerData.getValue() != null) {
            mPlayerData.getValue().setTime(currentTime);
        }
    }

    void rewindTo(int milliSecondsFromStart) {
        mPlayerManager.rewind(milliSecondsFromStart / 1000);
    }

    void loadVideo(String link) {
        mPlayerManager.loadVideo(link);
    }


    public void send() {
        mChatManager.sendMessage(messageField.get(), prefs.getHexColor());
        messageField.set("");
    }


    /**
     * Used in landscape mode
     * Emits message that consists of one smile
     * @param smile string code of smile
     */
    public void sendSmile(String smile) {
        mChatManager.sendMessage(smile, prefs.getHexColor());
    }

    public void play() {
        mPlayerManager.play();
    }

    public void pause() {
        mPlayerManager.pause();
    }

    private void postNotification(String nick, String event) {
        postNotification(nick, event, null);
    }

    private void postNotification(String nick, String event, @Nullable String additionalInfo) {
        ChatNotification notification = new ChatNotification(nick, event, additionalInfo);
        mMessage.postValue(notification);
    }

    MutableLiveData<PlayerData> getPlayerData() {
        return mPlayerData;
    }

    MutableLiveData<String> getVideoLink() {
        return mVideoLink;
    }

    MutableLiveData<Integer> getVideoTime() {
        return mVideoTime;
    }

    MutableLiveData<Boolean> getShouldPlay() {
        return mShouldPlay;
    }

    MutableLiveData<Boolean> getIsLightWhite() {
        return mIsLightWhite;
    }

    MutableLiveData<ChatItem> getMessage() {
        return mMessage;
    }

    public MutableLiveData<HashMap<String, String>> getSmilesPaths() {
        return mSmilesPaths;
    }
}
