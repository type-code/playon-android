package ga.chrom_web.player.multiplayer.ui.player;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ga.chrom_web.player.multiplayer.PlayerViewModel;
import ga.chrom_web.player.multiplayer.R;
import ga.chrom_web.player.multiplayer.Utils;
import ga.chrom_web.player.multiplayer.databinding.FragmentPlayerBinding;


public class PlayerFragment extends Fragment {

    private CustomYoutubePlayerFragment youtubePlayerFragment;
    private PlayerViewModel mViewModel;
    private FragmentPlayerBinding mBinding;
    private ChatAdapter mChatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.debugLog("Starting room fragment...");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);
        // TODO: is it good or bad idea ???
//        setRetainInstance(true);
        if (youtubePlayerFragment == null) {
            youtubePlayerFragment = new CustomYoutubePlayerFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.youtubeContainer, youtubePlayerFragment)
                    .commit();
        }
        youtubePlayerFragment.setYoutubePlayerListener(new CustomYoutubePlayerFragment.PlayerListener() {
            @Override
            public void onClickUpload() {
                createEditTextDialog();
            }

            @Override
            public void onRewind(int timeInMillis) {
                mViewModel.rewindTo(timeInMillis);
            }

            @Override
            public void onClickPause() {
                mViewModel.pause();
            }

            @Override
            public void onClickPlay() {
                mViewModel.play();
            }

            @Override
            public void onPlayerInitialized() {
                mViewModel.playerInitialized();
            }
        });

        mChatAdapter = new ChatAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.rvChat.setLayoutManager(layoutManager);
        mBinding.rvChat.setHasFixedSize(true);
        mBinding.rvChat.setAdapter(mChatAdapter);
        layoutManager.setStackFromEnd(true);

        return mBinding.getRoot();
    }

    private void createEditTextDialog() {
        final EditText edittext = new EditText(getActivity());
        edittext.setText("https://www.youtube.com/watch?v=XGmFF82PE50");
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Enter youtube video link");
        // TODO: validate link for only youtube videos
        adb.setView(edittext);

        adb.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
            mViewModel.loadVideo(edittext.getText().toString());
        });
        adb.setNegativeButton(android.R.string.cancel, null);
        adb.show();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class);

        mViewModel.getVideoLink().observe(this, videoLink -> {
            youtubePlayerFragment.loadVideo(videoLink);
        });
        mViewModel.getVideoTime().observe(this, timeInMillis -> {
            youtubePlayerFragment.seekToMillis(timeInMillis);
        });
        mViewModel.getShouldPlay().observe(this, shouldPlay -> {
            playOrPause(shouldPlay);
        });
        mViewModel.getPlayerData().observe(this, playerData -> {
            Utils.debugLog("PLAYER DATA POSTED!!!");
            youtubePlayerFragment.loadVideo(playerData.getVideo(),
                    playerData.getTimeInMilli(), playerData.isPlaying());
        });
        mViewModel.getMessages().observe(this, message -> {
            mChatAdapter.addItem(message);
            // after adding item scroll to the very bottom
            mBinding.rvChat.post(() -> {
                mBinding.rvChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
            });
        });
        mBinding.setPlayerViewModel(mViewModel);
    }

    public void onDestroyView() {
        super.onDestroyView();
        youtubePlayerFragment.onParentViewDestroy();
        // TODO: make it better
        mViewModel.setCurrentTime(youtubePlayerFragment.getCurrentTimeMillis() / 1000);
    }


    private void playOrPause(boolean shouldPlay) {
        if (shouldPlay) {
            youtubePlayerFragment.play();
        } else {
            youtubePlayerFragment.pause();
        }
    }
}

