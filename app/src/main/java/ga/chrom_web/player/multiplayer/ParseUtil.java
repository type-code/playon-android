package ga.chrom_web.player.multiplayer;


import org.json.JSONException;
import org.json.JSONObject;

public class ParseUtil {

    public static PlayData parseOnPlay(Object playObj) {
        try {
            JSONObject jsonObject = ((JSONObject) playObj);
            PlayData play = new PlayData();
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
        try {
            JSONObject jsonObject = (JSONObject) connectionDataObj;
            ConnectionData connectionData = new ConnectionData();
            connectionData.setVideo(jsonObject.getString("video"));
            connectionData.setTime(jsonObject.getDouble("time"));
            connectionData.setPlaying(jsonObject.getBoolean("play"));
            connectionData.setLight(jsonObject.getBoolean("light"));
            return connectionData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
