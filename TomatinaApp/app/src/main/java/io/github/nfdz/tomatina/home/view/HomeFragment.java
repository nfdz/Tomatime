package io.github.nfdz.tomatina.home.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
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
import io.github.nfdz.tomatina.service.PomodoroService;
import io.github.nfdz.tomatina.settings.view.SettingsActivity;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

public class HomeFragment extends Fragment implements HomeContract.View, Observer<RealmResults<PomodoroRealm>> {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private static long CLOCK_RATE_MILLIS = 1000;
    private static float ALPHA_DISABLED_BUTTON = 0.5f;
    private static int MAX_INDICATORS_TO_DRAW = 5;

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
    @BindView(R.id.home_layer_warn) View home_layer_warn;

    private HomeContract.Presenter presenter;
    private LiveData<RealmResults<PomodoroRealm>> bindedData = null;
    private PomodoroRealm shownPomodoroRealm;
    private Handler handler;
    private ClockTask clockTask;
    private WaitingContinueReceiver waitingContinueReceiver;

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
        waitingContinueReceiver = new WaitingContinueReceiver();
        handler = new Handler();
        presenter = new HomePresenter(this);
        presenter.create();
    }

    @Override
    public void onDestroyView() {
        presenter.destroy();
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (clockTask == null) {
            clockTask = new ClockTask();
            handler.postDelayed(clockTask, CLOCK_RATE_MILLIS);
        }
        if (bindedData != null) {
            bindedData.observe(this, this);
        } else {
            onChanged(null);
        }
        registeWaitingReceiver();
    }

    private void registeWaitingReceiver() {
        try {
            IntentFilter filter = new IntentFilter(PomodoroService.CONTINUE_POMODORO_ACTION);
            getActivity().registerReceiver(waitingContinueReceiver, filter);
        } catch (Exception e) {
            Timber.e(e, "Cannot register waiting continue receiver");
        }
    }

    @Override
    public void onStop() {
        unregisteWaitingReceiver();
        if (bindedData != null) {
            bindedData.removeObservers(this);
        }
        if (clockTask != null) {
            clockTask.cancelled = true;
            clockTask = null;
        }
        super.onStop();
    }

    private void unregisteWaitingReceiver() {
        try {
            getActivity().unregisterReceiver(waitingContinueReceiver);
        } catch (Exception e) {
            Timber.e(e, "Cannot unregister waiting continue receiver");
        }
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
        try {
            SettingsActivity.start(getActivity());
        } catch (Exception e) {
            Timber.e(e, "Cannot start settings activity");
        }
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
        hideWarningLayer();
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
        hideWarningLayer();
        // top section
        home_tv_state.setText(R.string.state_text_working);
        long ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
        home_tv_progress_total.setText("/ " + getTimerTextFor(shownPomodoroRealm.getPomodoroTimeInMillis()));
        int progress = (int) (((ellapsedTime + 0.0f)/shownPomodoroRealm.getPomodoroTimeInMillis()) * 100);
        progress = Math.min(progress, 100);
        setStageProgressBar(progress/100f);
        if (progress == 100) handleWaitingContinueEvent();
        // TODO icono home_iv_anim.setImageResource();

        // global summary section
        home_iv_global_working.setSelected(true);
        home_iv_global_short_break.setSelected(false);
        home_iv_global_long_break.setSelected(false);
        setupIndicator(home_ll_global_working_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_short_break_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak()-1);
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
        hideWarningLayer();
        // top section
        home_tv_state.setText(R.string.state_text_short_break);
        long ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
        home_tv_progress_total.setText("/ " + getTimerTextFor(shownPomodoroRealm.getShortBreakTimeInMillis()));
        int progress = (int) (((ellapsedTime + 0.0f)/shownPomodoroRealm.getShortBreakTimeInMillis()) * 100);
        progress = Math.min(progress, 100);
        setStageProgressBar(progress/100f);
        if (progress == 100) handleWaitingContinueEvent();
        // TODO icono home_iv_anim.setImageResource();

        // global summary section
        home_iv_global_working.setSelected(false);
        home_iv_global_short_break.setSelected(true);
        home_iv_global_long_break.setSelected(false);
        setupIndicator(home_ll_global_working_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_short_break_container, shownPomodoroRealm.getCounter()-1, shownPomodoroRealm.getPomodorosToLongBreak()-1);
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
        hideWarningLayer();
        // top section
        home_tv_state.setText(R.string.state_text_long_break);
        long ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
        home_tv_progress_total.setText("/ " + getTimerTextFor(shownPomodoroRealm.getLongBreakTimeInMillis()));
        int progress = (int) (((ellapsedTime + 0.0f)/shownPomodoroRealm.getLongBreakTimeInMillis()) * 100);
        progress = Math.min(progress, 100);
        setStageProgressBar(progress/100f);
        // TODO icono home_iv_anim.setImageResource();

        // global summary section
        home_iv_global_working.setSelected(false);
        home_iv_global_short_break.setSelected(false);
        home_iv_global_long_break.setSelected(true);
        setupIndicator(home_ll_global_working_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_short_break_container, shownPomodoroRealm.getCounter()-1, shownPomodoroRealm.getPomodorosToLongBreak()-1);
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
        if (shownPomodoroRealm != null && shownPomodoroRealm.getPomodorosToLongBreak() > MAX_INDICATORS_TO_DRAW) {
            setupTextIndicator(indicatorContainer, progress, total);
        } else {
            setupPrettyIndicator(indicatorContainer, progress, total);
        }
    }

    private void setupPrettyIndicator(LinearLayout indicatorContainer, int progress, int total) {
        indicatorContainer.removeAllViews();
        Context context = indicatorContainer.getContext();
        int size = getResources().getDimensionPixelSize(R.dimen.home_indicator_size);
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.home_indicator_margin_horizontal);
        for (int i = 0; i < total; i++) {
            boolean isCompleted = i < progress;
            ImageView indicatorIv = new ImageView(context);
            indicatorIv.setImageResource(isCompleted ? R.drawable.shape_circle_light_filled : R.drawable.shape_circle_light);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.setMargins(horizontalMargin, 0, horizontalMargin, 0);
            indicatorIv.setLayoutParams(layoutParams);
            indicatorContainer.addView(indicatorIv);
        }
    }

    private void setupTextIndicator(LinearLayout indicatorContainer, int progress, int total) {
        indicatorContainer.removeAllViews();
        Context context = indicatorContainer.getContext();
        TextView indicatorTv = new TextView(context);
        indicatorTv.setGravity(Gravity.CENTER);
        indicatorTv.setTypeface(indicatorTv.getTypeface(), Typeface.BOLD);
        String text = Integer.toString(progress) + "/" + Integer.toString(total);
        indicatorTv.setText(text);
        int size = getResources().getDimensionPixelSize(R.dimen.home_indicator_size);
        indicatorTv.setTextSize(COMPLEX_UNIT_PX, size);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1);
        indicatorTv.setLayoutParams(layoutParams);
        indicatorContainer.addView(indicatorTv);
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

    private class ClockTask implements Runnable {

        private boolean cancelled;

        @Override
        public void run() {
            if (!cancelled) {
                try {
                    if (shownPomodoroRealm != null) {
                        long ellapsedTime;
                        int progress;
                        long maxTime;
                        switch (shownPomodoroRealm.getState()) {
                            case PomodoroState.WORKING:
                                ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
                                progress = (int) (((ellapsedTime + 0.0f)/shownPomodoroRealm.getPomodoroTimeInMillis()) * 100);
                                maxTime = shownPomodoroRealm.getPomodoroTimeInMillis();
                                break;
                            case PomodoroState.SHORT_BREAK:
                                ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
                                progress = (int) (((ellapsedTime + 0.0f)/shownPomodoroRealm.getShortBreakTimeInMillis()) * 100);
                                maxTime = shownPomodoroRealm.getShortBreakTimeInMillis();
                                break;
                            case PomodoroState.LONG_BREAK:
                                ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
                                progress = (int) (((ellapsedTime + 0.0f)/shownPomodoroRealm.getLongBreakTimeInMillis()) * 100);
                                maxTime = shownPomodoroRealm.getLongBreakTimeInMillis();
                                break;
                            case PomodoroState.FINISHED:
                            case PomodoroState.NONE:
                            default:
                                ellapsedTime = 0;
                                progress = 0;
                                maxTime = 0;
                        }
                        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
                        home_tv_progress_total.setText("/ " + getTimerTextFor(maxTime));
                        progress = Math.min(progress, 100);
                        setStageProgressBar(progress/100f);
                    }
                } catch (Exception e) {
                    Timber.e(e, "There was an error processing tick");
                } finally {
                    handler.postDelayed(this, CLOCK_RATE_MILLIS);
                }
            }
        }
    }

    public class WaitingContinueReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Timber.d("Waiting continue event received");
            handleWaitingContinueEvent();
        }
    }

    private void handleWaitingContinueEvent() {
        if (shownPomodoroRealm != null) {
            switch (shownPomodoroRealm.getState()) {
                case PomodoroState.WORKING:
                    if (shownPomodoroRealm.getCounter() + 1 < shownPomodoroRealm.getPomodorosToLongBreak()) {
                        handleContinueToShortBreak();
                    } else {
                        handleContinueToLongBreak();
                    }
                    break;
                case PomodoroState.SHORT_BREAK:
                    handleContinueToWork();
                    break;
                case PomodoroState.LONG_BREAK:
                case PomodoroState.FINISHED:
                case PomodoroState.NONE:
                default:
            }
        }
    }

    private void handleContinueToWork() {
        // TODO
        showWarningLayer();
    }

    private void handleContinueToShortBreak() {
        // TODO
        showWarningLayer();
    }

    private void handleContinueToLongBreak() {
        // TODO
        showWarningLayer();
    }

    private void showWarningLayer() {
        home_layer_warn.setVisibility(View.VISIBLE);
    }

    private void hideWarningLayer() {
        home_layer_warn.setVisibility(View.GONE);
    }

}
