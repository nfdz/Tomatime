package io.github.nfdz.tomatina.common.utils;

import android.arch.lifecycle.LiveData;

import io.github.nfdz.tomatina.BuildConfig;
import io.github.nfdz.tomatina.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatina.common.model.PomodoroRealm;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class RealmUtils {

    private static final String DB_NAME = "tomatina.realm";
    private static final long SCHEMA_VERSION_NAME = 1;

    public static RealmConfiguration getConfiguration() {
        RealmConfiguration.Builder bld = new RealmConfiguration.Builder()
                .name(DB_NAME)
                .schemaVersion(SCHEMA_VERSION_NAME);
        if (!BuildConfig.DEBUG) {
            bld.deleteRealmIfMigrationNeeded();
        }
        return bld.build();
    }

    public static <T extends RealmModel> LiveData<RealmResults<T>> asLiveData(RealmResults<T> results) {
        return new RealmLiveData<>(results);
    }

    public static class RealmLiveData<T extends RealmModel> extends LiveData<RealmResults<T>> {

        private RealmResults<T> results;

        private final RealmChangeListener<RealmResults<T>> listener =
                new RealmChangeListener<RealmResults<T>>() {
                    @Override
                    public void onChange(RealmResults<T> results) {
                        setValue(results);
                    }
                };

        public RealmLiveData(RealmResults<T> realmResults) {
            results = realmResults;
        }

        @Override
        protected void onActive() {
            results.addChangeListener(listener);
        }

        @Override
        protected void onInactive() {
            results.removeChangeListener(listener);
        }

    }

    public static void savePomodoroInfo(Realm realm,
                                        final long id,
                                        final String title,
                                        final String notes,
                                        final String category,
                                        final boolean solveConflict,
                                        final boolean overwriteIfNeed) {
        String key = PomodoroInfoRealm.buildKeyFromTitle(title);
        PomodoroRealm pomodoro = realm.where(PomodoroRealm.class).equalTo(PomodoroRealm.ID_FIELD, id).findFirst();
        PomodoroInfoRealm currentInfo = pomodoro.getPomodoroInfo();
        if (currentInfo != null) {
            replacePomodoroInfoIfNeeded(realm, pomodoro, currentInfo, key, title, notes, category, solveConflict, overwriteIfNeed);
        } else {
            replacePomodoroInfo(realm, pomodoro, null, key, title, notes, category, solveConflict, overwriteIfNeed);
        }
    }

    private static void replacePomodoroInfoIfNeeded(Realm realm,
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

    private static void replacePomodoroInfo(Realm realm,
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

    private static void setPomodoroInfo(PomodoroInfoRealm info,
                                        String title,
                                        String notes,
                                        String category) {
        info.setTitle(title);
        info.setCategory(category);
        info.setNotes(notes);
    }

    private static void deletePomodoroInfoIfUseless(Realm realm, PomodoroInfoRealm info) {
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
