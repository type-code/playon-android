package ga.chrom_web.player.multiplayer

import android.os.Handler
import android.os.Looper
import ga.chrom_web.player.multiplayer.di.App
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.HttpURLConnection
import java.net.URI
import javax.inject.Inject
import javax.inject.Named
import kotlin.concurrent.thread


class SmilesLoader {

    companion object {
        private const val SMILES_DATA_LINK = "http://player.chrom-web.ga/api/smiles.php"
        // TODO: get smiles version link
        private const val SMILES_VERSION_LINK = "version"
        private const val SMILES_FILENAME = "smiles"
    }

    private var smilesNames: File

    @Inject
    @field:[Named ("internalDir")]
    lateinit var internalDir: File

    @Inject
    @field:[Named ("externalCacheDir")]
    lateinit var cacheDir: File

    init {
        App.getComponent().inject(this)
        smilesNames = File(internalDir, SMILES_FILENAME)
    }

    public fun getSmilesNames(onSmilesNamesReadyListener: OnSmilesReadyListener?) {
        thread {
            if (!areSmilesNamesLoaded()) {
                loadSmilesNames()
            }
            val smilesNames = readSmilesNames()
            onSmilesNamesReadyListener?.onSmilesReady(smilesNames)
            downloadSmiles(smilesNames)
        }
    }

    public fun getSmilesPaths(onSmilesPathReadyListener: OnSmilesPathReadyListener?) {
        thread {
            val smilesNames = readSmilesNames()
            downloadSmiles(smilesNames)
            smilesNames.forEach {
                smilesNames[it.key] = cacheDir.absolutePath + "/" + it.value
            }
//            Handler(Looper.getMainLooper()).post({
            onSmilesPathReadyListener?.onSmilesReady(smilesNames)
//            })
        }
    }


    private fun readSmilesNames():HashMap<String, String> {
        return JsonUtil.parseSmilesFromFile(FileUtils.read(smilesNames))
    }

    private fun loadSmilesNames() {

        val request: Request = Request.Builder()
                .url(SMILES_DATA_LINK)
                .build()

        val response = OkHttpClient().newCall(request).execute()
        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!.string()
            // TODO: save server image path
            val smiles = JsonUtil.getSmilesJsonArray(body)
            saveSmilesNames(smiles)
        }
        response.close()

    }

    public fun checkSmilesVersion() {
        // TODO: implement
    }

    private fun areSmilesNamesLoaded() = smilesNames.exists()

    private fun saveSmilesNames(jsonSmiles: String) {
        FileUtils.write(smilesNames, jsonSmiles)
    }

    private fun downloadSmiles(smilesNames: HashMap<String, String>) {
        val smilesDir = "http://player.chrom-web.ga/img/s/"
        smilesNames.forEach {
            val filename = it.value

            val uri = URI.create("$smilesDir$filename")
            val file = File(cacheDir, filename)
            if (!file.exists()) {
                downloadSmile(uri, file)
            }
        }
    }

    private fun downloadSmile(link: URI, file: File) {
        Utils.debugLog("Downloading smile: " + link)
        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null
        try {
            val url = link.toURL()
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode < HttpURLConnection.HTTP_OK
                    || connection.responseCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
                //TODO: handle
            }
            input = connection.inputStream
            output = FileOutputStream(file)

            val data = ByteArray(4096)
            var count: Int
            while (true) {
                count = input!!.read(data)
                if (count == -1) {
                    return
                }
                output.write(data, 0, count)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                output?.close()
                input?.close()
            } catch (ignored: IOException) {
            }
            connection?.disconnect()
        }
    }

    interface OnSmilesReadyListener {
        fun onSmilesReady(smiles: HashMap<String, String> )
    }

    interface OnSmilesPathReadyListener {
        fun onSmilesReady(smiles: HashMap<String, String> )
    }

}
