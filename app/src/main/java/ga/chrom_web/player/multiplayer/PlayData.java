package ga.chrom_web.player.multiplayer;


class PlayData {
    private String video;
    private double time;
    private String nick;

    public PlayData() {
    }

    public PlayData(String video, double time, String nick) {
        this.video = video;
        this.time = time;
        this.nick = nick;
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
