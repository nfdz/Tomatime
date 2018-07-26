package io.github.nfdz.tomatina.historical.presenter;

import io.github.nfdz.tomatina.historical.HistoricalContract;

public class HistoricalPresenter implements HistoricalContract.Presenter {

    private HistoricalContract.View view;
    private HistoricalContract.Interactor interactor;

    public HistoricalPresenter(HistoricalContract.View view) {
        this.view = view;
    }

    @Override
    public void create() {

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
        if (view != null && interactor != null) {

        }
    }

    @Override
    public void savePomodoroInfo(long id, String title, String notes, String category) {
        if (view != null && interactor != null) {

        }
    }

    @Override
    public void overwritePomodoroInfo(long id, String title, String notes, String category) {
        if (view != null && interactor != null) {

        }
    }

    @Override
    public void useExistingPomodoroInfo(long id, String title, String notes, String category) {
        if (view != null && interactor != null) {

        }
    }

}
