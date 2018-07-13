package io.github.nfdz.tomatina.home.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.common.model.PomodoroRealm;
import io.github.nfdz.tomatina.common.model.PomodoroState;
import io.github.nfdz.tomatina.common.utils.SnackbarUtils;
import io.github.nfdz.tomatina.home.HomeContract;
import io.github.nfdz.tomatina.home.presenter.HomePresenter;
import io.realm.Realm;
import io.realm.RealmResults;

public class HomeFragment extends Fragment implements HomeContract.View, Observer<RealmResults<PomodoroRealm>> {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private static float ALPHA_DISABLED_BUTTON = 0.5f;

    @BindView(R.id.home_tv_state) TextView home_tv_state;
    @BindView(R.id.home_tv_progress_current) TextView home_tv_progress_current;
    @BindView(R.id.home_tv_progress_total) TextView home_tv_progress_total;
    @BindView(R.id.home_iv_global_working) ImageView home_iv_global_working;
    @BindView(R.id.home_iv_global_short_break) ImageView home_iv_global_short_break;
    @BindView(R.id.home_iv_global_long_break) ImageView home_iv_global_long_break;
    @BindView(R.id.home_ll_global_working_container) LinearLayout home_ll_global_working_container;
    @BindView(R.id.home_ll_global_short_break_container) LinearLayout home_ll_global_short_break_container;
    @BindView(R.id.home_ll_global_long_break_container) LinearLayout home_ll_global_long_break_container;
    @BindView(R.id.home_btn_toggle_pomodoro) FloatingActionButton home_btn_toggle_pomodoro;
    @BindView(R.id.home_btn_info_pomodoro) FloatingActionButton home_btn_info_pomodoro;
    @BindView(R.id.home_btn_settings_pomodoro) FloatingActionButton home_btn_settings_pomodoro;
    @BindView(R.id.home_btn_skip_stage) FloatingActionButton home_btn_skip_stage;
    @BindView(R.id.home_guideline_anim) Guideline home_guideline_anim;
    @BindView(R.id.home_iv_anim) ImageView home_iv_anim;

    private HomeContract.Presenter presenter;
    private LiveData<RealmResults<PomodoroRealm>> bindedData = null;
    private PomodoroRealm shownPomodoroRealm;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new HomePresenter(this);
        presenter.create();
    }

    @Override
    public void onDestroyView() {
        presenter.destroy();
        super.onDestroyView();
    }

    @OnClick(R.id.home_btn_toggle_pomodoro)
    public void onTogglePomodoroClick() {
        if (shownPomodoroRealm == null) {
            presenter.onStartPomodoroClick();
        } else {
            presenter.onStopPomodoroClick();
        }
    }

    @OnClick(R.id.home_btn_skip_stage)
    public void onSkipStageClick() {
        presenter.onSkipStageClick();
    }

    @OnClick(R.id.home_btn_info_pomodoro)
    public void onInfoPomodoroClick() {
        // TODO open dialog
    }

    @OnClick(R.id.home_btn_settings_pomodoro)
    public void onSettingsPomodoroClick() {
        // TODO navigate to settings
    }

    @Override
    public void bindViewToLiveData(LiveData<RealmResults<PomodoroRealm>> data) {
        if (bindedData != null) {
            bindedData.removeObservers(this);
        }
        bindedData = data;
        if (bindedData != null) {
            bindedData.observe(this, this);
        } else {
            onChanged(null);
        }
        showSaveInfoError();
    }

    @Override
    public void showSaveInfoError() {
        SnackbarUtils.show(getView(), R.string.home_save_info_error, Snackbar.LENGTH_LONG);
    }

    @Override
    public void onChanged(@Nullable RealmResults<PomodoroRealm> pomodorosRealms) {
        updateView(copyData(pomodorosRealms));
    }

    private PomodoroRealm copyData(@Nullable RealmResults<PomodoroRealm> pomodorosRealms) {
        try {
            PomodoroRealm result = pomodorosRealms.first();
            Realm realm = result.getRealm();
            return realm.copyFromRealm(result);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void updateView(PomodoroRealm pomodoroRealm) {
        this.shownPomodoroRealm = pomodoroRealm;
        if (pomodoroRealm == null) {
            showEmptyMode();
        } else {
            switch (pomodoroRealm.getState()) {
                case PomodoroState.WORKING:
                    showWorkingMode();
                    break;
                case PomodoroState.SHORT_BREAK:
                    showShortBreakMode();
                    break;
                case PomodoroState.LONG_BREAK:
                    showLongBreakMode();
                    break;
                case PomodoroState.FINISHED: // TODO handle finish event
                case PomodoroState.NONE:
                default:
                    this.shownPomodoroRealm = null;
                    showEmptyMode();
            }
        }
    }

    private void showEmptyMode() {
        // top section
        home_tv_state.setText(R.string.state_text_none);
        home_tv_progress_current.setText(getTimerTextFor(0));
        home_tv_progress_total.setText("/ " + getTimerTextFor(0));
        setStageProgressBar(0);
        // TODO icono inicial home_iv_anim.setImageResource();

        // global summary section
        home_iv_global_working.setSelected(false);
        home_iv_global_short_break.setSelected(false);
        home_iv_global_long_break.setSelected(false);
        setupIndicator(home_ll_global_working_container, 0, 0);
        setupIndicator(home_ll_global_short_break_container, 0, 0);
        setupIndicator(home_ll_global_long_break_container, 0, 0);

        // bottom buttons
        home_btn_toggle_pomodoro.setEnabled(true);
        home_btn_toggle_pomodoro.setAlpha(1f);
        home_btn_toggle_pomodoro.setImageResource(R.drawable.ic_play_dark);
        home_btn_skip_stage.setEnabled(false);
        home_btn_skip_stage.setAlpha(ALPHA_DISABLED_BUTTON);
        home_btn_info_pomodoro.setEnabled(false);
        home_btn_info_pomodoro.setAlpha(ALPHA_DISABLED_BUTTON);
        home_btn_settings_pomodoro.setEnabled(true);
        home_btn_settings_pomodoro.setAlpha(1f);
    }

    private void showWorkingMode() {
        // top section
        home_tv_state.setText(R.string.state_text_working);
        long ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
        home_tv_progress_total.setText("/ " + getTimerTextFor(shownPomodoroRealm.getPomodoroTimeInMillis()));
        int progress = (int) (((ellapsedTime + 0.0f)/shownPomodoroRealm.getPomodoroTimeInMillis()) * 100);
        progress = Math.min(progress, 100);
        setStageProgressBar(progress);
        // TODO icono home_iv_anim.setImageResource();

        // global summary section
        home_iv_global_working.setSelected(true);
        home_iv_global_short_break.setSelected(false);
        home_iv_global_long_break.setSelected(false);
        setupIndicator(home_ll_global_working_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_short_break_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_long_break_container, 0, 1);

        // bottom buttons
        home_btn_toggle_pomodoro.setEnabled(true);
        home_btn_toggle_pomodoro.setAlpha(1f);
        home_btn_toggle_pomodoro.setImageResource(R.drawable.ic_stop_dark);
        home_btn_skip_stage.setEnabled(true);
        home_btn_skip_stage.setAlpha(1f);
        home_btn_info_pomodoro.setEnabled(true);
        home_btn_info_pomodoro.setAlpha(1f);
        home_btn_settings_pomodoro.setEnabled(false);
        home_btn_settings_pomodoro.setAlpha(ALPHA_DISABLED_BUTTON);
    }

    private void showShortBreakMode() {
        // top section
        home_tv_state.setText(R.string.state_text_short_break);
        long ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
        home_tv_progress_total.setText("/ " + getTimerTextFor(shownPomodoroRealm.getShortBreakTimeInMillis()));
        int progress = (int) (((ellapsedTime + 0.0f)/shownPomodoroRealm.getShortBreakTimeInMillis()) * 100);
        progress = Math.min(progress, 100);
        setStageProgressBar(progress);
        // TODO icono home_iv_anim.setImageResource();

        // global summary section
        home_iv_global_working.setSelected(false);
        home_iv_global_short_break.setSelected(true);
        home_iv_global_long_break.setSelected(false);
        setupIndicator(home_ll_global_working_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_short_break_container, shownPomodoroRealm.getCounter()-1, shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_long_break_container, 0, 1);

        // bottom buttons
        home_btn_toggle_pomodoro.setEnabled(true);
        home_btn_toggle_pomodoro.setAlpha(1f);
        home_btn_toggle_pomodoro.setImageResource(R.drawable.ic_stop_dark);
        home_btn_skip_stage.setEnabled(true);
        home_btn_skip_stage.setAlpha(1f);
        home_btn_info_pomodoro.setEnabled(true);
        home_btn_info_pomodoro.setAlpha(1f);
        home_btn_settings_pomodoro.setEnabled(false);
        home_btn_settings_pomodoro.setAlpha(ALPHA_DISABLED_BUTTON);
    }

    private void showLongBreakMode() {
        // top section
        home_tv_state.setText(R.string.state_text_long_break);
        long ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
        home_tv_progress_total.setText("/ " + getTimerTextFor(shownPomodoroRealm.getLongBreakTimeInMillis()));
        int progress = (int) (((ellapsedTime + 0.0f)/shownPomodoroRealm.getLongBreakTimeInMillis()) * 100);
        progress = Math.min(progress, 100);
        setStageProgressBar(progress);
        // TODO icono home_iv_anim.setImageResource();

        // global summary section
        home_iv_global_working.setSelected(false);
        home_iv_global_short_break.setSelected(false);
        home_iv_global_long_break.setSelected(true);
        setupIndicator(home_ll_global_working_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_short_break_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_long_break_container, 0, 1);

        // bottom buttons
        home_btn_toggle_pomodoro.setEnabled(true);
        home_btn_toggle_pomodoro.setAlpha(1f);
        home_btn_toggle_pomodoro.setImageResource(R.drawable.ic_stop_dark);
        home_btn_skip_stage.setEnabled(true);
        home_btn_skip_stage.setAlpha(1f);
        home_btn_info_pomodoro.setEnabled(true);
        home_btn_info_pomodoro.setAlpha(1f);
        home_btn_settings_pomodoro.setEnabled(false);
        home_btn_settings_pomodoro.setAlpha(ALPHA_DISABLED_BUTTON);
    }

    private void setupIndicator(LinearLayout indicatorContainer, int progress, int total) {
        indicatorContainer.removeAllViews();
    }

    private void setStageProgressBar(float ratio) {
        float adaptedRatio = ratio * 0.7f + 0.15f;
        home_guideline_anim.setGuidelinePercent(adaptedRatio);
    }

    private String getTimerTextFor(long time) {
        long minutesRaw = TimeUnit.MILLISECONDS.toMinutes(time);
        if (minutesRaw > 60) {
            return "+60:00";
        }
        String minutes = Long.toString(minutesRaw);
        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        String seconds = Long.toString(TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }
        return minutes + ":" + seconds;
    }

}
