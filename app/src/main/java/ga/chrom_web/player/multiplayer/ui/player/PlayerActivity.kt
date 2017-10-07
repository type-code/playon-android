package ga.chrom_web.player.multiplayer.ui.player

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ga.chrom_web.player.multiplayer.R

class PlayerActivity : AppCompatActivity() {


    private lateinit var playerFragment: PlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        if (savedInstanceState == null) {
            playerFragment = PlayerFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, playerFragment)
                    .commit()
        }

    }
}
