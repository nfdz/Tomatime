package io.github.nfdz.tomatime.home;

import android.arch.lifecycle.LiveData;

import io.github.nfdz.tomatime.common.model.PomodoroRealm;
import io.realm.RealmResults;

public interface HomeContract {

    interface View {
        void showWelcomeDialog();
        void bindViewToLiveData(LiveData<RealmResults<PomodoroRealm>> data);
        void showSaveInfoError();
        void showSaveInfoConflict(long id, String title, String notes, String category);
    }

    interface Presenter {
        void create();
        void destroy();
        void onStartPomodoroClick();
        void onStopPomodoroClick();
        void onSkipStageClick();
        void onContinueClick();
        void savePomodoroInfo(long id, String title, String notes, String category);
        void overwritePomodoroInfo(long id, String title, String notes, String category);
        void useExistingPomodoroInfo(long id, String title, String notes, String category);
    }

    interface Interactor {
        void initialize();
        void destroy();
        boolean handleFirstTime();
        LiveData<RealmResults<PomodoroRealm>> loadDataAsync();
        void startPomodoro();
        void stopPomodoro();
        void skipStage();
        void skipFinishStage();

        interface SaveInfoCallback {
            void onSuccess();
            void onConflict();
            void onError();
        }
        void savePomodoroInfo(long id,
                              String title,
                              String notes,
                              String category,
                              boolean solveConflict,
                              boolean overwriteIfNeed,
                              SaveInfoCallback callback);
    }

}
