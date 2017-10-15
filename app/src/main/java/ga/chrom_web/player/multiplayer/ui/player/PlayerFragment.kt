package ga.chrom_web.player.multiplayer.ui.player

import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.Utils
import ga.chrom_web.player.multiplayer.data.ChatItem
import ga.chrom_web.player.multiplayer.databinding.FragmentPlayerBinding
import ga.chrom_web.player.multiplayer.ui.SmilesAdapter
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.util.*


class PlayerFragment : Fragment() {

    companion object {
        private const val SMILES_IN_ROW_PORTRAIT = 7
        private const val SMILES_IN_ROW_LANDSCAPE = 3
        private const val KEYBOARD_HEIGHT_IN_DP = 263
        private const val MESSAGES = "messages"
    }

    private var mYoutubePlayerFragment: CustomYoutubePlayerFragment? = null
    private lateinit var mViewModel: PlayerViewModel
    private lateinit var mBinding: FragmentPlayerBinding
    private var mChatAdapter: ChatAdapter? = null
    private var mIsOrientationChangedByButton = false
    private var mIsSmilesLayoutVisible = false
    private var mHandler: Handler = Handler(Looper.getMainLooper())

    private val screenOrientation: Int
        get() = resources.configuration.orientation


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Utils.debugLog("Starting room fragment...")
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)

        // TODO: is it good or bad idea setRetainInstance(true)???

        if (mYoutubePlayerFragment == null) {
            mYoutubePlayerFragment = CustomYoutubePlayerFragment()
            fragmentManager.beginTransaction()
                    .replace(R.id.youtubeContainer, mYoutubePlayerFragment)
                    .commit()
        }

        mYoutubePlayerFragment!!.playerListener = object : CustomYoutubePlayerFragment.PlayerListener {
            override fun onClickFullscreen() {
                mIsOrientationChangedByButton = true
                if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            override fun onClickUpload() {
                createUploadDialog()
            }

            override fun onRewind(timeInMillis: Int) {
                mViewModel.rewindTo(timeInMillis)
            }

            override fun onClickPause() {
                mViewModel.pause()
            }

            override fun onClickPlay() {
                mViewModel.play()
            }

            override fun onPlayerInitialized() {
                mViewModel.playerInitialized()
            }
        }

        initChat()

        savedInstanceState?.let {
            val messages = savedInstanceState.getSerializable(MESSAGES) as ArrayList<ChatItem>
            mChatAdapter!!.addItems(messages)
        }

        val orientationEventListener = object : OrientationEventListener(activity) {
            override fun onOrientationChanged(orientation: Int) {
                val epsilon = 10
                val leftLandscape = 90
                val rightLandscape = 270
                val portraitInverse = 180
                val portraitNormal = 0
                // TODO: continue
                if (epsilonCheck(orientation, leftLandscape, epsilon) || epsilonCheck(orientation, rightLandscape, epsilon)) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                }
            }

            private fun epsilonCheck(a: Int, b: Int, epsilon: Int): Boolean {
                return a > b - epsilon && a < b + epsilon
            }
        }
        orientationEventListener.enable()
        return mBinding.root
    }


    /**
     *  Hides smile keyboard
     *  On portrait mode will change size of keyboard to 0
     *  On landscape mode only change visibility to View.GONE
     */
    private fun hideSmilesKeyboard(withRequestLayout: Boolean) {
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.smilesKeyboard.layoutParams.height = 0
            if (withRequestLayout) {
                mBinding.smilesKeyboard.requestLayout()
            }
        }
        mIsSmilesLayoutVisible = false
        mBinding.isSmileKeyboardOpen = false

    }

    /**
     *  Hides smile keyboard
     *  On portrait mode will change size of keyboard to KEYBOARD_HEIGHT_IN_DP
     *  On landscape mode only change visibility to View.VISIBLE
     */
    private fun showSmilesKeyboard(withRequestLayout: Boolean) {
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            val isKeyboardVisible = KeyboardVisibilityEvent.isKeyboardVisible(activity)
            if (isKeyboardVisible) {
                Utils.hideKeyboard(activity)
            }
            mBinding.smilesKeyboard.layoutParams.height = Utils.dpToPx(activity, KEYBOARD_HEIGHT_IN_DP).toInt()
            if (withRequestLayout || !isKeyboardVisible) {
                mBinding.smilesKeyboard.requestLayout()
            }
        }
        mIsSmilesLayoutVisible = true
        mBinding.isSmileKeyboardOpen = true
    }

    private fun initSmiles(smilePaths: HashMap<String, String>) {
        KeyboardVisibilityEvent.setEventListener(activity) { isOpen ->
            if (isOpen) {
                hideSmilesKeyboard(true)
            }
        }

        mBinding.etMessage?.setOnClickListener { hideSmilesKeyboard(false) }

        mBinding.imgSmiles.setOnClickListener {
            if (mIsSmilesLayoutVisible) {
                hideSmilesKeyboard(false)

                // code below will work only in portrait mode
                mBinding.etMessage?.apply {
                    requestFocus()
                    Utils.showKeyboard(mBinding.etMessage)
                }
            } else {
                showSmilesKeyboard(false)
            }
        }

        val adapter = SmilesAdapter(convertSmilesPaths(smilePaths))
        val smilesInRow: Int

        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            smilesInRow = SMILES_IN_ROW_PORTRAIT
            adapter.onSmileClickListener = { smile ->
                mBinding.etMessage?.append(smile)
            }
        } else { // landscape
            smilesInRow = SMILES_IN_ROW_LANDSCAPE
            adapter.onSmileClickListener = { smile ->
                mViewModel.sendSmile(smile)
            }
        }

        val layoutManager = GridLayoutManager(activity, smilesInRow)
        mBinding.smilesKeyboard.layoutManager = layoutManager
        mBinding.smilesKeyboard.adapter = adapter
    }

    private fun convertSmilesPaths(smilePaths: HashMap<String, String>): ArrayList<Pair<String, String>> {
        val smilesArray = ArrayList<Pair<String, String>>()
        for (key in smilePaths.keys) {
            val pair = Pair<String, String>(key, smilePaths.get(key)!!)
            smilesArray.add(pair)
        }
        return smilesArray
    }

    private fun initChat() {
        mChatAdapter = ChatAdapter()

        val layoutManager = LinearLayoutManager(activity)
        mBinding.rvChat.layoutManager = layoutManager
        mBinding.rvChat.setHasFixedSize(true)
        mBinding.rvChat.adapter = mChatAdapter
        layoutManager.stackFromEnd = true
    }

    private fun createUploadDialog() {
        val edittext = EditText(activity)
        edittext.setText("https://www.youtube.com/watch?v=XGmFF82PE50")
        val adb = AlertDialog.Builder(activity)
        adb.setTitle("Enter youtube video link")
        // TODO: validate link for only youtube videos
        adb.setView(edittext)

        adb.setPositiveButton(android.R.string.ok) { _, _ -> mViewModel.loadVideo(edittext.text.toString()) }
        adb.setNegativeButton(android.R.string.cancel, null)
        adb.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(PlayerViewModel::class.java)

        mViewModel.videoLink.observe(this, android.arch.lifecycle.Observer { videoLink ->
            videoLink?.let {
                mYoutubePlayerFragment!!.loadVideo(videoLink)
            }
        })
        mViewModel.videoTime.observe(this, android.arch.lifecycle.Observer { timeInMillis ->
            timeInMillis?.let {
                mYoutubePlayerFragment!!.seekToMillis(timeInMillis)
            }
        })
        mViewModel.shouldPlay.observe(this, android.arch.lifecycle.Observer { shouldPlay ->
            shouldPlay?.let {
                playOrPause(shouldPlay)
            }
        })
        mViewModel.playerData.observe(this, android.arch.lifecycle.Observer { playerData ->
            playerData?.let {
                mYoutubePlayerFragment!!.loadVideo(playerData.getVideo(),
                        playerData.getTimeInMilli(), playerData.isPlaying())
            }
        })
        mViewModel.message.observe(this, android.arch.lifecycle.Observer { message ->
            mChatAdapter!!.addItem(message)
            // after adding item scroll to the very bottom
            mBinding.rvChat.post { mBinding.rvChat.smoothScrollToPosition(mChatAdapter!!.itemCount - 1) }
        })
        mViewModel.smilesPaths.observe(this, android.arch.lifecycle.Observer { smilePaths ->
            mHandler.post {
                smilePaths?.let {
                    initSmiles(smilePaths)
                }
            }
        })
        mBinding.playerViewModel = mViewModel
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // remove last item because it will be also post by LiveData
        if (!mChatAdapter!!.items.isEmpty()) {
            mChatAdapter!!.items.removeAt(mChatAdapter!!.items.size - 1)
        }
        outState.putSerializable(MESSAGES, mChatAdapter!!.items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mYoutubePlayerFragment!!.onParentViewDestroy()
        // TODO: make it better
        // if it is configuration then set time to retain it later
        mViewModel.setCurrentTime(mYoutubePlayerFragment!!.getCurrentTimeMillis()!! / 1000)
    }


    private fun playOrPause(shouldPlay: Boolean) {
        if (shouldPlay) {
            mYoutubePlayerFragment!!.play()
        } else {
            mYoutubePlayerFragment!!.pause()
        }
    }

    /**
     * Method should be called by activity when back button was pressed.
     *
     * @return boolean true if handled
     */
    fun onBackPressed(): Boolean {
        if (mIsSmilesLayoutVisible) {
            hideSmilesKeyboard(true)
            return true
        }
        return false
    }

}

