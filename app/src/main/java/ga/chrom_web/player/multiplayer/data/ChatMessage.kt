package ga.chrom_web.player.multiplayer.data


import android.graphics.Color

class ChatMessage : ChatItem() {
    private var text: String? = null
    private var color: String? = null

    fun getColor(): Int {
        if (color == null) {
            return Color.BLACK;
        }
        return Color.parseColor(color)
    }

    fun getFullMessage():String = nick + ": " + text

}
