package io.github.nfdz.tomatime.home.model;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import io.github.nfdz.tomatime.TomatimeApp;
import io.github.nfdz.tomatime.common.model.PomodoroRealm;
import io.github.nfdz.tomatime.common.utils.OverlayPermissionHelper;
import io.github.nfdz.tomatime.common.utils.RealmUtils;
import io.github.nfdz.tomatime.common.utils.SettingsPreferencesUtils;
import io.github.nfdz.tomatime.home.HomeContract;
import io.github.nfdz.tomatime.service.PomodoroService;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

public class HomeInteractor implements HomeContract.Interactor {

    @Override
    public void initialize() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean handleFirstTime() {
        boolean isFirstTime = SettingsPreferencesUtils.getAndSetFirstTimeFlag();
        if (isFirstTime) {
            // Enable overlay by default if it is possible
            if (OverlayPermissionHelper.hasOverlayPermission(TomatimeApp.INSTANCE)) {
                SettingsPreferencesUtils.setOverlayViewFlag(true);
            }
        }
        return isFirstTime;
    }

    @Override
    public LiveData<RealmResults<PomodoroRealm>> loadDataAsync() {
        return RealmUtils.asLiveData(TomatimeApp.REALM.where(PomodoroRealm.class)
                .sort(PomodoroRealm.START_TIME_FIELD, Sort.DESCENDING)
                .findAllAsync());
    }

    @Override
    public void startPomodoro() {
        PomodoroService.startPomodoro(TomatimeApp.INSTANCE);
    }

    @Override
    public void stopPomodoro() {
        PomodoroService.stopPomodoro(TomatimeApp.INSTANCE);
    }

    @Override
    public void skipStage() {
        PomodoroService.skipStage(TomatimeApp.INSTANCE);
    }

    @Override
    public void skipFinishStage() {
        PomodoroService.skipFinishStage(TomatimeApp.INSTANCE);
    }

    @Override
    public void savePomodoroInfo(final long id,
                                 final String title,
                                 final String notes,
                                 final String category,
                                 final boolean solveConflict,
                                 final boolean overwriteIfNeed,
                                 final SaveInfoCallback callback) {
        TomatimeApp.REALM.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmUtils.savePomodoroInfo(realm, id, title, notes, category, solveConflict, overwriteIfNeed);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                if (error instanceof RealmUtils.ConflictException) {
                    callback.onConflict();
                } else {
                    Timber.e(error, "Problem saving pomodoro info");
                    callback.onError();
                }
            }
        });
    }



}
