package ga.chrom_web.player.multiplayer;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import ga.chrom_web.player.multiplayer.data.ConnectionData;
import ga.chrom_web.player.multiplayer.data.VideoData;

public class JsonUtil {

    public static <T> T jsonToObject(Object jsonObj, Class<T> type) {
        String data = jsonObj.toString();
        Gson gson = new Gson();
        return gson.fromJson(data, type);
    }

    public static boolean parseToggleLight(Object arg) {
        try {
            JSONObject jsonObject = ((JSONObject) arg);
            return jsonObject.getBoolean("light");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String parseNick(Object arg) {
        JSONObject jsonObject = ((JSONObject) arg);
        try {
            return jsonObject.getString("nick");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
