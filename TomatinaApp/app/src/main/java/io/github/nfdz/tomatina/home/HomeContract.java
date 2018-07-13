package io.github.nfdz.tomatina.home;

import android.arch.lifecycle.LiveData;

import io.github.nfdz.tomatina.common.model.PomodoroRealm;
import io.realm.RealmResults;

public interface HomeContract {

    interface View {
        void bindViewToLiveData(LiveData<RealmResults<PomodoroRealm>> data);
        void showInfoDialog();
    }

    interface Presenter {
        void create();
        void destroy();
        void savePomodoroInfo(long id, String title, String notes);
        void onStartPomodoroClick();
        void onStopPomodoroClick();
    }

    interface Interactor {
        void initialize();
        void destroy();
        LiveData<PomodoroRealm> loadDataAsync();
        void startPomodoro();
        void stopPomodoro();
    }

}
