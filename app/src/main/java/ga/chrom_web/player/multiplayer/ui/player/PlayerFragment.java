package ga.chrom_web.player.multiplayer.ui.player;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import ga.chrom_web.player.multiplayer.BuildConfig;
import ga.chrom_web.player.multiplayer.PlayerViewModel;
import ga.chrom_web.player.multiplayer.R;
import ga.chrom_web.player.multiplayer.Utils;
import ga.chrom_web.player.multiplayer.databinding.FragmentPlayerBinding;
import ga.chrom_web.player.multiplayer.databinding.YoutubePlayerControlsBinding;


public class PlayerFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    private YoutubePlayerControlsBinding mPopupBinding;
    private YouTubePlayer mPlayer;
    private PlayerViewModel mViewModel;
    private FragmentPlayerBinding mBinding;
    private ChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private Handler mHandler;
    private boolean mIsProgressActive;
    private boolean mIsControlsShown;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);

        YouTubePlayerSupportFragment youTubeFragment = new YouTubePlayerSupportFragment();
        getFragmentManager().beginTransaction().replace(R.id.youtubeContainer, youTubeFragment).commit();
        youTubeFragment.initialize(BuildConfig.YouTubeApiKey, this);

        mHandler = new Handler(Looper.getMainLooper());
        mChatAdapter = new ChatAdapter();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.rvChat.setLayoutManager(mLayoutManager);
        mBinding.rvChat.setHasFixedSize(true);
        mBinding.rvChat.setAdapter(mChatAdapter);
        mLayoutManager.setStackFromEnd(true);

        mIsControlsShown = true;

        mHandler.postDelayed(() -> {
            initControls(youTubeFragment.getView().getWidth(), youTubeFragment.getView().getHeight());

        }, 1000);
        return mBinding.getRoot();
    }



    public void initControls(int width, int height) {
        int[] leftTopViewPosition = new int[2];
        mBinding.mainContainer.getLocationOnScreen(leftTopViewPosition);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        mPopupBinding = DataBindingUtil
                .inflate(inflater, R.layout.youtube_player_controls, null, false);
        mPopupBinding.setViewModel(mViewModel);

        PopupWindow pw = new PopupWindow(mPopupBinding.getRoot(), width, height);
        pw.showAtLocation(mBinding.mainContainer, Gravity.TOP, 0, leftTopViewPosition[1]);


        ((SeekBar) mPopupBinding.getRoot().findViewById(R.id.videoProgress))
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateProgress(seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateProgress(seekBar);
            }
        });

        mPopupBinding.getRoot().findViewById(R.id.imgUpload).setOnClickListener(view -> {

        });
        mPopupBinding.getRoot().findViewById(R.id.imgFullscreen).setOnClickListener(view -> {

        });
        mPopupBinding.getRoot().setOnClickListener(view -> {
            if (mIsControlsShown) {
                hidePlayerControls();
            } else {
                showPlayerControls();
            }
            mIsControlsShown = !mIsControlsShown;
        });
    }

    private void updateProgress(SeekBar seekBar) {
        int width = seekBar.getWidth()
                - seekBar.getPaddingLeft()
                - seekBar.getPaddingRight();
        int thumbPos = seekBar.getPaddingLeft()
                + width
                * seekBar.getProgress()
                / seekBar.getMax();
        int currentProgressStart = thumbPos - mPopupBinding.tvVideoTimeCurrent.getWidth();
        int durationEnd = thumbPos + mPopupBinding.tvVideoDuration.getWidth();
        if (currentProgressStart > 0) {
            if (durationEnd < Utils.getScreenWidth()) {
                mPopupBinding.tvVideoTimeCurrent.setX(currentProgressStart);
                mPopupBinding.tvVideoDuration.setX(thumbPos);
            }  else {
                mPopupBinding.tvVideoDuration.setX(Utils.getScreenWidth() - mPopupBinding.tvVideoDuration.getWidth());
                mPopupBinding.tvVideoTimeCurrent.setX(mPopupBinding.tvVideoDuration.getX()
                        - mPopupBinding.tvVideoTimeCurrent.getWidth());
            }
        }
    }

    private void hidePlayerControls() {
        mPopupBinding.getRoot().animate().alpha(0f).setDuration(300)
                .withEndAction(() -> {
                    setControlsClickable(false);
                });
    }

    private void showPlayerControls() {
        mPopupBinding.getRoot().animate().alpha(1f).setDuration(300)
                .withStartAction(() -> {
                    setControlsClickable(true);
                });
    }

    private void setControlsClickable(boolean clickable) {
        mPopupBinding.getRoot().findViewById(R.id.imgPlay).setClickable(clickable);
        mPopupBinding.getRoot().findViewById(R.id.imgPause).setClickable(clickable);
        mPopupBinding.getRoot().findViewById(R.id.imgUpload).setClickable(clickable);
        mPopupBinding.getRoot().findViewById(R.id.imgFullscreen).setClickable(clickable);
        // TODO: add seekbar here
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
        mViewModel.getMessages().observe(this, message -> {
            mChatAdapter.addItem(message);
            // after adding item scroll to the very bottom
            mHandler.post(() -> {
                mBinding.rvChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
            });
        });
        mBinding.setPlayerViewModel(mViewModel);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        Log.d("Logs", "YOUTUBE PLAYER INIT SUCCESS");
        mPlayer = player;
        mPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        mViewModel.playerInitialized();
        mPlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {
            }

            @Override
            public void onLoaded(String s) {
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
                Log.d("Logs", "ERROR" + errorReason.toString());
            }
        });
    }

    private void updateProgressBarEachSecond() {

        mBinding.setDuration(mPlayer.getDurationMillis());
        mPopupBinding.setDuration(mPlayer.getDurationMillis());
        if (mIsProgressActive) {
            return;
        }
        mIsProgressActive = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBinding.setCurrentTime(mPlayer.getCurrentTimeMillis());
                mPopupBinding.setCurrentTime(mPlayer.getCurrentTimeMillis());
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
        Log.d("Logs", "error");
        Log.d("Logs", errorReason.toString());
    }

}

