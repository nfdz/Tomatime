package io.github.nfdz.tomatime.home.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.tomatime.R;
import io.github.nfdz.tomatime.common.dialog.PomodoroInfoDialog;
import io.github.nfdz.tomatime.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatime.common.model.PomodoroRealm;
import io.github.nfdz.tomatime.common.model.PomodoroState;
import io.github.nfdz.tomatime.common.utils.SnackbarUtils;
import io.github.nfdz.tomatime.home.HomeContract;
import io.github.nfdz.tomatime.home.presenter.HomePresenter;
import io.github.nfdz.tomatime.service.PomodoroService;
import io.github.nfdz.tomatime.settings.view.SettingsActivity;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

public class HomeFragment extends Fragment implements HomeContract.View,
        PomodoroInfoDialog.UpdateInfoCallback,
        Observer<RealmResults<PomodoroRealm>> {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private static long CLOCK_RATE_MILLIS = 1000;
    private static float ALPHA_DISABLED_BUTTON = 0.5f;
    private static int MAX_INDICATORS_TO_DRAW = 4;

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
    @BindView(R.id.home_bg_progress_fill_indicator) View home_bg_progress_fill_indicator;
    @BindView(R.id.home_layer_warn) View home_layer_warn;
    @BindView(R.id.home_btn_continue) View home_btn_continue;

    private HomeContract.Presenter presenter;
    private LiveData<RealmResults<PomodoroRealm>> bindedData = null;
    private PomodoroRealm shownPomodoroRealm;
    private Handler handler;
    private ClockTask clockTask;
    private WaitingContinueReceiver waitingContinueReceiver;
    private int currentGif = -1;

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
        registerWaitingReceiver();
    }

    private void registerWaitingReceiver() {
        try {
            IntentFilter filter = new IntentFilter(PomodoroService.CONTINUE_POMODORO_ACTION);
            getActivity().registerReceiver(waitingContinueReceiver, filter);
        } catch (Exception e) {
            Timber.e(e, "Cannot register waiting continue receiver");
        }
    }

    @Override
    public void onStop() {
        unregisterWaitingReceiver();
        if (bindedData != null) {
            bindedData.removeObservers(this);
        }
        if (clockTask != null) {
            clockTask.cancelled = true;
            clockTask = null;
        }
        super.onStop();
    }

    private void unregisterWaitingReceiver() {
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
        if (shownPomodoroRealm != null) {
            PomodoroInfoRealm info = shownPomodoroRealm.getPomodoroInfo();
            PomodoroInfoDialog dialog = info != null ?
                    PomodoroInfoDialog.newInstance(info.getTitle(), info.getNotes(), info.getCategory())
                    : PomodoroInfoDialog.newInstance("", "", "");
            dialog.setCallback(this);
            dialog.show(getFragmentManager(), "pomodoro_info_dialog");
        }
    }

    @OnClick(R.id.home_btn_settings_pomodoro)
    public void onSettingsPomodoroClick() {
        try {
            SettingsActivity.start(getActivity());
        } catch (Exception e) {
            Timber.e(e, "Cannot start settings activity");
        }
    }

    @OnClick(R.id.home_btn_continue)
    public void onContinueClick() {
        presenter.onContinueClick();
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
    public void showSaveInfoConflict(final long id, final String title, final String notes, final String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppAlertDialog);
        builder.setTitle(R.string.info_conflict_dialog_title)
                .setMessage(R.string.info_conflict_dialog_content)
                .setPositiveButton(R.string.info_conflict_dialog_overwrite,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                presenter.overwritePomodoroInfo(id, title, notes, category);
                                dialog.dismiss();
                            }
                        }
                )
                .setNeutralButton(R.string.info_conflict_dialog_existing,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                presenter.useExistingPomodoroInfo(id, title, notes, category);
                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton(R.string.info_conflict_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                ).show();
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
        hideBreakLayer();
        hideContinue();
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
        home_tv_progress_current.setText(getTimerTextFor(0));
        home_tv_progress_total.setText("/ " + getTimerTextFor(0));
        setStageProgressBar(0);
        disableGif();
        home_bg_progress_fill_indicator.setVisibility(View.INVISIBLE);

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
        long ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
        int progress = (int) (((ellapsedTime + 0.0f) / shownPomodoroRealm.getPomodoroTimeInMillis()) * 100);
        progress = Math.min(progress, 100);
        if (ellapsedTime > shownPomodoroRealm.getPomodoroTimeInMillis()) {
            ellapsedTime = shownPomodoroRealm.getPomodoroTimeInMillis();
        }
        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
        home_tv_progress_total.setText("/ " + getTimerTextFor(shownPomodoroRealm.getPomodoroTimeInMillis()));
        setStageProgressBar(progress / 100f);
        if (progress == 100) handleWaitingContinueEvent();
        setWorkingGif();
        home_bg_progress_fill_indicator.setVisibility(View.VISIBLE);

        // global summary section
        home_iv_global_working.setSelected(true);
        home_iv_global_short_break.setSelected(false);
        home_iv_global_long_break.setSelected(false);
        setupIndicator(home_ll_global_working_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_short_break_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak() - 1);
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
        long ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
        int progress = (int) (((ellapsedTime + 0.0f) / shownPomodoroRealm.getShortBreakTimeInMillis()) * 100);
        progress = Math.min(progress, 100);
        if (ellapsedTime > shownPomodoroRealm.getShortBreakTimeInMillis()) {
            ellapsedTime = shownPomodoroRealm.getShortBreakTimeInMillis();
        }
        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
        home_tv_progress_total.setText("/ " + getTimerTextFor(shownPomodoroRealm.getShortBreakTimeInMillis()));
        setStageProgressBar(progress / 100f);
        if (progress == 100) handleWaitingContinueEvent();
        setShortBreakGif();
        home_bg_progress_fill_indicator.setVisibility(View.VISIBLE);

        // global summary section
        home_iv_global_working.setSelected(false);
        home_iv_global_short_break.setSelected(true);
        home_iv_global_long_break.setSelected(false);
        setupIndicator(home_ll_global_working_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_short_break_container, shownPomodoroRealm.getCounter() - 1, shownPomodoroRealm.getPomodorosToLongBreak() - 1);
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

        showBreakLayer();
    }

    private void showLongBreakMode() {
        // top section
        long ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
        int progress = (int) (((ellapsedTime + 0.0f) / shownPomodoroRealm.getLongBreakTimeInMillis()) * 100);
        progress = Math.min(progress, 100);
        if (ellapsedTime > shownPomodoroRealm.getLongBreakTimeInMillis()) {
            ellapsedTime = shownPomodoroRealm.getLongBreakTimeInMillis();
        }
        home_tv_progress_current.setText(getTimerTextFor(ellapsedTime));
        home_tv_progress_total.setText("/ " + getTimerTextFor(shownPomodoroRealm.getLongBreakTimeInMillis()));
        setStageProgressBar(progress / 100f);
        setLongBreakGif();
        home_bg_progress_fill_indicator.setVisibility(View.VISIBLE);

        // global summary section
        home_iv_global_working.setSelected(false);
        home_iv_global_short_break.setSelected(false);
        home_iv_global_long_break.setSelected(true);
        setupIndicator(home_ll_global_working_container, shownPomodoroRealm.getCounter(), shownPomodoroRealm.getPomodorosToLongBreak());
        setupIndicator(home_ll_global_short_break_container, shownPomodoroRealm.getCounter() - 1, shownPomodoroRealm.getPomodorosToLongBreak() - 1);
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

        showBreakLayer();
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
            indicatorIv.setImageResource(isCompleted ? R.drawable.shape_circle_dark_filled : R.drawable.shape_circle_dark);
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        indicatorTv.setLayoutParams(layoutParams);
        indicatorContainer.addView(indicatorTv);
    }

    private void setStageProgressBar(float ratio) {
        // TODO
        //float adaptedRatio = ratio * 0.7f + 0.15f;
        home_guideline_anim.setGuidelinePercent(ratio);
    }

    private String getTimerTextFor(long time) {
        long minutesRaw = TimeUnit.MILLISECONDS.toMinutes(time);
        if (minutesRaw > 60) {
            return "+59:59";
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

    @Override
    public void onInfoChange(String title, String notes, String category) {
        if (shownPomodoroRealm != null) {
            presenter.savePomodoroInfo(shownPomodoroRealm.getId(), title, notes, category);
        }
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
                                progress = (int) (((ellapsedTime + 0.0f) / shownPomodoroRealm.getPomodoroTimeInMillis()) * 100);
                                maxTime = shownPomodoroRealm.getPomodoroTimeInMillis();
                                if (ellapsedTime > shownPomodoroRealm.getPomodoroTimeInMillis()) ellapsedTime = shownPomodoroRealm.getPomodoroTimeInMillis();
                                break;
                            case PomodoroState.SHORT_BREAK:
                                ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
                                progress = (int) (((ellapsedTime + 0.0f) / shownPomodoroRealm.getShortBreakTimeInMillis()) * 100);
                                maxTime = shownPomodoroRealm.getShortBreakTimeInMillis();
                                if (ellapsedTime > shownPomodoroRealm.getShortBreakTimeInMillis()) ellapsedTime = shownPomodoroRealm.getShortBreakTimeInMillis();
                                break;
                            case PomodoroState.LONG_BREAK:
                                ellapsedTime = System.currentTimeMillis() - shownPomodoroRealm.getStartTimeMillis();
                                progress = (int) (((ellapsedTime + 0.0f) / shownPomodoroRealm.getLongBreakTimeInMillis()) * 100);
                                maxTime = shownPomodoroRealm.getLongBreakTimeInMillis();
                                if (ellapsedTime > shownPomodoroRealm.getLongBreakTimeInMillis()) ellapsedTime = shownPomodoroRealm.getLongBreakTimeInMillis();
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
                        setStageProgressBar(progress / 100f);
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
        showContinue();
    }

    private void handleContinueToShortBreak() {
        // TODO
        showContinue();
    }

    private void handleContinueToLongBreak() {
        // TODO
        showContinue();
    }

    private void showContinue() {
        home_btn_continue.setVisibility(View.VISIBLE);
    }

    private void hideContinue() {
        home_btn_continue.setVisibility(View.GONE);
    }

    private void showBreakLayer() {
        home_layer_warn.setVisibility(View.VISIBLE);
    }

    private void hideBreakLayer() {
        home_layer_warn.setVisibility(View.GONE);
    }

    private void setWorkingGif() {
        if (home_iv_anim != null && currentGif != R.raw.tomato) {
            currentGif = R.raw.tomato;
            Glide.with(getActivity())
                    .load(R.raw.tomato)
                    .into(home_iv_anim);
        }
    }

    private void setShortBreakGif() {
        if (home_iv_anim != null && currentGif != R.raw.sun) {
            currentGif = R.raw.sun;
            Glide.with(getActivity())
                    .load(R.raw.sun)
                    .into(home_iv_anim);
        }
    }

    private void setLongBreakGif() {
        if (home_iv_anim != null && currentGif != R.raw.party_ball) {
            currentGif = R.raw.party_ball;
            Glide.with(getActivity())
                    .load(R.raw.party_ball)
                    .into(home_iv_anim);
        }
    }

    private void disableGif() {
        if (home_iv_anim != null && currentGif != -1) {
            currentGif = -1;
            Bitmap nullBitmap = null;
            Glide.with(getActivity())
                    .load(nullBitmap)
                    .into(home_iv_anim);
        }
    }

}
