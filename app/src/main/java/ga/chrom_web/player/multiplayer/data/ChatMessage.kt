package ga.chrom_web.player.multiplayer.data


import android.graphics.Color
import java.io.Serializable

class ChatMessage : ChatItem(), Serializable {

    companion object {
        private const val DEFAULT_COLOR = Color.BLACK
    }

    private var text: String? = null
    private var color: String? = null

    fun getColor(): Int {
        color?.let {
            return Color.parseColor(it)
        }
        return DEFAULT_COLOR;
    }

    fun getFullMessage():String = nick + ": " + text

    fun getText(): String? = text

    override fun toString(): String = "ChatMessage(text=$text, color=$color)"
}
