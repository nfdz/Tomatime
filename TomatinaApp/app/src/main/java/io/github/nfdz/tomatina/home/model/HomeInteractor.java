package io.github.nfdz.tomatina.home.model;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import io.github.nfdz.tomatina.TomatinaApp;
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
    public void savePomodoroInfo(final long id, final String title, final String notes, final SaveInfoCallback callback) {
        TomatinaApp.REALM.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                PomodoroRealm pomodoro = realm.where(PomodoroRealm.class).equalTo(PomodoroRealm.ID_FIELD, id).findFirst();
                pomodoro.setTitle(title);
                pomodoro.setNotes(notes);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                Timber.e(error, "Problem saving pomodoro notes");
            }
        });
    }

}
