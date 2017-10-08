package ga.chrom_web.player.multiplayer


import com.google.gson.Gson

import org.json.JSONException
import org.json.JSONObject

object JsonUtil {

    fun <T> jsonToObject(jsonObj: Any, type: Class<T>): T {
        return Gson().fromJson(jsonObj.toString(), type)
    }

    fun parseToggleLight(arg: Any): Boolean {
        try {
            val jsonObject = arg as JSONObject
            return jsonObject.getBoolean("light")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return false
    }

    fun parseNick(arg: Any): String? {
        val jsonObject = arg as JSONObject
        try {
            return jsonObject.getString("nick")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }
}
