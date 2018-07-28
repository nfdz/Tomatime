package io.github.nfdz.tomatina.historical.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.TomatinaApp;
import io.github.nfdz.tomatina.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatina.common.model.PomodoroRealm;
import io.github.nfdz.tomatina.common.utils.RealmUtils;
import io.github.nfdz.tomatina.historical.HistoricalContract;
import io.github.nfdz.tomatina.service.PomodoroService;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import timber.log.Timber;

public class HistoricalInteractor implements HistoricalContract.Interactor, RealmChangeListener<RealmResults<PomodoroRealm>> {

    private DataListener listener;
    private RealmResults<PomodoroRealm> observedData;

    @Override
    public void initialize(DataListener listener) {
        this.listener = listener;
        if (observedData == null) {
            observedData = TomatinaApp.REALM.where(PomodoroRealm.class).findAllAsync();
            observedData.addChangeListener(this);
        }
    }

    @Override
    public void destroy() {
        if (observedData != null) {
            observedData.removeChangeListener(this);
            observedData = null;
        }
    }

    @Override
    public void onChange(@NonNull RealmResults<PomodoroRealm> pomodoroRealms) {
        if (listener != null) {
            Timber.d("There is some changes, reloading historical data...");
            loadDataAsync();
        }
    }

    private void loadDataAsync() {
        final PomodoroInfoRealm noInfo = new PomodoroInfoRealm();
        noInfo.setTitle(TomatinaApp.INSTANCE.getString(R.string.historical_no_info_title));
        final Set<String> categories = new HashSet<>();
        final SortedMap<PomodoroInfoRealm,List<PomodoroRealm>> data = new TreeMap<>();
        TomatinaApp.REALM.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<PomodoroRealm> managedPomodoros = realm.where(PomodoroRealm.class).findAll();
                List<PomodoroRealm> pomodoros = realm.copyFromRealm(managedPomodoros);
                for (PomodoroRealm pomodoro : pomodoros) {
                    PomodoroInfoRealm info = pomodoro.getPomodoroInfo();
                    if (info == null) {
                        info = noInfo;
                    } else {
                        categories.add(info.getCategory());
                    }
                    List<PomodoroRealm> pomodorosOfInfo = data.get(info);
                    if (pomodorosOfInfo == null) {
                        pomodorosOfInfo = new ArrayList<>();
                        data.put(info, pomodorosOfInfo);
                    }
                    pomodorosOfInfo.add(pomodoro);
                }
                categories.remove("");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                listener.onNotifyData(categories, data);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                Timber.e(error, "Problem loading pomodoros");
            }
        });
    }

    @Override
    public void startPomodoro(PomodoroInfoRealm info) {
        PomodoroService.stopPomodoro(TomatinaApp.INSTANCE);
        PomodoroService.startPomodoro(TomatinaApp.INSTANCE, info);
    }

    @Override
    public void deletePomodoros(final PomodoroInfoRealm info, final DeleteCallback callback) {
        TomatinaApp.REALM.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<PomodoroRealm> results = realm.where(PomodoroRealm.class)
                        .equalTo(PomodoroRealm.INFO_FIELD + "." + PomodoroInfoRealm.KEY_FIELD, info.getKey())
                        .findAll();
                if (results != null && !results.isEmpty()) {
                    for (PomodoroRealm pomodoro : results) {
                        if (!pomodoro.isOngoing()) {
                            pomodoro.deleteFromRealm();
                        }
                    }
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
                Timber.e(error, "Problem deleting pomodoros");
                callback.onError();
            }
        });
    }

    @Override
    public void savePomodoroInfo(final List<PomodoroRealm> pomodoros,
                                 final String title,
                                 final String notes,
                                 final String category,
                                 final boolean solveConflict,
                                 final boolean overwriteIfNeed,
                                 final SaveInfoCallback callback) {
        TomatinaApp.REALM.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                for (PomodoroRealm pomodoro : pomodoros) {
                    RealmUtils.savePomodoroInfo(realm, pomodoro.getId(), title, notes, category, solveConflict, overwriteIfNeed);
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
