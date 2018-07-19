package io.github.nfdz.tomatina.home;

import android.arch.lifecycle.LiveData;

import io.github.nfdz.tomatina.common.model.PomodoroRealm;
import io.realm.RealmResults;

public interface HomeContract {

    interface View {
        void bindViewToLiveData(LiveData<RealmResults<PomodoroRealm>> data);
        void showSaveInfoError();
    }

    interface Presenter {
        void create();
        void destroy();
        void onStartPomodoroClick();
        void onStopPomodoroClick();
        void onSkipStageClick();
        void savePomodoroInfo(long id, String title, String notes, String category);
    }

    interface Interactor {
        void initialize();
        void destroy();
        LiveData<RealmResults<PomodoroRealm>> loadDataAsync();
        void startPomodoro();
        void stopPomodoro();
        void skipStage();

        interface SaveInfoCallback {
            void onSuccess();
            void onConflict();
            void onError();
        }
        void savePomodoroInfo(long id, String title, String notes, String category, SaveInfoCallback callback);
    }

}
