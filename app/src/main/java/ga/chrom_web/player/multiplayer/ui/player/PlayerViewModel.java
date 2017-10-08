package ga.chrom_web.player.multiplayer.ui.player;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ga.chrom_web.player.multiplayer.ChatManager;
import ga.chrom_web.player.multiplayer.ConnectionManager;
import ga.chrom_web.player.multiplayer.Manager;
import ga.chrom_web.player.multiplayer.PlayerManager;
import ga.chrom_web.player.multiplayer.SharedPreferenceHelper;
import ga.chrom_web.player.multiplayer.Utils;
import ga.chrom_web.player.multiplayer.data.ChatItem;
import ga.chrom_web.player.multiplayer.data.ChatMessage;
import ga.chrom_web.player.multiplayer.data.ChatNotification;
import ga.chrom_web.player.multiplayer.data.PlayerData;
import ga.chrom_web.player.multiplayer.data.VideoData;
import ga.chrom_web.player.multiplayer.di.App;

public class PlayerViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> mShouldPlay;
    private MutableLiveData<String> mVideoLink;
    private MutableLiveData<Integer> mVideoTime;
    private MutableLiveData<Boolean> mIsLightWhite;
    private MutableLiveData<ChatItem> mMessage;
    private MutableLiveData<PlayerData> mPlayerData;
    public ObservableField<String> messageField;
    @Inject
    ConnectionManager mConnectionManager;
    @Inject
    PlayerManager mPlayerManager;
    @Inject
    ChatManager mChatManager;
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
        messageField = new ObservableField<>();

        mConnectionManager.setConnectionListener(new ConnectionManager.ConnectionListener() {
            @Override
            public void connected(PlayerData playerData) {
                mPlayerData.postValue(playerData);
                mConnectionManager.join(prefs.getNick());
            }

            @Override
            public void joined(String nick) {
                postNotification(nick, Manager.EVENT_JOIN);
            }

            @Override
            public void someoneDisconnected(String nick) {
                postNotification(nick, Manager.EVENT_DISCONNECT);
            }
        });

        mPlayerManager.setPlayerListener(new PlayerManager.PlayerListener() {
            @Override
            public void onPlay(VideoData videoData) {
                mPlayerData.getValue().setTime(videoData.getTime());
                mPlayerData.getValue().setPlaying(true);

                mVideoTime.postValue(videoData.getTimeInMilli());
                mShouldPlay.postValue(true);
                postNotification(videoData.getNick(), Manager.EVENT_PLAY);

            }

            @Override
            public void onPause(VideoData videoData) {
                mPlayerData.getValue().setPlaying(false);

                mShouldPlay.postValue(false);
                postNotification(videoData.getNick(), Manager.EVENT_PAUSE);
            }

            @Override
            public void onRewind(VideoData videoData) {
                mPlayerData.getValue().setTime(videoData.getTime());

                mVideoTime.postValue(videoData.getTimeInMilli());
                postNotification(videoData.getNick(), Manager.EVENT_REWIND,
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
                postNotification(videoData.getNick(), Manager.EVENT_LOAD);
            }
        });

        mChatManager.setChatListener(new ChatManager.ChatListener() {
            @Override
            public void onMessage(ChatMessage chatMessage) {
                mMessage.postValue(chatMessage);
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
        mPlayerData.getValue().setTime(currentTime);
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

    public MutableLiveData<Boolean> getIsLightWhite() {
        return mIsLightWhite;
    }

    MutableLiveData<ChatItem> getMessage() {
        return mMessage;
    }

}
