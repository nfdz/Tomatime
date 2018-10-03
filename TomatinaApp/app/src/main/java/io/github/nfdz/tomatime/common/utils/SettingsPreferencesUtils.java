package io.github.nfdz.tomatime.common.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import io.github.nfdz.tomatime.R;
import io.github.nfdz.tomatime.TomatimeApp;

public class SettingsPreferencesUtils {

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(TomatimeApp.INSTANCE);
    }

    /**
     * This method restores default preferences of a pomodoro. It is
     * working time, short break time, long break time and pomodoros to long break.
     */
    public static void restoreDefaultSettings() {
        SharedPreferences.Editor editor = getPreferences().edit();

        int pomodoroTimeDefault = Integer.parseInt(TomatimeApp.INSTANCE.getString(R.string.pref_pomodoro_time_default));
        String pomodoroTimeKey = TomatimeApp.INSTANCE.getString(R.string.pref_pomodoro_time_key);
        editor.putInt(pomodoroTimeKey, pomodoroTimeDefault);

        int shortBreakTimeDefault = Integer.parseInt(TomatimeApp.INSTANCE.getString(R.string.pref_short_break_time_default));
        String shortBreakTimeKey = TomatimeApp.INSTANCE.getString(R.string.pref_short_break_time_key);
        editor.putInt(shortBreakTimeKey, shortBreakTimeDefault);

        int longBreakTimeDefault = Integer.parseInt(TomatimeApp.INSTANCE.getString(R.string.pref_long_break_time_default));
        String longBreakTimeKey = TomatimeApp.INSTANCE.getString(R.string.pref_long_break_time_key);
        editor.putInt(longBreakTimeKey, longBreakTimeDefault);

        int pomodorosToLongBreakDefault = Integer.parseInt(TomatimeApp.INSTANCE.getString(R.string.pref_pomodoros_to_long_break_default));
        String pomodorosToLongBreakKey = TomatimeApp.INSTANCE.getString(R.string.pref_pomodoros_to_long_break_key);
        editor.putInt(pomodorosToLongBreakKey, pomodorosToLongBreakDefault);

        editor.apply();
    }

    public static long getPomodoroTimeInMillis() {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_pomodoro_time_key);
        int defaultValue = Integer.parseInt(TomatimeApp.INSTANCE.getString(R.string.pref_pomodoro_time_default));
        int pomodoroTimeMinutes = getPreferences().getInt(key, defaultValue);
        return TimeUnit.MINUTES.toMillis(pomodoroTimeMinutes);
    }

    public static long getShortBreakTimeInMillis() {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_short_break_time_key);
        int defaultValue = Integer.parseInt(TomatimeApp.INSTANCE.getString(R.string.pref_short_break_time_default));
        int shortBreakTimeMinutes = getPreferences().getInt(key, defaultValue);
        return TimeUnit.MINUTES.toMillis(shortBreakTimeMinutes);
    }

    public static long getLongBreakTimeInMillis() {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_long_break_time_key);
        int defaultValue = Integer.parseInt(TomatimeApp.INSTANCE.getString(R.string.pref_long_break_time_default));
        int longBreakTimeMinutes = getPreferences().getInt(key, defaultValue);
        return TimeUnit.MINUTES.toMillis(longBreakTimeMinutes);
    }

    public static int getPomodorosToLongBreak() {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_pomodoros_to_long_break_key);
        int defaultValue = Integer.parseInt(TomatimeApp.INSTANCE.getString(R.string.pref_pomodoros_to_long_break_default));
        return getPreferences().getInt(key, defaultValue);
    }

    public static boolean getSoundEnabledFlag() {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_sound_key);
        boolean defaultValue = Boolean.parseBoolean(TomatimeApp.INSTANCE.getString(R.string.pref_sound_default));
        return getPreferences().getBoolean(key, defaultValue);
    }

    public static String getCustomSoundId() {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_sound_custom_key);
        return getPreferences().getString(key, "");
    }

    public static boolean getVibrationEnabledFlag() {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_vibration_key);
        boolean defaultValue = Boolean.parseBoolean(TomatimeApp.INSTANCE.getString(R.string.pref_vibration_default));
        return getPreferences().getBoolean(key, defaultValue);
    }

    public static boolean getInsistAlertFlag() {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_insist_key);
        boolean defaultValue = Boolean.parseBoolean(TomatimeApp.INSTANCE.getString(R.string.pref_insist_default));
        return getPreferences().getBoolean(key, defaultValue);
    }

    public static boolean getOverlayViewFlag() {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_overlay_key);
        boolean defaultValue = Boolean.parseBoolean(TomatimeApp.INSTANCE.getString(R.string.pref_overlay_default));
        return getPreferences().getBoolean(key, defaultValue);
    }

    public static void setOverlayViewFlag(boolean enabled) {
        String key = TomatimeApp.INSTANCE.getString(R.string.pref_overlay_key);
        getPreferences().edit().putBoolean(key, enabled).apply();
    }

    public static boolean getAndSetFirstTimeFlag() {
        String key = "first_time";
        boolean value = getPreferences().getBoolean(key, true);
        if (value) {
            getPreferences().edit().putBoolean(key, false).apply();
        }
        return value;
    }

}
