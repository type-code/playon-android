package ga.chrom_web.player.multiplayer


import com.google.gson.*

import org.json.JSONException
import org.json.JSONObject
import kotlin.concurrent.thread

class JsonUtil {

    companion object {

        @JvmStatic
        fun <T> jsonToObject(jsonObj: Any, type: Class<T>): T {
            return Gson().fromJson(jsonObj.toString(), type)
        }

        @JvmStatic
        fun parseToggleLight(arg: Any): Boolean {
            try {
                val jsonObject = arg as JSONObject
                return jsonObject.getBoolean("light")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return false
        }


        @JvmStatic
        fun parseNick(arg: Any): String? {
            val jsonObject = arg as JSONObject
            try {
                return jsonObject.getString("nick")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }

        @JvmStatic
        fun getSmilesJsonArray(arg: String): String {
            val json = JsonParser().parse(arg)
            return json.asJsonObject.getAsJsonArray("smiles").toString()
        }

        @JvmStatic
        fun parseSmilesFromFile(json: String): HashMap<String, String> {
            val jsonArray = JsonParser().parse(json).asJsonArray
            val smiles = HashMap<String, String>()
            jsonArray.forEach {
                val jsonSmile = it.asJsonObject
                val smilename = jsonSmile["name"].asString
                val filename = jsonSmile["file"].asString
                smiles[smilename] = filename
            }
            return smiles
        }
    }


}
