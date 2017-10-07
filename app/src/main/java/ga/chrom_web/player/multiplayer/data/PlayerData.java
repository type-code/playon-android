package ga.chrom_web.player.multiplayer.data;


import com.google.gson.annotations.SerializedName;

public class PlayerData {
    private String video;
    private double time;
    @SerializedName("play")
    private boolean isPlaying;
    @SerializedName("light")
    private boolean isLight;

    public PlayerData() {
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public double getTime() {
        return time;
    }

    public int getTimeInMilli() {
        return (int)time * 1000;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isWhiteLight() {
        return isLight;
    }

    public void setLight(boolean light) {
        isLight = light;
    }
}
