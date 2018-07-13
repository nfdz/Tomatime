package io.github.nfdz.tomatina.common.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.TomatinaApp;

public class SettingsPreferencesUtils {

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(TomatinaApp.INSTANCE);
    }

    /**
     * This method restores default preferences of a pomodoro. It is
     * working time, short break time, long break time and pomodoros to long break.
     */
    public static void restoreDefaultSettings() {
        SharedPreferences.Editor editor = getPreferences().edit();

        int pomodoroTimeDefault = Integer.parseInt(TomatinaApp.INSTANCE.getString(R.string.pref_pomodoro_time_default));
        String pomodoroTimeKey = TomatinaApp.INSTANCE.getString(R.string.pref_pomodoro_time_key);
        editor.putInt(pomodoroTimeKey, pomodoroTimeDefault);

        int shortBreakTimeDefault = Integer.parseInt(TomatinaApp.INSTANCE.getString(R.string.pref_short_break_time_default));
        String shortBreakTimeKey = TomatinaApp.INSTANCE.getString(R.string.pref_short_break_time_key);
        editor.putInt(shortBreakTimeKey, shortBreakTimeDefault);

        int longBreakTimeDefault = Integer.parseInt(TomatinaApp.INSTANCE.getString(R.string.pref_long_break_time_default));
        String longBreakTimeKey = TomatinaApp.INSTANCE.getString(R.string.pref_long_break_time_key);
        editor.putInt(longBreakTimeKey, longBreakTimeDefault);

        int pomodorosToLongBreakDefault = Integer.parseInt(TomatinaApp.INSTANCE.getString(R.string.pref_pomodoros_to_long_break_default));
        String pomodorosToLongBreakKey = TomatinaApp.INSTANCE.getString(R.string.pref_pomodoros_to_long_break_key);
        editor.putInt(pomodorosToLongBreakKey, pomodorosToLongBreakDefault);

        editor.apply();
    }

    public static long getPomodoroTimeInMillis() {
        String key = TomatinaApp.INSTANCE.getString(R.string.pref_pomodoro_time_key);
        int defaultValue = Integer.parseInt(TomatinaApp.INSTANCE.getString(R.string.pref_pomodoro_time_default));
        int pomodoroTimeMinutes = getPreferences().getInt(key, defaultValue);
        return TimeUnit.MINUTES.toMillis(pomodoroTimeMinutes);
    }

    public static long getShortBreakTimeInMillis() {
        String key = TomatinaApp.INSTANCE.getString(R.string.pref_short_break_time_key);
        int defaultValue = Integer.parseInt(TomatinaApp.INSTANCE.getString(R.string.pref_short_break_time_default));
        int shortBreakTimeMinutes = getPreferences().getInt(key, defaultValue);
        return TimeUnit.MINUTES.toMillis(shortBreakTimeMinutes);
    }

    public static long getLongBreakTimeInMillis() {
        String key = TomatinaApp.INSTANCE.getString(R.string.pref_long_break_time_key);
        int defaultValue = Integer.parseInt(TomatinaApp.INSTANCE.getString(R.string.pref_long_break_time_default));
        int longBreakTimeMinutes = getPreferences().getInt(key, defaultValue);
        return TimeUnit.MINUTES.toMillis(longBreakTimeMinutes);
    }

    public static int getPomodorosToLongBreak() {
        String key = TomatinaApp.INSTANCE.getString(R.string.pref_pomodoros_to_long_break_key);
        int defaultValue = Integer.parseInt(TomatinaApp.INSTANCE.getString(R.string.pref_pomodoros_to_long_break_default));
        return getPreferences().getInt(key, defaultValue);
    }

    public static boolean getSoundEnabledFlag() {
        String key = TomatinaApp.INSTANCE.getString(R.string.pref_sound_key);
        boolean defaultValue = Boolean.parseBoolean(TomatinaApp.INSTANCE.getString(R.string.pref_sound_default));
        return getPreferences().getBoolean(key, defaultValue);
    }

    public static boolean getVibrationEnabledFlag() {
        String key = TomatinaApp.INSTANCE.getString(R.string.pref_vibration_key);
        boolean defaultValue = Boolean.parseBoolean(TomatinaApp.INSTANCE.getString(R.string.pref_vibration_default));
        return getPreferences().getBoolean(key, defaultValue);
    }

}
