package ga.chrom_web.player.multiplayer;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class JsonUtil {


    public static VideoData parseOnPlay(Object playObj) {
        try {
            JSONObject jsonObject = ((JSONObject) playObj);
            VideoData play = new VideoData();
            play.setNick(jsonObject.getString("nick"));
            play.setTime(jsonObject.getDouble("time"));
            play.setVideo(jsonObject.getString("video"));
            return play;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConnectionData parseConnectionData(Object connectionDataObj) {
        String data = connectionDataObj.toString();
        Gson gson = new Gson();
        return gson.fromJson(data, ConnectionData.class);
    }


    public static int parseRewind(Object objects) {
        try {
            JSONObject jsonObject = ((JSONObject) objects);
            return jsonObject.getInt("second");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
