package io.github.nfdz.tomatime.common.utils;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;

import timber.log.Timber;

public class NotificationUtils {

    private static final long[] VIBRATION_PATTERN = new long[]{0, 1000, 100, 200, 100, 200, 100, 200, 100, 200, 100, 1000};
    private static final int[] VIBRATION_AMPLITUDES = new int[]{0, 255, 120, 255, 120, 255, 120, 255, 120, 255, 120, 255};
    private static final long DEMO_TIME_MILLIS = 2000;

    private static Ringtone ONGOING_SOUND;

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

    public static void soundIfNeeded(Context context, String customSoundClipId) {
        clearIfIsNotPlaying();
        try {
            if (ONGOING_SOUND == null) {
                ONGOING_SOUND = RingtoneManager.getRingtone(context, getNotificationSoundUri(context, customSoundClipId));
                ONGOING_SOUND.play();
            }
        } catch (Exception e) {
            Timber.e(e, "Cannot play event ringtone");
        }
    }

    private static void clearIfIsNotPlaying() {
        try {
            if (ONGOING_SOUND != null && ! ONGOING_SOUND.isPlaying()) {
                ONGOING_SOUND.stop();
                ONGOING_SOUND = null;
            }
        } catch (Exception e) {
            Timber.e(e, "Cannot clear ringtone that is not playing");
        }
    }

    public static void muteIfNeeded() {
        try {
            if (ONGOING_SOUND != null) {
                ONGOING_SOUND.stop();
                ONGOING_SOUND = null;
            }
        } catch (Exception e) {
            Timber.e(e, "Cannot mute ongoing ringtone");
        }
    }

    public static void soundDemo(Context context, String customSoundClipId) {
        try {
            final Ringtone r = RingtoneManager.getRingtone(context, getNotificationSoundUri(context, customSoundClipId));
            r.play();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        r.stop();
                    } catch (Exception e) {
                        Timber.e(e, "Cannot stop demo ringtone");
                    }
                }
            }, DEMO_TIME_MILLIS);
        } catch (Exception e) {
            Timber.e(e, "Cannot play demo ringtone");
        }
    }

    public static Uri getNotificationSoundUri(Context context, String soundClipId) {
        if (!TextUtils.isEmpty(soundClipId)) {
            RingtoneManager manager = new RingtoneManager(context);
            manager.setType(RingtoneManager.TYPE_ALARM);
            Cursor cursor = manager.getCursor();
            while (cursor.moveToNext()) {
                if (soundClipId.equals(cursor.getString(RingtoneManager.ID_COLUMN_INDEX))) {
                    return Uri.parse(cursor.getString(RingtoneManager.URI_COLUMN_INDEX)+"/"+cursor.getString(RingtoneManager.ID_COLUMN_INDEX));
                }
            }
        }
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    }

}
