package ga.chrom_web.player.multiplayer


import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppExecutors {
    var mainThread: Executor
        private set
    var networkThread: Executor
        private set
    var diskThread: Executor
        private set

    @Inject
    constructor() {
        this.mainThread = MainThreadExecutor()
        this.networkThread = Executors.newSingleThreadScheduledExecutor()
        this.diskThread = Executors.newSingleThreadScheduledExecutor()
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())
        override fun execute(runnable: Runnable) {
            mainThreadHandler.post(runnable)
        }
    }

}
