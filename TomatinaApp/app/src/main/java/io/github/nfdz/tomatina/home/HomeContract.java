package io.github.nfdz.tomatina.home;

public interface HomeContract {

    interface View {
        void showHomeTab();
        void showUserTab();
        void showHistoricalTab();
        void navigateToSettings();
    }

    interface Presenter {
        void initialize();
        void resume();
    }

}
