package io.github.nfdz.tomatime;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.fabric.sdk.android.Fabric;
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

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        INSTANCE = this;
        setupLogger();
        setupRealm();
        setupCrashlytics();
        setupAnalytics();
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

    private void setupCrashlytics() {
        // Set up Crashlytics, disabled for debug builds
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        // Initialize Fabric with the debug-disabled crashlytics
        Fabric.with(this, crashlyticsKit);

        // Debug
//        Crashlytics crashlyticsKit = new Crashlytics.Builder()
//                .core(new CrashlyticsCore.Builder().build())
//                .build();
//        final Fabric fabric = new Fabric.Builder(this)
//                .kits(crashlyticsKit)
//                .debuggable(true)
//                .build();
//        Fabric.with(fabric);
    }

    private void setupAnalytics() {
        if (!BuildConfig.DEBUG) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }
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

    public void logAnalytics(@NonNull @Size(min = 1L,max = 40L) String event) {
        if (!BuildConfig.DEBUG && firebaseAnalytics != null) {
            firebaseAnalytics.logEvent(event, null);
        } else {
            Timber.i("AnalyticsDebug: " + event);
        }
    }

    public void reportException(@NonNull Exception ex) {
        if (BuildConfig.DEBUG) {
            Timber.w("CrashlyticsReportDebug", ex);
        } else {
            Crashlytics.logException(ex);
        }
    }

}
