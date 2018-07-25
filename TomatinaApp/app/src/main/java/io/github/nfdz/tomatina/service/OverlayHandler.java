package io.github.nfdz.tomatina.service;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.main.view.MainActivity;
import timber.log.Timber;

public class OverlayHandler implements View.OnClickListener {

    private final Context context;
    private WindowManager.LayoutParams viewLayoutParams;
    private View lockedOverlay = null;

    public OverlayHandler(Context context) {
        this.context = context.getApplicationContext();
        initializeLayoutParams();
    }

    private void initializeLayoutParams() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            viewLayoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
        } else {
            viewLayoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
        }
        viewLayoutParams.gravity = Gravity.TOP | Gravity.START;
    }


    public void hide() {
        if (lockedOverlay != null) {
            try {
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                windowManager.removeView(lockedOverlay);
                lockedOverlay = null;
            } catch (Exception e) {
                Timber.e(e, "Cannot hide overlay view");
            }
        }
    }

    public void show() {
        try {
            if (lockedOverlay == null) {
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                LayoutInflater inflater = LayoutInflater.from(context);
                lockedOverlay = inflater.inflate(R.layout.view_overlay, null);
                lockedOverlay.setOnClickListener(this);
                windowManager.addView(lockedOverlay, viewLayoutParams);
            }
        } catch (Exception e) {
            Timber.e(e, "Cannot show overlay view");
        }
    }

    @Override
    public void onClick(View v) {
        try {
            MainActivity.start(context);
        } catch (Exception e) {
            Timber.e(e, "Cannot handle click event going to main activity");
        }
    }

}
