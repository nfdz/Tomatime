package io.github.nfdz.tomatime.settings.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

import io.github.nfdz.tomatime.R;

public class NumberPickerPreference extends DialogPreference {

    private static final int DEFAULT_MAX_VALUE = 60;
    private static final int DEFAULT_MIN_VALUE = 1;
    private static final String DEFAULT_SUM_SUFFIX = "";

    private int value;
    private int minValue;
    private int maxValue;
    private String suffix;

    public NumberPickerPreference(Context context) {
        this(context, null);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.number_picker_attrs);
        maxValue = a.getInteger(R.styleable.number_picker_attrs_max_number, DEFAULT_MAX_VALUE);
        minValue = a.getInteger(R.styleable.number_picker_attrs_min_number, DEFAULT_MIN_VALUE);
        String suffix = a.getString(R.styleable.number_picker_attrs_suffix);
        this.suffix = TextUtils.isEmpty(suffix) ? DEFAULT_SUM_SUFFIX : suffix;
        a.recycle();
    }

    @Override
    public CharSequence getSummary() {
        return Integer.toString(value) + " " + suffix;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, minValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(minValue) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
        notifyChanged();
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.dialog_preference_number_picker;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getValue() {
        return value;
    }

    public String getSuffix() {
        return suffix;
    }

}