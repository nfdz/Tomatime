package io.github.nfdz.tomatime.historical;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import io.github.nfdz.tomatime.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatime.common.model.PomodoroRealm;
import io.github.nfdz.tomatime.historical.model.PomodoroHistoricalEntry;

public interface HistoricalContract {

    interface View {
        void showData(List<PomodoroHistoricalEntry> data);
        void showCategories(Set<String> categories);
        void setSelectedCategories(Set<String> selectedCategories);
        void showPomodoroInfoDialog(PomodoroHistoricalEntry entry);
        void showSaveInfoError();
        void showDeleteInfoError();
        void showSaveInfoConflict(PomodoroHistoricalEntry entry, String title, String notes, String category);
        void navigateToPomodoro();
    }

    interface Presenter {
        void create();
        void destroy();
        void onCategoryClick(String category);
        void onPomodoroClick(PomodoroHistoricalEntry entry);
        void onStartPomodoroClick(PomodoroHistoricalEntry entry);
        void onDeletePomodoroClick(PomodoroHistoricalEntry entry);
        void savePomodoroInfo(PomodoroHistoricalEntry entry, String title, String notes, String category);
        void overwritePomodoroInfo(PomodoroHistoricalEntry entry, String title, String notes, String category);
        void useExistingPomodoroInfo(PomodoroHistoricalEntry entry, String title, String notes, String category);
    }

    interface Interactor {
        void initialize(DataListener listener);
        void destroy();

        interface DataListener {
            void onNotifyData(Set<String> categories, List<PomodoroHistoricalEntry> data);
        }

        void startPomodoro(PomodoroHistoricalEntry entry);

        interface DeleteCallback {
            void onSuccess(boolean somethingOngoing);
            void onError();
        }
        void deletePomodoros(PomodoroHistoricalEntry entry, DeleteCallback callback);

        interface SaveInfoCallback {
            void onSuccess();
            void onConflict();
            void onError();
        }
        void savePomodoroInfo(PomodoroHistoricalEntry entry,
                              String title,
                              String notes,
                              String category,
                              boolean solveConflict,
                              boolean overwriteIfNeed,
                              SaveInfoCallback callback);
    }

}
