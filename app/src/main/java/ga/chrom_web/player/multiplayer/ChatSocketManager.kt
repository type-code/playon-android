package ga.chrom_web.player.multiplayer


import org.json.JSONObject

import ga.chrom_web.player.multiplayer.data.ChatMessage

class ChatSocketManager : SocketManager() {

    var mChatListener: ChatListener? = null

    override fun subscribeOnEvents() {
        socket.on(SocketManager.EVENT_MESSAGE) {
            Utils.debugLog("Chat message: " + it[0].toString())
            mChatListener?.onMessage(JsonUtil.jsonToObject(it[0], ChatMessage::class.java))
        }
    }

    fun sendMessage(text: String, hexColor: String) {
        val map = mapOf("text" to text, "hexColor" to hexColor)
        socket.emit(SocketManager.EVENT_MESSAGE, JSONObject(map))
    }

    interface ChatListener {
        fun onMessage(chatMessage: ChatMessage)
    }
}
