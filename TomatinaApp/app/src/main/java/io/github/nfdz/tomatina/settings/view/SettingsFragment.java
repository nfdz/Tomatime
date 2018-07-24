package io.github.nfdz.tomatina.settings.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.common.utils.OverlayPermissionHelper;
import io.github.nfdz.tomatina.common.utils.SettingsPreferencesUtils;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, OverlayPermissionHelper.Callback {

    private OverlayPermissionHelper overlayPermissionHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overlayPermissionHelper = new OverlayPermissionHelper(getActivity(), this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        setupRestoreButton();
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
            ((SwitchPreferenceCompat) findPreference(getString(R.string.pref_overlay_key))).setChecked(overlayEnabled);
            if (overlayEnabled && !overlayPermissionHelper.hasOverlayPermission()) {
                sharedPreferences.edit().putBoolean(key, false).apply();

                // TODO explain permission before request
                overlayPermissionHelper.request();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overlayPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPermissionsGranted() {
        SettingsPreferencesUtils.setOverlayViewFlag(true);

        // TODO set preference manually?
    }

    @Override
    public void onPermissionsDenied() {
        SettingsPreferencesUtils.setOverlayViewFlag(false);
    }
}
