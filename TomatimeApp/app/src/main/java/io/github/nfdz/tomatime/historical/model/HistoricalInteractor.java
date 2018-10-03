package io.github.nfdz.tomatime.historical.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.nfdz.tomatime.R;
import io.github.nfdz.tomatime.TomatimeApp;
import io.github.nfdz.tomatime.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatime.common.model.PomodoroRealm;
import io.github.nfdz.tomatime.common.utils.RealmUtils;
import io.github.nfdz.tomatime.historical.HistoricalContract;
import io.github.nfdz.tomatime.service.PomodoroService;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

public class HistoricalInteractor implements HistoricalContract.Interactor, RealmChangeListener<RealmResults<PomodoroRealm>> {

    private DataListener listener;
    private RealmResults<PomodoroRealm> observedData;

    @Override
    public void initialize(DataListener listener) {
        this.listener = listener;
        if (observedData == null) {
            observedData = TomatimeApp.REALM.where(PomodoroRealm.class).findAllAsync();
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
        noInfo.setTitle(TomatimeApp.INSTANCE.getString(R.string.historical_no_info_title));
        final Set<String> categories = new HashSet<>();
        final List<PomodoroHistoricalEntry> data = new ArrayList<>();
        TomatimeApp.REALM.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<PomodoroRealm> pomodoros = realm.where(PomodoroRealm.class)
                        .sort(PomodoroRealm.START_TIME_FIELD, Sort.DESCENDING).findAll();
                Map<PomodoroInfoRealm,List<PomodoroRealm>> map = new HashMap<>();
                for (PomodoroRealm pomodoro : pomodoros) {
                    PomodoroInfoRealm info = pomodoro.getPomodoroInfo();
                    if (info == null) {
                        info = noInfo;
                    } else {
                        categories.add(info.getCategory());
                    }
                    List<PomodoroRealm> pomodorosOfInfo = map.get(info);
                    if (pomodorosOfInfo == null) {
                        pomodorosOfInfo = new ArrayList<>();
                        map.put(info, pomodorosOfInfo);
                    }
                    pomodorosOfInfo.add(pomodoro);
                }
                for (Map.Entry<PomodoroInfoRealm,List<PomodoroRealm>> entry : map.entrySet()) {
                    PomodoroInfoRealm info = entry.getKey();
                    List<PomodoroRealm> content = entry.getValue();
                    data.add(new PomodoroHistoricalEntry(info.getKey(),
                            info.getTitle(),
                            info.getNotes(),
                            info.getCategory(),
                            content.size(),
                            computePomodorosDurationInMin(content),
                            content.get(0).getStartTimeMillis()));
                }
                Collections.sort(data);
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

    public int computePomodorosDurationInMin(List<PomodoroRealm> pomodoros) {
        int durationMin = 0;
        for (PomodoroRealm pomodoro : pomodoros) {
            durationMin += Math.max(0, TimeUnit.MILLISECONDS.toMinutes(pomodoro.getStartTimeMillis() - pomodoro.getId()));
        }
        return Math.max(1, durationMin);
    }

    @Override
    public void startPomodoro(PomodoroHistoricalEntry entry) {
        PomodoroService.startPomodoro(TomatimeApp.INSTANCE, entry.infoKey);
    }

    @Override
    public void deletePomodoros(final PomodoroHistoricalEntry entry, final DeleteCallback callback) {
        final AtomicBoolean somethingOngoing = new AtomicBoolean(false);
        TomatimeApp.REALM.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<PomodoroRealm> results;
                if (TextUtils.isEmpty(entry.infoKey)) {
                    results = realm.where(PomodoroRealm.class)
                            .isNull(PomodoroRealm.INFO_FIELD)
                            .findAll();
                } else {
                    results = realm.where(PomodoroRealm.class)
                            .equalTo(PomodoroRealm.INFO_FIELD + "." + PomodoroInfoRealm.KEY_FIELD, entry.infoKey)
                            .findAll();
                }
                if (results != null && !results.isEmpty()) {
                    for (PomodoroRealm pomodoro : results) {
                        if (!pomodoro.isOngoing()) {
                            pomodoro.deleteFromRealm();
                        } else {
                            somethingOngoing.set(true);
                        }
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess(somethingOngoing.get());
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
    public void savePomodoroInfo(final PomodoroHistoricalEntry entry,
                                 final String title,
                                 final String notes,
                                 final String category,
                                 final boolean solveConflict,
                                 final boolean overwriteIfNeed,
                                 final SaveInfoCallback callback) {
        TomatimeApp.REALM.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<PomodoroRealm> pomodoros;
                if (TextUtils.isEmpty(entry.infoKey)) {
                    pomodoros = realm.where(PomodoroRealm.class)
                            .isNull(PomodoroRealm.INFO_FIELD)
                            .findAll();
                } else {
                    pomodoros = realm.where(PomodoroRealm.class)
                            .equalTo(PomodoroRealm.INFO_FIELD + "." + PomodoroInfoRealm.KEY_FIELD, entry.infoKey)
                            .findAll();
                }
                boolean savedFirstOne = false;
                for (PomodoroRealm pomodoro : pomodoros) {
                    RealmUtils.savePomodoroInfo(realm, pomodoro.getId(), title, notes, category, solveConflict || savedFirstOne, overwriteIfNeed);
                    savedFirstOne = true;
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
