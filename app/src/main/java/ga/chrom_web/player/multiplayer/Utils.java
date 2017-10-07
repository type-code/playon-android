package ga.chrom_web.player.multiplayer;


import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class Utils {

    public static void debugLog(String log) {
        boolean isDebug = true;
        if (isDebug) {
            Log.d("Logs", log);
        }
    }

    public static String formatTimeMilliseconds(int milliseconds) {
        return formatTimeSeconds(milliseconds / 1000);
    }

    public static String formatTimeSeconds(int seconds) {
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

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
