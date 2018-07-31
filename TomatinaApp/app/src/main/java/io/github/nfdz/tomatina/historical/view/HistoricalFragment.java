package io.github.nfdz.tomatina.historical.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.common.dialog.PomodoroInfoDialog;
import io.github.nfdz.tomatina.common.utils.SnackbarUtils;
import io.github.nfdz.tomatina.historical.HistoricalContract;
import io.github.nfdz.tomatina.historical.model.PomodoroHistoricalEntry;
import io.github.nfdz.tomatina.historical.presenter.HistoricalPresenter;

public class HistoricalFragment extends Fragment implements HistoricalContract.View,
        CategoriesAdapter.Callback, PomodorosAdapter.Callback, PomodoroInfoDialog.UpdateInfoCallback {

    public static HistoricalFragment newInstance() {
        return new HistoricalFragment();
    }

    @BindView(R.id.historical_rv_categories) RecyclerView historical_rv_categories;
    @BindView(R.id.historical_rv_pomodoros) RecyclerView historical_rv_pomodoros;

    private HistoricalContract.Presenter presenter;
    private CategoriesAdapter categoriesAdapter;
    private PomodorosAdapter pomodorosAdapter;
    private PomodoroHistoricalEntry dialogEntry;

    public HistoricalFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historical, container, false);
        ButterKnife.bind(this, view);
        setupView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new HistoricalPresenter(this);
        presenter.create();
    }

    @Override
    public void onDestroyView() {
        presenter.destroy();
        super.onDestroyView();
    }

    private void setupView() {
        setupCategories();
        setupPomodoros();
    }

    private void setupCategories() {
        categoriesAdapter = new CategoriesAdapter(getActivity(), this);
        historical_rv_categories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        historical_rv_categories.setAdapter(categoriesAdapter);
    }

    private void setupPomodoros() {
        pomodorosAdapter = new PomodorosAdapter(getActivity(), this);
        historical_rv_pomodoros.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        historical_rv_pomodoros.setAdapter(pomodorosAdapter);
    }

    @Override
    public void showData(List<PomodoroHistoricalEntry> data) {
        pomodorosAdapter.setData(data);
    }

    @Override
    public void showCategories(Set<String> categories) {
        categoriesAdapter.setCategories(categories);
    }

    @Override
    public void setSelectedCategories(Set<String> selectedCategories) {
        categoriesAdapter.setSelectedCategories(selectedCategories);
    }

    @Override
    public void showPomodoroInfoDialog(PomodoroHistoricalEntry entry) {
        this.dialogEntry = entry;
        PomodoroInfoDialog dialog = PomodoroInfoDialog.newInstance(entry.title, entry.notes, entry.category);
        dialog.setCallback(this);
        dialog.show(getFragmentManager(), "pomodoro_info_dialog");
    }

    @Override
    public void showSaveInfoError() {
        SnackbarUtils.show(getView(), R.string.home_save_info_error, Snackbar.LENGTH_LONG);
    }

    @Override
    public void showSaveInfoConflict(final PomodoroHistoricalEntry entry, final String title, final String notes, final String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppAlertDialog);
        builder.setTitle(R.string.info_conflict_dialog_title)
                .setMessage(R.string.info_conflict_dialog_content)
                .setPositiveButton(R.string.info_conflict_dialog_overwrite,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                presenter.overwritePomodoroInfo(entry, title, notes, category);
                                dialog.dismiss();
                            }
                        }
                )
                .setNeutralButton(R.string.info_conflict_dialog_existing,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                presenter.useExistingPomodoroInfo(entry, title, notes, category);
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
    public void navigateToPomodoro() {
        // TODO
    }

    @Override
    public void onCategoryClick(String category) {
        presenter.onCategoryClick(category);
    }

    @Override
    public void onInfoChange(String title, String notes, String category) {
        if (dialogEntry != null) {
            presenter.savePomodoroInfo(dialogEntry, title, notes, category);
            dialogEntry = null;
        }
    }

    @Override
    public void onPomodoroClick(PomodoroHistoricalEntry entry) {
        presenter.onPomodoroClick(entry);
    }

    @Override
    public void onStartPomodoroClick(PomodoroHistoricalEntry entry) {
        presenter.onStartPomodoroClick(entry);
    }

    @Override
    public void onDeletePomodoroClick(PomodoroHistoricalEntry entry) {
        presenter.onDeletePomodoroClick(entry);
    }

}
