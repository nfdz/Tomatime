package io.github.nfdz.tomatina.historical.presenter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import io.github.nfdz.tomatina.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatina.common.model.PomodoroRealm;
import io.github.nfdz.tomatina.historical.HistoricalContract;
import io.github.nfdz.tomatina.historical.model.HistoricalInteractor;

public class HistoricalPresenter implements HistoricalContract.Presenter, HistoricalContract.Interactor.DataListener {

    private final Set<String> selectedCategories;

    private SortedMap<PomodoroInfoRealm, List<PomodoroRealm>> data;

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
    public void savePomodoroInfo(List<PomodoroRealm> pomodoros, String title, String notes, String category) {
        if (view != null && interactor != null) {
            interactor.savePomodoroInfo(pomodoros, title, notes, category, false, false, new HistoricalContract.Interactor.SaveInfoCallback() {
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
    public void overwritePomodoroInfo(List<PomodoroRealm> pomodoros, String title, String notes, String category) {
        if (view != null && interactor != null) {
            interactor.savePomodoroInfo(pomodoros, title, notes, category, true, true, new HistoricalContract.Interactor.SaveInfoCallback() {
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
    public void useExistingPomodoroInfo(List<PomodoroRealm> pomodoros, String title, String notes, String category) {
        if (view != null && interactor != null) {
            interactor.savePomodoroInfo(pomodoros, title, notes, category, true, false, new HistoricalContract.Interactor.SaveInfoCallback() {
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
    public void onNotifyData(Set<String> categories, SortedMap<PomodoroInfoRealm, List<PomodoroRealm>> data) {
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
            SortedMap<PomodoroInfoRealm,List<PomodoroRealm>> filteredData = new TreeMap<>();
            for (Map.Entry<PomodoroInfoRealm,List<PomodoroRealm>> entry : this.data.entrySet()) {
                if (selectedCategories.contains(entry.getKey().getCategory())) {
                    filteredData.put(entry.getKey(), entry.getValue());
                }
            }
            view.showData(filteredData);
        }
    }

}
