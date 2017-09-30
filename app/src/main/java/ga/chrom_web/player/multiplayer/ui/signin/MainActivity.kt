package ga.chrom_web.player.multiplayer.ui.signin

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.ui.player.PlayerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // TODO: create viewmodel ???
        if (PreferenceManager.getDefaultSharedPreferences(this).contains("nick")) {
            startActivity(Intent(this, PlayerActivity::class.java))
            finish()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment())
                .commit()

    }

    fun hello() {
        print("its mee")
    }
}
