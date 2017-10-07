package ga.chrom_web.player.multiplayer


import javax.inject.Inject

import io.socket.client.Socket

abstract class Manager {

    companion object {

        const val EVENT_CONNECTED = "connected"
        const val EVENT_DISCONNECT = "disc"
        const val EVENT_JOIN = "join"

        const val EVENT_PLAY = "play"
        const val EVENT_PAUSE = "pause"
        const val EVENT_REWIND = "rewind"
        const val EVENT_VIDEO_TIME = "video_time"
        const val EVENT_LIGHT = "light"
        const val EVENT_LOAD = "load"

        const val EVENT_MESSAGE = "message"
    }

    @Inject
    lateinit var socket: Socket


    init {
        App.getComponent().inject(this)
        subscribeOnEvents()
    }

    protected abstract fun subscribeOnEvents()

    protected fun printEmit(eventName: String) {
        Utils.debugLog("EMIT " + eventName)
    }
}