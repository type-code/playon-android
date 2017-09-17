package ga.chrom_web.player.multiplayer


import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView


/**
 * A simple [Fragment] subclass.
 */
class PlayerFragment : Fragment(), YouTubePlayer.OnInitializedListener {

    val key:String = "AIzaSyANEN7jpYPyMeZOPMgC8jhlczEWVeoEUYE"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_player, container, false)

        val youtubeView:YouTubePlayerView = view.findViewById(R.id.youtube_view);

        youtubeView.initialize(key, this);
        return view
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, player: YouTubePlayer?, wasRestored: Boolean) {
        player?.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS)
        if (!wasRestored) {
            player?.cueVideo("fhWaJi1Hsfo")
            Handler().postDelayed(Runnable {
                player?.play()
            }, 10000)
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider?, errorReason: YouTubeInitializationResult?) {
        print(errorReason.toString());
    }

}// Required empty public constructor
