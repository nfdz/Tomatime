package io.github.nfdz.tomatina.home.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.tomatina.R;

public class HomeFragment extends Fragment {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

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
    @BindView(R.id.home_guideline_anim) Guideline home_guideline_anim;
    @BindView(R.id.home_iv_anim) ImageView home_iv_anim;

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
        home_iv_global_long_break.setSelected(true);
    }

    private void setToggleButtonPlay() {

    }

    private void setToggleButtonStop() {

    }

    private void setInfoButtonEnabled(boolean enabled) {

    }

    private void setSettingsButtonEnabled(boolean enabled) {

    }

    @OnClick(R.id.home_btn_toggle_pomodoro)
    public void onTogglePomodoroClick() {

    }

    @OnClick(R.id.home_btn_info_pomodoro)
    public void onInfoPomodoroClick() {

    }

    @OnClick(R.id.home_btn_settings_pomodoro)
    public void onSettingsPomodoroClick() {

    }

}
