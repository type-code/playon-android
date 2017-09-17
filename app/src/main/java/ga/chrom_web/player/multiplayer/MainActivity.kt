package ga.chrom_web.player.multiplayer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hello()
        startActivity(Intent(this, PlayerActivity::class.java))
    }

    fun hello() {
        print("its mee")
    }
}
