package io.github.nfdz.tomatina.historical;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import io.github.nfdz.tomatina.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatina.common.model.PomodoroRealm;

public interface HistoricalContract {

    interface View {
        void showData(Map<PomodoroInfoRealm,List<PomodoroRealm>> data);
        void showCategories(List<String> categories);
        void setSelectedCategories(List<String> selectedCategories);
    }

    interface Presenter {
        void create();
        void destroy();
        void onCategoryClick(String category);
        void savePomodoroInfo(long id, String title, String notes, String category);
        void overwritePomodoroInfo(long id, String title, String notes, String category);
        void useExistingPomodoroInfo(long id, String title, String notes, String category);
    }

    interface Interactor {
        void initialize();
        void destroy();

        interface LoadDataCallback {
            void onSuccess(Set<String> categories, SortedMap<PomodoroInfoRealm,List<PomodoroRealm>> data);
            void onError();
        }
        void loadDataAsync(LoadDataCallback callback);

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
