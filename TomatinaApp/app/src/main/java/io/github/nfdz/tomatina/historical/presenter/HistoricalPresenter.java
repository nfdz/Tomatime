package io.github.nfdz.tomatina.historical.presenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.nfdz.tomatina.historical.HistoricalContract;
import io.github.nfdz.tomatina.historical.model.HistoricalInteractor;
import io.github.nfdz.tomatina.historical.model.PomodoroHistoricalEntry;

public class HistoricalPresenter implements HistoricalContract.Presenter, HistoricalContract.Interactor.DataListener {

    private final Set<String> selectedCategories;

    private List<PomodoroHistoricalEntry> data;

    private HistoricalContract.View view;
    private HistoricalContract.Interactor interactor;

    public HistoricalPresenter(HistoricalContract.View view) {
        this.view = view;
        this.interactor = new HistoricalInteractor();
        selectedCategories = new HashSet<>();
    }

    @Override
    public void create() {
        if (view != null && interactor != null) {
            interactor.initialize(this);
        }
    }

    @Override
    public void destroy() {
        if (interactor != null) {
            interactor.destroy();
            interactor = null;
        }
        view = null;
    }

    @Override
    public void onCategoryClick(String category) {
        if (view != null && interactor != null && data != null) {
            if (selectedCategories.contains(category)) {
                selectedCategories.remove(category);
            } else {
                selectedCategories.add(category);
            }
            view.setSelectedCategories(selectedCategories);
            updateContent();
        }
    }

    @Override
    public void onPomodoroClick(PomodoroHistoricalEntry entry) {
        if (view != null && interactor != null) {
            view.showPomodoroInfoDialog(entry);
        }
    }

    @Override
    public void onStartPomodoroClick(PomodoroHistoricalEntry entry) {
        if (view != null && interactor != null) {
            interactor.startPomodoro(entry);
            view.navigateToPomodoro();
        }
    }

    @Override
    public void onDeletePomodoroClick(PomodoroHistoricalEntry entry) {
        if (view != null && interactor != null) {
            interactor.deletePomodoros(entry, new HistoricalContract.Interactor.DeleteCallback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError() {

                }
            });
        }
    }

    @Override
    public void savePomodoroInfo(PomodoroHistoricalEntry entry, String title, String notes, String category) {
        if (view != null && interactor != null) {
            interactor.savePomodoroInfo(entry, title, notes, category, false, false, new HistoricalContract.Interactor.SaveInfoCallback() {
                @Override
                public void onSuccess() {
                    if (view != null && interactor != null) {

                    }
                }
                @Override
                public void onConflict() {
                    if (view != null && interactor != null) {

                    }
                }
                @Override
                public void onError() {
                    if (view != null && interactor != null) {

                    }
                }
            });
        }
    }

    @Override
    public void overwritePomodoroInfo(PomodoroHistoricalEntry entry, String title, String notes, String category) {
        if (view != null && interactor != null) {
            interactor.savePomodoroInfo(entry, title, notes, category, true, true, new HistoricalContract.Interactor.SaveInfoCallback() {
                @Override
                public void onSuccess() {
                    if (view != null && interactor != null) {

                    }
                }
                @Override
                public void onConflict() { /* never called */ }
                @Override
                public void onError() {
                    if (view != null && interactor != null) {

                    }
                }
            });
        }
    }

    @Override
    public void useExistingPomodoroInfo(PomodoroHistoricalEntry entry, String title, String notes, String category) {
        if (view != null && interactor != null) {
            interactor.savePomodoroInfo(entry, title, notes, category, true, false, new HistoricalContract.Interactor.SaveInfoCallback() {
                @Override
                public void onSuccess() {
                    if (view != null && interactor != null) {

                    }
                }
                @Override
                public void onConflict() { /* never called */ }
                @Override
                public void onError() {
                    if (view != null && interactor != null) {

                    }
                }
            });
        }
    }

    @Override
    public void onNotifyData(Set<String> categories, List<PomodoroHistoricalEntry> data) {
        if (view != null && interactor != null) {
            this.data = data;
            view.showCategories(categories);
            updateContent();
        }
    }

    private void updateContent() {
        if (selectedCategories.isEmpty()) {
            view.showData(this.data);
        } else {
            List<PomodoroHistoricalEntry> filteredData = new ArrayList<>();
            for (PomodoroHistoricalEntry entry : this.data) {
                if (selectedCategories.contains(entry.category)) {
                    filteredData.add(entry);
                }
            }
            view.showData(filteredData);
        }
    }

}
