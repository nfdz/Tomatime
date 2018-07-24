package io.github.nfdz.tomatina;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import io.github.nfdz.tomatina.common.model.PomodoroRealm;
import io.github.nfdz.tomatina.common.model.PomodoroState;
import io.github.nfdz.tomatina.common.utils.RealmUtils;
import io.realm.Realm;
import io.realm.Sort;
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
        clearOutdatedPomodoroIfAny();

        // TODO ask overlay permission to enable overlay by default
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

    private void clearOutdatedPomodoroIfAny() {
        try {
            REALM.beginTransaction();
            PomodoroRealm pomodoroRealm = REALM.where(PomodoroRealm.class)
                    .sort(PomodoroRealm.START_TIME_FIELD, Sort.DESCENDING)
                    .findFirst();
            if (pomodoroRealm != null && pomodoroRealm.isOngoing()) {
                pomodoroRealm.setState(PomodoroState.FINISHED);
            }
            REALM.commitTransaction();
        } catch (Exception e) {
            Timber.e(e, "There was an error clearing outdated pomodoro");
        }
    }

}
