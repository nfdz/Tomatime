package io.github.nfdz.tomatina.common.utils;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;

import timber.log.Timber;

public class NotificationUtils {

    private static final long[] VIBRATION_PATTERN = new long[]{0, 1000, 100, 200, 100, 200, 100, 200, 100, 200, 100, 1000};
    private static final  int[] VIBRATION_AMPLITUDES = new int[]{0, 255, 120, 255, 120, 255, 120, 255, 120, 255, 120, 255};

    public static void vibrate(Vibrator vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator.hasAmplitudeControl()) {
                VibrationEffect effect = VibrationEffect.createWaveform(VIBRATION_PATTERN, VIBRATION_AMPLITUDES, -1);
                vibrator.vibrate(effect);
            } else {
                VibrationEffect effect = VibrationEffect.createWaveform(VIBRATION_PATTERN, -1);
                vibrator.vibrate(effect);
            }
        } else {
            vibrator.vibrate(VIBRATION_PATTERN, -1);
        }
    }

    public static void sound(Context context, String customSoundClipId) {
        try {
            Ringtone r = RingtoneManager.getRingtone(context, getNotificationSoundUri(context, customSoundClipId));
            r.play();
        } catch (Exception e) {
            Timber.e(e, "Cannot play event ringtone");
        }
    }

    public static Uri getNotificationSoundUri(Context context, String soundClipId) {
        if (!TextUtils.isEmpty(soundClipId)) {
            RingtoneManager manager = new RingtoneManager(context);
            manager.setType(RingtoneManager.TYPE_NOTIFICATION);
            Cursor cursor = manager.getCursor();
            while (cursor.moveToNext()) {
                if (soundClipId.equals(cursor.getString(RingtoneManager.ID_COLUMN_INDEX))) {
                    return Uri.parse(cursor.getString(RingtoneManager.URI_COLUMN_INDEX)+"/"+cursor.getString(RingtoneManager.ID_COLUMN_INDEX));
                }
            }
        }
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

}
