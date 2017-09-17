package ga.chrom_web.player.multiplayer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.youtube.player.YouTubeBaseActivity

class PlayerActivity : YouTubeBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlayerFragment())
                .commit()
    }
}
