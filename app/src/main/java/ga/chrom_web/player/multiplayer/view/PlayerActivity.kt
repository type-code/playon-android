package ga.chrom_web.player.multiplayer.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ga.chrom_web.player.multiplayer.R

class PlayerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, PlayerFragment())
                .commit()
    }
}
