package io.github.nfdz.tomatime.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;

public class OverlayPermissionHelper {

    private static final int OVERLAY_PERMISSION_REQUEST = 5634;

    public interface Callback {
        void onPermissionsGranted();
        void onPermissionsDenied();
    }

    private final Callback callback;
    private final Fragment fragment;

    public OverlayPermissionHelper(Fragment fragment, Callback callback) {
        this.callback = callback;
        this.fragment = fragment;
    }

    public void request() {
        if (!hasOverlayPermission()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + fragment.getActivity().getPackageName()));
            fragment.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST);
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
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(fragment.getActivity());
    }

    public static boolean hasOverlayPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

}
