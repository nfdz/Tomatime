package io.github.nfdz.tomatina.home;

import android.arch.lifecycle.LiveData;

import io.github.nfdz.tomatina.common.model.PomodoroInfo;
import io.github.nfdz.tomatina.common.model.PomodoroState;

public interface HomeContract {

    interface View {
        void bindViewToLiveData(LiveData<PomodoroState> data);
        void showInfoDialog(PomodoroInfo info);
        void navigateToSettings();
    }

    interface Presenter {
        void create();
        void destroy();
    }

    interface Interactor {
        void initialize();
        void destroy();
        LiveData<PomodoroState> loadDataAsync();

    }

}
