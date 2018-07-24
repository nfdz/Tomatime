package io.github.nfdz.tomatina.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class OverlayPermissionHelper {

    private static final int OVERLAY_PERMISSION_REQUEST = 5634;

    public interface Callback {
        void onPermissionsGranted();
        void onPermissionsDenied();
    }

    private final Callback callback;
    private final Activity activity;

    public OverlayPermissionHelper(Activity activity, Callback callback) {
        this.callback = callback;
        this.activity = activity;
    }

    public void request() {
        if (!hasOverlayPermission()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST);
        } else {
            callback.onPermissionsGranted();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST) {
            if (hasOverlayPermission()) {
                callback.onPermissionsGranted();
            } else {
                callback.onPermissionsDenied();
            }
        }
    }

    public boolean hasOverlayPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(activity);
    }

    public static boolean hasOverlayPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

}
