package ga.chrom_web.player.multiplayer.ui.player

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import ga.chrom_web.player.multiplayer.BuildConfig
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.Utils
import ga.chrom_web.player.multiplayer.data.VideoData

class CustomYoutubePlayerFragment : Fragment(), YouTubePlayer.OnInitializedListener {

    private lateinit var popupView: View
    private var mPlayer: YouTubePlayer? = null
    private var mIsControlsShown: Boolean = true
    private var mIsProgressActive: Boolean = false
    private var mIsPlayerInitialized: Boolean = false
    private lateinit var mHandler: Handler
    private var popupWindow: PopupWindow? = null
    private var mContext: Context? = null

    var youtubePlayerListener: PlayerListener? = null
    var youTubeFragment: YouTubePlayerSupportFragment? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Utils.debugLog("Starting player fragment...")

        val view: View = inflater.inflate(R.layout.fragment_custom_youtube_player, container, false)

        mHandler = Handler(Looper.getMainLooper())
        if (youTubeFragment == null) {
            youTubeFragment = YouTubePlayerSupportFragment()
            fragmentManager.beginTransaction().replace(R.id.youtubeContainer, youTubeFragment).commit()
            youTubeFragment?.initialize(BuildConfig.YouTubeApiKey, this)
        }

        view.post({
            initControls(view, youTubeFragment!!.view!!.width, youTubeFragment!!.view!!.height)
        })
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // getting context in most places returns null
        // so save it as a field
        this.mContext = context
    }

    fun loadVideo(link: String) {
        if (mIsPlayerInitialized) {
            mPlayer?.cueVideo(link)
        }
    }

    fun loadVideo(link: String, time: Int, isPlaying: Boolean) {
        if (!mIsPlayerInitialized) {
            return;
        }
        // loadVideo plays video after load finish and cueVideo not
        if (isPlaying) {
            mPlayer?.loadVideo(link, time)
        } else {
            mPlayer?.cueVideo(link, time)
        }
    }

    public fun play() {
        if (mIsPlayerInitialized) {
            mPlayer?.play()
        }
    }

    public fun pause() {
        if (mIsPlayerInitialized) {
            mPlayer?.pause()
        }
    }

    public fun seekToMillis(milliSeconds: Int) {
        if (mIsPlayerInitialized) {
            mPlayer?.seekToMillis(milliSeconds)
        }
    }

    fun getCurrentTimeMillis():Int? = mPlayer?.currentTimeMillis

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer,
                                         wasRestored: Boolean) {
        Utils.debugLog("Player init success, wasRestored " + wasRestored)
        mIsPlayerInitialized = true;
        mPlayer = player
        mPlayer?.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS)
        youtubePlayerListener?.onPlayerInitialized()
        mPlayer?.setPlayerStateChangeListener(object : YouTubePlayer.PlayerStateChangeListener {
            override fun onLoading() {}

            override fun onLoaded(s: String) {
                updateProgressBarEachSecond()
            }

            override fun onAdStarted() {}

            override fun onVideoStarted() {}

            override fun onVideoEnded() {}

            override fun onError(errorReason: YouTubePlayer.ErrorReason) {
                Utils.debugLog("ERROR" + errorReason.toString())
            }
        })
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider?, result: YouTubeInitializationResult?) {
        Utils.debugLog("PLAYER INIT ERROR " + result.toString())
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("time", mPlayer!!.currentTimeMillis)
    }

    private fun initControls(parent: View, youtubePlayerWidth: Int, youtubePlayerHeight: Int) {
        val inflater = LayoutInflater.from(mContext)
        popupView = inflater.inflate(R.layout.custom_youtube_player_controls, null, false)

        val leftTopPosition = Utils.getStatusBarHeight(mContext);

        popupWindow = PopupWindow(popupView, youtubePlayerWidth, youtubePlayerHeight)
        popupWindow?.showAtLocation(parent, Gravity.TOP.or(Gravity.START), 0, leftTopPosition)
        popupView.findViewById<SeekBar>(R.id.videoProgress)
                .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                        updateProgress(seekBar)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        updateProgress(seekBar)
                        youtubePlayerListener?.onRewind(popupView.findViewById<SeekBar>(R.id.videoProgress).progress)
                    }
                })

        popupView.findViewById<View>(R.id.imgPlay).setOnClickListener({
            youtubePlayerListener?.onClickPlay()
        })

        popupView.findViewById<View>(R.id.imgPause).setOnClickListener({
            youtubePlayerListener?.onClickPause()
        })


        popupView.findViewById<View>(R.id.imgUpload).setOnClickListener({
            youtubePlayerListener?.onClickUpload()
        })
        popupView.findViewById<View>(R.id.imgFullscreen).setOnClickListener({ view ->
            // TODO: go to fullscreen
        })
        popupView.setOnClickListener({ view ->
            if (mIsControlsShown) {
                hidePlayerControls()
            } else {
                showPlayerControls()
            }
            mIsControlsShown = !mIsControlsShown
        })
    }

    private fun hidePlayerControls() {
        popupView.animate().alpha(0f).setDuration(300)
                .withEndAction { setControlsClickable(false) }
    }

    private fun showPlayerControls() {
        popupView.animate().alpha(1f).setDuration(300)
                .withStartAction { setControlsClickable(true) }
    }

    private fun setControlsClickable(clickable: Boolean) {
        popupView.findViewById<View>(R.id.imgPlay).isClickable = clickable
        popupView.findViewById<View>(R.id.imgPause).isClickable = clickable
        popupView.findViewById<View>(R.id.imgUpload).isClickable = clickable
        popupView.findViewById<View>(R.id.imgFullscreen).isClickable = clickable
        popupView.findViewById<View>(R.id.videoProgress).isClickable = clickable
    }

    private fun updateProgressBarEachSecond() {

        popupView.findViewById<SeekBar>(R.id.videoProgress).max = mPlayer!!.durationMillis
        popupView.findViewById<TextView>(R.id.tvVideoDuration).text = Utils.formatTimeMilliseconds(mPlayer!!.durationMillis)
        if (mIsProgressActive) {
            return
        }
        mIsProgressActive = true
        mHandler.post(object : Runnable {
            override fun run() {
                // TODO: find better way to stop timer
                try {
                    mPlayer?.let {
                        popupView.findViewById<SeekBar>(R.id.videoProgress).progress = mPlayer!!.currentTimeMillis
                        popupView.findViewById<TextView>(R.id.tvVideoTimeCurrent).text =
                                Utils.formatTimeMilliseconds(mPlayer!!.currentTimeMillis)
                    }
                    mHandler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun updateProgress(seekBar: SeekBar) {
        if (seekBar.max == 0) {
            return
        }
        val tvVideoTimeCurrent = popupView.findViewById<TextView>(R.id.tvVideoTimeCurrent)
        val tvVideoDuration = popupView.findViewById<TextView>(R.id.tvVideoDuration)
        val width = (seekBar.width
                - seekBar.paddingLeft
                - seekBar.paddingRight)
        val thumbPos = seekBar.paddingLeft + width * seekBar.progress / seekBar.max
        val currentProgressStart = thumbPos - tvVideoTimeCurrent.width
        val durationEnd = thumbPos + tvVideoDuration.width
        if (currentProgressStart > 0) {
            if (durationEnd < Utils.getScreenWidth()) {
                tvVideoTimeCurrent.x = currentProgressStart.toFloat()
                tvVideoDuration.x = thumbPos.toFloat()
            } else {
                tvVideoDuration.x = (Utils.getScreenWidth() - tvVideoDuration.width).toFloat()
                tvVideoTimeCurrent.x = tvVideoDuration.x - tvVideoTimeCurrent.width
            }
        }
    }

      fun onParentViewDestroy() {
        // for some reason lifecycle events called in some random way
        // and most of them not called at all, so take the parent lifecycle events
        popupWindow?.dismiss()
        mIsPlayerInitialized = false
    }

    interface PlayerListener {

        fun onPlayerInitialized()

        fun onClickPlay()

        fun onClickPause()

        /**
         *  Occurs when user manually rewinds video time
         */
        fun onRewind(timeInMillis: Int)

        fun onClickUpload()

    }

}