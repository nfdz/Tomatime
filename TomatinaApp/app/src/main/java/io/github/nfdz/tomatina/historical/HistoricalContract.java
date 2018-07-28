package io.github.nfdz.tomatina.historical;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import io.github.nfdz.tomatina.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatina.common.model.PomodoroRealm;

public interface HistoricalContract {

    interface View {
        void showData(SortedMap<PomodoroInfoRealm,List<PomodoroRealm>> data);
        void showCategories(Set<String> categories);
        void setSelectedCategories(Set<String> selectedCategories);
        void showSaveInfoError();
        void showSaveInfoConflict(List<PomodoroRealm> pomodoros, String title, String notes, String category);
    }

    interface Presenter {
        void create();
        void destroy();
        void onCategoryClick(String category);
        void savePomodoroInfo(List<PomodoroRealm> pomodoros, String title, String notes, String category);
        void overwritePomodoroInfo(List<PomodoroRealm> pomodoros, String title, String notes, String category);
        void useExistingPomodoroInfo(List<PomodoroRealm> pomodoros, String title, String notes, String category);
    }

    interface Interactor {
        void initialize(DataListener listener);
        void destroy();

        interface DataListener {
            void onNotifyData(Set<String> categories, SortedMap<PomodoroInfoRealm,List<PomodoroRealm>> data);
        }

        void startPomodoro(PomodoroInfoRealm info);

        interface DeleteCallback {
            void onSuccess();
            void onError();
        }
        void deletePomodoros(PomodoroInfoRealm info, DeleteCallback callback);

        interface SaveInfoCallback {
            void onSuccess();
            void onConflict();
            void onError();
        }
        void savePomodoroInfo(List<PomodoroRealm> pomodoros,
                              String title,
                              String notes,
                              String category,
                              boolean solveConflict,
                              boolean overwriteIfNeed,
                              SaveInfoCallback callback);
    }

}
