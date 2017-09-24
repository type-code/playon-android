package ga.chrom_web.player.multiplayer.view

import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ga.chrom_web.player.multiplayer.ConnectionManager
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.SocketModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val connectionManager = ConnectionManager()
//        connectionManager.join("george")
        startActivity(Intent(this, PlayerActivity::class.java))
    }

    fun hello() {
        print("its mee")
    }
}
