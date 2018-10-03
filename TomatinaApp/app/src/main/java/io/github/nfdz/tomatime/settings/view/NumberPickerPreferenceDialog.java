package io.github.nfdz.tomatime.settings.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nfdz.tomatime.R;

public class NumberPickerPreferenceDialog extends PreferenceDialogFragmentCompat {

    public static NumberPickerPreferenceDialog newInstance(String key) {
        final NumberPickerPreferenceDialog fragment = new NumberPickerPreferenceDialog();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    private static final boolean WRAP_SELECTOR_WHEEL = true;

    @BindView(R.id.dialog_number_picker) NumberPicker dialog_number_picker;
    @BindView(R.id.dialog_tv_suffix) TextView dialog_tv_suffix;

    private NumberPickerPreference numberPickerPreference;

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.getContext().setTheme(R.style.AppAlertDialog);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        DialogPreference preference = getPreference();
        if (preference instanceof NumberPickerPreference) {
            ButterKnife.bind(this, view);
            setDividerAccentColor(dialog_number_picker);
            numberPickerPreference = ((NumberPickerPreference) preference);
            dialog_number_picker.setMinValue(numberPickerPreference.getMinValue());
            dialog_number_picker.setMaxValue(numberPickerPreference.getMaxValue());
            dialog_number_picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
            dialog_number_picker.setValue(numberPickerPreference.getValue());
            dialog_tv_suffix.setText(numberPickerPreference.getSuffix());
        } else {
            throw new IllegalStateException("Dialog view must be used by NumberPickerPreference");
        }
    }

    private void setDividerAccentColor(NumberPicker picker) {
        try {
            java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (java.lang.reflect.Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    pf.set(picker, colorDrawable);
                    break;
                }
            }
        } catch (Exception e) {
            // swallow
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            dialog_number_picker.clearFocus();
            int newValue = dialog_number_picker.getValue();
            if (numberPickerPreference.callChangeListener(newValue)) {
                numberPickerPreference.setValue(newValue);
            }
        }
    }
}
