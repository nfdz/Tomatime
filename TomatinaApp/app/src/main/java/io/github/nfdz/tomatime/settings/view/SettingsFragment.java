package io.github.nfdz.tomatime.settings.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

import java.util.ArrayList;

import io.github.nfdz.tomatime.R;
import io.github.nfdz.tomatime.common.utils.NotificationUtils;
import io.github.nfdz.tomatime.common.utils.OverlayPermissionHelper;
import io.github.nfdz.tomatime.common.utils.SettingsPreferencesUtils;
import timber.log.Timber;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, OverlayPermissionHelper.Callback {

    private OverlayPermissionHelper overlayPermissionHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overlayPermissionHelper = new OverlayPermissionHelper(this, this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        setupView();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupView() {
        setupRestoreButton();
        setupAvailableSounds();
    }

    private void setupRestoreButton() {
        Preference restoreDefaultPrefs = findPreference(getString(R.string.pref_restore_default_key));
        restoreDefaultPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SettingsPreferencesUtils.restoreDefaultSettings();
                return true;
            }
        });
    }

    private void setupAvailableSounds() {
        RingtoneManager manager = new RingtoneManager(getActivity());
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        String defaultSound = getString(R.string.pref_sound_custom_default);
        names.add(defaultSound);
        ids.add(defaultSound);
        while (cursor.moveToNext()) {
            String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            String name = cursor.getString((RingtoneManager.TITLE_COLUMN_INDEX));
            names.add(name);
            ids.add(id);
        }
        ListPreference customSoundPref = (ListPreference) findPreference(getString(R.string.pref_sound_custom_key));
        customSoundPref.setEntries(names.toArray(new String[]{}));
        customSoundPref.setEntryValues(ids.toArray(new String[]{}));
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof NumberPickerPreference) {
            dialogFragment = NumberPickerPreferenceDialog.newInstance(preference.getKey());
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_pomodoro_time_key))) {
            int defaultValue = Integer.parseInt(getString(R.string.pref_pomodoro_time_default));
            ((NumberPickerPreference) findPreference(key)).setValue(sharedPreferences.getInt(key, defaultValue));
        } else if (key.equals(getString(R.string.pref_short_break_time_key))) {
            int defaultValue = Integer.parseInt(getString(R.string.pref_short_break_time_default));
            ((NumberPickerPreference) findPreference(key)).setValue(sharedPreferences.getInt(key, defaultValue));
        } else if (key.equals(getString(R.string.pref_long_break_time_key))) {
            int defaultValue = Integer.parseInt(getString(R.string.pref_long_break_time_default));
            ((NumberPickerPreference) findPreference(key)).setValue(sharedPreferences.getInt(key, defaultValue));
        } else if (key.equals(getString(R.string.pref_pomodoros_to_long_break_key))) {
            int defaultValue = Integer.parseInt(getString(R.string.pref_pomodoros_to_long_break_default));
            ((NumberPickerPreference) findPreference(key)).setValue(sharedPreferences.getInt(key, defaultValue));
        } else if (key.equals(getString(R.string.pref_overlay_key))) {
            boolean defaultValue = Boolean.parseBoolean(getString(R.string.pref_overlay_default));
            boolean overlayEnabled = sharedPreferences.getBoolean(key, defaultValue);
            if (overlayEnabled && !overlayPermissionHelper.hasOverlayPermission()) {
                overlayEnabled = false;
                sharedPreferences.edit().putBoolean(key, overlayEnabled).apply();

                // TODO explain permission before request
                overlayPermissionHelper.request();
            }
            ((SwitchPreferenceCompat) findPreference(getString(R.string.pref_overlay_key))).setChecked(overlayEnabled);
        } else if (key.equals(getString(R.string.pref_sound_custom_key))) {
            NotificationUtils.sound(getActivity(), SettingsPreferencesUtils.getCustomSoundId());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overlayPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPermissionsGranted() {
        ((SwitchPreferenceCompat) findPreference(getString(R.string.pref_overlay_key))).setChecked(true);
        SettingsPreferencesUtils.setOverlayViewFlag(true);
    }

    @Override
    public void onPermissionsDenied() {
        ((SwitchPreferenceCompat) findPreference(getString(R.string.pref_overlay_key))).setChecked(false);
        SettingsPreferencesUtils.setOverlayViewFlag(false);
    }

}
