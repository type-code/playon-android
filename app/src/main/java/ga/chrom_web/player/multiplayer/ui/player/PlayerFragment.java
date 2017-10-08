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

import java.util.ArrayList;

import ga.chrom_web.player.multiplayer.R;
import ga.chrom_web.player.multiplayer.Utils;
import ga.chrom_web.player.multiplayer.data.ChatItem;
import ga.chrom_web.player.multiplayer.databinding.FragmentPlayerBinding;


public class PlayerFragment extends Fragment {

    private static final String MESSAGES = "messages";

    private CustomYoutubePlayerFragment mYoutubePlayerFragment;
    private PlayerViewModel mViewModel;
    private FragmentPlayerBinding mBinding;
    private ChatAdapter mChatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.debugLog("Starting room fragment...");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);

        // TODO: is it good or bad idea setRetainInstance(true)???

        if (mYoutubePlayerFragment == null) {
            mYoutubePlayerFragment = new CustomYoutubePlayerFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.youtubeContainer, mYoutubePlayerFragment)
                    .commit();
        }
        mYoutubePlayerFragment.setYoutubePlayerListener(new CustomYoutubePlayerFragment.PlayerListener() {
            @Override
            public void onClickUpload() {
                createUploadDialog();
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

        initChat();
        if (savedInstanceState != null) {
            ArrayList<ChatItem> messages = (ArrayList<ChatItem>) savedInstanceState.getSerializable(MESSAGES);
            mChatAdapter.addItems(messages);
        }
        return mBinding.getRoot();
    }

    private void initChat() {
        mChatAdapter = new ChatAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.rvChat.setLayoutManager(layoutManager);
        mBinding.rvChat.setHasFixedSize(true);
        mBinding.rvChat.setAdapter(mChatAdapter);
        layoutManager.setStackFromEnd(true);
    }

    private void createUploadDialog() {
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
            mYoutubePlayerFragment.loadVideo(videoLink);
        });
        mViewModel.getVideoTime().observe(this, timeInMillis -> {
            mYoutubePlayerFragment.seekToMillis(timeInMillis);
        });
        mViewModel.getShouldPlay().observe(this, shouldPlay -> {
            playOrPause(shouldPlay);
        });
        mViewModel.getPlayerData().observe(this, playerData -> {
            mYoutubePlayerFragment.loadVideo(playerData.getVideo(),
                    playerData.getTimeInMilli(), playerData.isPlaying());
        });
        mViewModel.getMessage().observe(this, message -> {
            mChatAdapter.addItem(message);
            // after adding item scroll to the very bottom
            mBinding.rvChat.post(() -> {
                mBinding.rvChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
            });
        });
        mBinding.setPlayerViewModel(mViewModel);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // remove last item because it will be also post by LiveData
        mChatAdapter.getItems().remove(mChatAdapter.getItems().size() - 1);
        outState.putSerializable(MESSAGES, mChatAdapter.getItems());
    }

    public void onDestroyView() {
        super.onDestroyView();
        mYoutubePlayerFragment.onParentViewDestroy();
        // TODO: make it better
        mViewModel.setCurrentTime(mYoutubePlayerFragment.getCurrentTimeMillis() / 1000);
    }


    private void playOrPause(boolean shouldPlay) {
        if (shouldPlay) {
            mYoutubePlayerFragment.play();
        } else {
            mYoutubePlayerFragment.pause();
        }
    }
}

