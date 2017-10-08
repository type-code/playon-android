package ga.chrom_web.player.multiplayer.data

import java.io.Serializable


abstract class ChatItem : Serializable{
    public var nick: String? = null
}
