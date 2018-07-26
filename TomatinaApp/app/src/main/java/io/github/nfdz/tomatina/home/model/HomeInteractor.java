package io.github.nfdz.tomatina.home.model;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import io.github.nfdz.tomatina.TomatinaApp;
import io.github.nfdz.tomatina.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatina.common.model.PomodoroRealm;
import io.github.nfdz.tomatina.common.utils.RealmUtils;
import io.github.nfdz.tomatina.home.HomeContract;
import io.github.nfdz.tomatina.service.PomodoroService;
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
    public LiveData<RealmResults<PomodoroRealm>> loadDataAsync() {
        return RealmUtils.asLiveData(TomatinaApp.REALM.where(PomodoroRealm.class)
                .sort(PomodoroRealm.START_TIME_FIELD, Sort.DESCENDING)
                .findAllAsync());
    }

    @Override
    public void startPomodoro() {
        PomodoroService.startPomodoro(TomatinaApp.INSTANCE);
    }

    @Override
    public void stopPomodoro() {
        PomodoroService.stopPomodoro(TomatinaApp.INSTANCE);
    }

    @Override
    public void skipStage() {
        PomodoroService.skipStage(TomatinaApp.INSTANCE);
    }

    @Override
    public void savePomodoroInfo(final long id,
                                 final String title,
                                 final String notes,
                                 final String category,
                                 final boolean solveConflict,
                                 final boolean overwriteIfNeed,
                                 final SaveInfoCallback callback) {
        TomatinaApp.REALM.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                String key = PomodoroInfoRealm.buildKeyFromTitle(title);
                PomodoroRealm pomodoro = realm.where(PomodoroRealm.class).equalTo(PomodoroRealm.ID_FIELD, id).findFirst();
                PomodoroInfoRealm currentInfo = pomodoro.getPomodoroInfo();
                if (currentInfo != null) {
                    replacePomodoroInfoIfNeeded(realm, pomodoro, currentInfo, key, title, notes, category, solveConflict, overwriteIfNeed);
                } else {
                    replacePomodoroInfo(realm, pomodoro, null, key, title, notes, category, solveConflict, overwriteIfNeed);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                if (error instanceof ConflictException) {
                    callback.onConflict();
                } else {
                    Timber.e(error, "Problem saving pomodoro info");
                    callback.onError();
                }
            }
        });
    }

    private void replacePomodoroInfoIfNeeded(Realm realm,
                                     PomodoroRealm pomodoro,
                                     PomodoroInfoRealm currentInfo,
                                     String key,
                                     String title,
                                     String notes,
                                     String category,
                                     boolean solveConflict,
                                     boolean overwriteIfNeed) {
        boolean hasDifferentKey = !key.equals(currentInfo.getKey());
        if (hasDifferentKey) {
            replacePomodoroInfo(realm, pomodoro, currentInfo, key, title, notes, category, solveConflict, overwriteIfNeed);
        } else {
            setPomodoroInfo(currentInfo, title, notes, category);
        }
    }

    private void replacePomodoroInfo(Realm realm,
                                               PomodoroRealm pomodoro,
                                               PomodoroInfoRealm currentInfo,
                                               String key,
                                               String title,
                                               String notes,
                                               String category,
                                               boolean solveConflict,
                                               boolean overwriteIfNeed) {
        PomodoroInfoRealm conflictInfo = realm.where(PomodoroInfoRealm.class).equalTo(PomodoroInfoRealm.KEY_FIELD, key).findFirst();
        if (conflictInfo != null) {
            if (solveConflict) {
                deletePomodoroInfoIfUseless(realm, currentInfo);
                pomodoro.setPomodoroInfo(conflictInfo);
                if (overwriteIfNeed) {
                    setPomodoroInfo(conflictInfo, title, notes, category);
                }
            } else {
                throw new ConflictException();
            }
        } else {
            deletePomodoroInfoIfUseless(realm, currentInfo);
            PomodoroInfoRealm newInfo = realm.createObject(PomodoroInfoRealm.class, key);
            setPomodoroInfo(newInfo, title, notes, category);
            pomodoro.setPomodoroInfo(newInfo);
        }
    }

    private void setPomodoroInfo(PomodoroInfoRealm info,
                                 String title,
                                 String notes,
                                 String category) {
        info.setTitle(title);
        info.setCategory(category);
        info.setNotes(notes);
    }

    private void deletePomodoroInfoIfUseless(Realm realm, PomodoroInfoRealm info) {
        if (info != null) {
            RealmResults<PomodoroRealm> results = realm.where(PomodoroRealm.class)
                    .equalTo(PomodoroRealm.INFO_FIELD + "." + PomodoroInfoRealm.KEY_FIELD, info.getKey())
                    .findAll();
            if (results == null || results.size() < 2) {
                info.deleteFromRealm();
            }
        }
    }

    public static class ConflictException extends RuntimeException {
        static final long serialVersionUID = 1L;
    }

}
