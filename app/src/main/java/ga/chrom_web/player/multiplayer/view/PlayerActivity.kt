package ga.chrom_web.player.multiplayer.view

import android.os.Bundle
import com.google.android.youtube.player.YouTubeBaseActivity
import ga.chrom_web.player.multiplayer.R

class PlayerActivity : YouTubeBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlayerFragment())
                .commit()
    }
}
