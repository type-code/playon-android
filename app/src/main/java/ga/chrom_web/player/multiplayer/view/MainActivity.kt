package ga.chrom_web.player.multiplayer.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ga.chrom_web.player.multiplayer.ConnectionManager
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.SocketModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun hello() {
        print("its mee")
    }
}
