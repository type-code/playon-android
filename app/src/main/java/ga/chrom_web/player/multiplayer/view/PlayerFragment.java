package ga.chrom_web.player.multiplayer.view;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import ga.chrom_web.player.multiplayer.BuildConfig;
import ga.chrom_web.player.multiplayer.PlayerViewModel;
import ga.chrom_web.player.multiplayer.R;
import ga.chrom_web.player.multiplayer.databinding.FragmentPlayerBinding;


public class PlayerFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

//    private ConnectionData videoData;
    private YouTubePlayer mPlayer;
    private PlayerViewModel mViewModel;
    private YouTubePlayerSupportFragment mYouTubeFragment;
    private FragmentPlayerBinding mBinding;
    private Handler mHandler;
    private boolean mIsProgressActive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);

        mYouTubeFragment = new YouTubePlayerSupportFragment();
        getFragmentManager().beginTransaction().replace(R.id.youtubeContainer, mYouTubeFragment).commit();
        mYouTubeFragment.initialize(BuildConfig.YouTubeApiKey, this);

        mHandler = new Handler(Looper.getMainLooper());

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class);

        mViewModel.getVideoLink().observe(this, videoLink -> {
            mPlayer.cueVideo(videoLink);
        });
        mViewModel.getVideoTime().observe(this, timeInMillis -> {
            mPlayer.seekToMillis(timeInMillis);
        });
        mViewModel.getShouldPlay().observe(this, shouldPlay -> {
            playOrPause(shouldPlay);
        });
        mBinding.setPlayerViewModel(mViewModel);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        System.out.println("player init");
        mPlayer = player;
        mPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        mViewModel.playerInitialized();
        mPlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {
            }

            @Override
            public void onLoaded(String s) {
                System.out.println("VIDEO LOADED " + s);
                updateProgressBarEachSecond();
            }

            @Override
            public void onAdStarted() {
            }

            @Override
            public void onVideoStarted() {
            }

            @Override
            public void onVideoEnded() {
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {
            }
        });
    }

    private void updateProgressBarEachSecond() {

        mBinding.pbVideoProgress.setMax(mPlayer.getDurationMillis());
        mBinding.pbVideoProgress.setIndeterminate(false);
        mBinding.pbVideoProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println(seekBar.getProgress() + " " + seekBar.getMax());
                mViewModel.rewindTo(seekBar.getProgress());
            }
        });
        if (mIsProgressActive) {
            return;
        }
        mIsProgressActive = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBinding.pbVideoProgress.setProgress(mPlayer.getCurrentTimeMillis());
                mHandler.postDelayed(this, 1000);
            }
        });
    }


    private void playOrPause(boolean shouldPlay) {
        if (shouldPlay) {
            mPlayer.play();
        } else {
            mPlayer.pause();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        System.out.println("error");
        System.out.println(errorReason.toString());
    }

}

