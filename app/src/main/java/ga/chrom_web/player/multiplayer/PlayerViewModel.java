package ga.chrom_web.player.multiplayer;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import ga.chrom_web.player.multiplayer.data.ChatItem;
import ga.chrom_web.player.multiplayer.data.ChatMessage;
import ga.chrom_web.player.multiplayer.data.ChatNotification;
import ga.chrom_web.player.multiplayer.data.ConnectionData;
import ga.chrom_web.player.multiplayer.data.VideoData;

public class PlayerViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> mShouldPlay;
    private MutableLiveData<String> mVideoLink;
    private MutableLiveData<Integer> mVideoTime;
    private MutableLiveData<Boolean> mIsLightWhite;
    private MutableLiveData<ChatItem> mMessages;
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
        mMessages = new MutableLiveData<>();

        mConnectionManager.setConnectionListener(new ConnectionManager.ConnectionListener() {
            @Override
            public void someoneConnected(ConnectionData connectionData) {
                mVideoTime.postValue(connectionData.getTimeInMilli());
                mVideoLink.postValue(connectionData.getVideo());
                mShouldPlay.postValue(connectionData.isPlaying());
                mIsLightWhite.postValue(connectionData.isWhiteLight());

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
                mVideoTime.postValue(videoData.getTimeInMilli());
                mShouldPlay.postValue(true);
                postNotification(videoData.getNick(), Manager.EVENT_PLAY);

            }

            @Override
            public void onPause(VideoData videoData) {
                mShouldPlay.postValue(false);
                postNotification(videoData.getNick(), Manager.EVENT_PAUSE);
            }

            @Override
            public void onRewind(VideoData videoData) {
                mVideoTime.postValue(videoData.getTimeInMilli());
                postNotification(videoData.getNick(), Manager.EVENT_REWIND,
                        Utils.getFormattedTime(videoData.getTime()));
            }

            @Override
            public void onLightToggle(boolean isWhite) {
                mIsLightWhite.postValue(isWhite);
            }

            @Override
            public void onNewVideoLoaded(VideoData videoData) {
                postNotification(videoData.getNick(), Manager.EVENT_LOAD);
            }
        });

        mChatManager.setChatListener(new ChatManager.ChatListener() {
            @Override
            public void onMessage(ChatMessage chatMessage) {
                mMessages.postValue(chatMessage);
            }
        });
    }

    public void postNotification(String nick, String event) {
        postNotification(nick, event, null);
    }

    public void postNotification(String nick, String event, @Nullable String additionalInfo) {
        ChatNotification notification = new ChatNotification(nick, event, additionalInfo);
        mMessages.postValue(notification);
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

    public MutableLiveData<Boolean> getIsLightWhite() {
        return mIsLightWhite;
    }

    public MutableLiveData<ChatItem> getMessages() {
        return mMessages;
    }

    public void rewindTo(int milliSecondsFromStart) {
        mPlayerManager.rewind(milliSecondsFromStart / 1000);
    }

    public void send(String message) {
        mChatManager.sendMessage(message, prefs.getHexColor());
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
