package ga.chrom_web.player.multiplayer.data;


public class VideoData {
    private String video;
    private double time;
    private String nick;


    public VideoData() {
    }


    public int getTimeInMilli() {
        return (int)time * 1000;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getTime() {
        return (int) time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
