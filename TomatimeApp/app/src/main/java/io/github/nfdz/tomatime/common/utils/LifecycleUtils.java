package io.github.nfdz.tomatime.common.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class LifecycleUtils {


    public static boolean isAppInForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am != null ? am.getRunningAppProcesses() : null;

        if (runningProcesses != null)  {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
