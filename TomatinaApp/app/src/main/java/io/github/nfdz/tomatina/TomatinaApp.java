package io.github.nfdz.tomatina;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import io.github.nfdz.tomatina.common.utils.RealmUtils;
import io.realm.Realm;
import timber.log.Timber;

public class TomatinaApp extends Application {

    public static TomatinaApp INSTANCE;
    public static Realm REALM;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        INSTANCE = this;
        setupLogger();
        setupRealm();
    }

    private void setupLogger() {
        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void setupRealm() {
        Realm.init(this);
        Realm.setDefaultConfiguration(RealmUtils.getConfiguration());
        REALM = Realm.getDefaultInstance();
    }

}
