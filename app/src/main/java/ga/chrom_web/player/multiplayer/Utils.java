package ga.chrom_web.player.multiplayer;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {

    public static void debugLog(String log) {
        boolean isDebug = true;
        if (isDebug) {
            Log.d("Logs", log);
        }
    }

    public static float dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static int spToPx(Context context, float sp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, r.getDisplayMetrics());
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
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
}
