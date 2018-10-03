package io.github.nfdz.tomatime.settings.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import io.github.nfdz.tomatime.R;

public class DarkPreferenceCategory extends android.support.v7.preference.PreferenceCategory {

    public DarkPreferenceCategory(Context context) {
        super(context);
    }

    public DarkPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DarkPreferenceCategory(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView titleView = (TextView) holder.findViewById(android.R.id.title);
        titleView.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorDark));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int horizontal = getContext().getResources().getDimensionPixelSize(R.dimen.content_horizontal_padding);
            int vertical = getContext().getResources().getDimensionPixelSize(R.dimen.content_vertical_padding);
            titleView.setPadding(horizontal, vertical, 0, 0);
        }
    }

}