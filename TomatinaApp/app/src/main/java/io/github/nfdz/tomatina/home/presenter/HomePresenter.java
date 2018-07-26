package io.github.nfdz.tomatina.home.presenter;

import io.github.nfdz.tomatina.home.HomeContract;
import io.github.nfdz.tomatina.home.model.HomeInteractor;

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View view;
    private HomeContract.Interactor interactor;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
        interactor = new HomeInteractor();
    }

    @Override
    public void create() {
        if (view != null && interactor != null) {
            interactor.initialize();
            view.bindViewToLiveData(interactor.loadDataAsync());
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
    public void savePomodoroInfo(final long id, final String title, final String notes, final String category) {
        if (view != null && interactor != null) {
            interactor.savePomodoroInfo(id, title, notes, category, false, false, new HomeContract.Interactor.SaveInfoCallback() {
                @Override
                public void onSuccess() {
                    // nothing
                }
                @Override
                public void onConflict() {
                    if (view != null) {
                        view.showSaveInfoConflict(id, title, notes, category);
                    }
                }
                @Override
                public void onError() {
                    if (view != null) {
                        view.showSaveInfoError();
                    }
                }
            });
        }
    }

    @Override
    public void overwritePomodoroInfo(long id, String title, String notes, String category) {
        if (view != null && interactor != null) {
            interactor.savePomodoroInfo(id, title, notes, category, true, true, new HomeContract.Interactor.SaveInfoCallback() {
                @Override
                public void onSuccess() {
                    // nothing
                }
                @Override
                public void onConflict() {
                    // do nothing
                }
                @Override
                public void onError() {
                    if (view != null) {
                        view.showSaveInfoError();
                    }
                }
            });
        }
    }

    @Override
    public void useExistingPomodoroInfo(long id, String title, String notes, String category) {
        if (view != null && interactor != null) {
            interactor.savePomodoroInfo(id, title, notes, category, true, false, new HomeContract.Interactor.SaveInfoCallback() {
                @Override
                public void onSuccess() {
                    // nothing
                }
                @Override
                public void onConflict() {
                    // do nothing
                }
                @Override
                public void onError() {
                    if (view != null) {
                        view.showSaveInfoError();
                    }
                }
            });
        }
    }

    @Override
    public void onStartPomodoroClick() {
        if (view != null && interactor != null) {
            interactor.startPomodoro();
        }
    }

    @Override
    public void onStopPomodoroClick() {
        if (view != null && interactor != null) {
            interactor.stopPomodoro();
        }
    }

    @Override
    public void onSkipStageClick() {
        if (view != null && interactor != null) {
            interactor.skipStage();
        }
    }

}
