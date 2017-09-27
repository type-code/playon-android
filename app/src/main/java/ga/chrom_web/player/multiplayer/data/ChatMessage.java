package ga.chrom_web.player.multiplayer.data;


import android.graphics.Color;

public class ChatMessage extends ChatItem {
    private String text;
    private String color;

    public ChatMessage() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return Color.parseColor(color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFullMessage() {
        return nick + ": " + text;
    }
}
