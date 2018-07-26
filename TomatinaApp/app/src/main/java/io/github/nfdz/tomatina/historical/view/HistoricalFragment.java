package io.github.nfdz.tomatina.historical.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.common.model.PomodoroInfoRealm;
import io.github.nfdz.tomatina.common.model.PomodoroRealm;
import io.github.nfdz.tomatina.historical.HistoricalContract;

public class HistoricalFragment extends Fragment implements HistoricalContract.View {

    public static HistoricalFragment newInstance() {
        return new HistoricalFragment();
    }

    @BindView(R.id.historical_rv_categories) RecyclerView historical_rv_categories;
    @BindView(R.id.historical_rv_pomodoros) RecyclerView historical_rv_pomodoros;

    public HistoricalFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historical, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void showData(Map<PomodoroInfoRealm, List<PomodoroRealm>> data) {

    }

    @Override
    public void showCategories(List<String> categories) {

    }

    @Override
    public void setSelectedCategories(List<String> selectedCategories) {

    }
}
