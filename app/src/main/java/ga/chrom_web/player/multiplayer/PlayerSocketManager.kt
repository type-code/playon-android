package ga.chrom_web.player.multiplayer


import org.json.JSONException
import org.json.JSONObject

import java.util.HashMap

import ga.chrom_web.player.multiplayer.data.VideoData

class PlayerSocketManager : SocketManager() {

    companion object {
        private val EMPTY_ARGS = Any()
    }

    private var playerListener: PlayerListener? = null

    fun setPlayerListener(playerListener: PlayerListener) {
        this.playerListener = playerListener
    }

    override fun subscribeOnEvents() {
        socket.on(EVENT_PLAY) { args ->
            Utils.debugLog("PLAY: " + args[0])
            playerListener?.onPlay(JsonUtil.jsonToObject(args[0], VideoData::class.java))
        }
        socket.on(EVENT_PAUSE) { args ->
            Utils.debugLog("PAUSE: " + args[0])
            playerListener?.onPause(JsonUtil.jsonToObject(args[0], VideoData::class.java))
        }
        socket.on(EVENT_REWIND) { args ->
            Utils.debugLog("REWIND: " + args[0])
            playerListener?.onRewind(JsonUtil.jsonToObject(args[0], VideoData::class.java))
        }
        socket.on(EVENT_LIGHT) { args ->
            Utils.debugLog("LIGHT CHANGED: " + args[0])
            playerListener?.onLightToggle(JsonUtil.parseToggleLight(args[0]))
        }
        socket.on(EVENT_LOAD) { args ->
            Utils.debugLog("New video loaded: " + args[0])
            playerListener?.onNewVideoLoaded(JsonUtil.jsonToObject(args[0], VideoData::class.java))
        }
        socket.on(EVENT_SYNC) { args ->
            Utils.debugLog("Player sync: " + args[0])
            playerListener?.onTimeSync(JsonUtil.jsonToObject(args[0], VideoData::class.java))
        }
        socket.on(EVENT_CLICK) { args ->
            Utils.debugLog("Clicked " + args[0])
        }
    }

    fun play() {
        printEmit(SocketManager.EVENT_PLAY)
        socket.emit(SocketManager.EVENT_PLAY, EMPTY_ARGS)
    }

    fun pause() {
        printEmit(SocketManager.EVENT_PAUSE)
        socket.emit(SocketManager.EVENT_PAUSE, EMPTY_ARGS)
    }

    fun rewind(secondsFromStart: Int) {
        printEmit(SocketManager.EVENT_REWIND)
        val map = mapOf("time" to secondsFromStart)
        socket.emit(SocketManager.EVENT_REWIND, JSONObject(map))
    }

    fun loadVideo(link: String) {
        printEmit(SocketManager.EVENT_LOAD)
        val args = mapOf("link" to link, "playlist" to false)

        socket.emit(SocketManager.EVENT_LOAD, JSONObject(args))

    }

    /**
     * Asks server for current video and time
     */
    fun videoTime() {
        printEmit(SocketManager.EVENT_VIDEO_TIME)
        socket.emit(SocketManager.EVENT_VIDEO_TIME, EMPTY_ARGS)
    }

    fun toggleLight() {
        printEmit(SocketManager.EVENT_LIGHT)
        socket.emit(SocketManager.EVENT_LIGHT, EMPTY_ARGS)
    }

    interface PlayerListener {

        fun onPlay(videoData: VideoData)

        fun onPause(videoData: VideoData)

        fun onRewind(videoData: VideoData)

        fun onLightToggle(isWhite: Boolean)

        fun onNewVideoLoaded(videoData: VideoData)

        fun onTimeSync(videoData: VideoData)
    }

}
