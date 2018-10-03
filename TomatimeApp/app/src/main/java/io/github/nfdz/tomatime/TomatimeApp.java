package io.github.nfdz.tomatime;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import io.github.nfdz.tomatime.common.model.PomodoroRealm;
import io.github.nfdz.tomatime.common.model.PomodoroState;
import io.github.nfdz.tomatime.common.utils.OverlayPermissionHelper;
import io.github.nfdz.tomatime.common.utils.RealmUtils;
import io.github.nfdz.tomatime.common.utils.SettingsPreferencesUtils;
import io.realm.Realm;
import io.realm.Sort;
import timber.log.Timber;

public class TomatimeApp extends Application {

    public static TomatimeApp INSTANCE;
    public static Realm REALM;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        INSTANCE = this;
        setupLogger();
        setupRealm();
        clearOutdatedPomodoroIfAny();
        handleFirstTime();
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

    private void handleFirstTime() {
        if (SettingsPreferencesUtils.getAndSetFirstTimeFlag()) {
            // Enable overlay by default if it is possible
            if (OverlayPermissionHelper.hasOverlayPermission(this)) {
                SettingsPreferencesUtils.setOverlayViewFlag(true);
            }
        }
    }

}
