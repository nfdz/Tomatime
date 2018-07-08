package io.github.nfdz.tomatina;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import io.realm.Realm;
import timber.log.Timber;

public class TomatinaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }

        Realm.init(this);

    }

}
