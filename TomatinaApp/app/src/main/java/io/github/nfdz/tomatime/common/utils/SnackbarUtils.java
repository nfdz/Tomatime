package io.github.nfdz.tomatime.common.utils;

import android.graphics.Typeface;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import io.github.nfdz.tomatime.R;
import io.github.nfdz.tomatime.TomatimeApp;
import timber.log.Timber;

public class SnackbarUtils {

    public static void show(View view, @StringRes int stringRes, int duration) {
        try {
            Snackbar snack = Snackbar.make(view, stringRes, duration);
            View snackView = snack.getView();
            snackView.setBackgroundColor(ContextCompat.getColor(TomatimeApp.INSTANCE, R.color.colorAccent));
            TextView tv = snackView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            tv.setTextColor(ContextCompat.getColor(TomatimeApp.INSTANCE, R.color.dark));
            tv.setMaxLines(4);
            snack.show();
        } catch (Exception e) {
            Timber.e(e, "There was a problem showing snack bar");
        }
    }

}
