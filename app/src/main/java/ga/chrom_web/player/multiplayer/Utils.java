package ga.chrom_web.player.multiplayer;


public class Utils {

    public static String formattedTime(int milliseconds) {
        return getFormattedTime(milliseconds / 1000);
    }

    public static String getFormattedTime(int seconds) {
        StringBuilder sb = new StringBuilder();
        if (seconds > 3600) {
            int hours = seconds / 3600;
            if (hours < 10) {
                sb.append('0');
            }
            sb.append(hours).append(':');
        }
        int minutes = (seconds % 3600) / 60;
        if (minutes < 10) {
            sb.append('0');
        }
        sb.append(minutes).append(':');
        int leftSeconds = seconds % 60;
        if (leftSeconds < 10) {
            sb.append('0');
        }
        sb.append(leftSeconds);
        return sb.toString();
    }
}
