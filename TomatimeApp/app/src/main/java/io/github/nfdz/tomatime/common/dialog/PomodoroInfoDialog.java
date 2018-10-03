package io.github.nfdz.tomatime.common.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nfdz.tomatime.R;

public class PomodoroInfoDialog extends DialogFragment {

    public interface UpdateInfoCallback {
        void onInfoChange(String title, String notes, String category);
    }

    public static PomodoroInfoDialog newInstance(@Nullable String title, @Nullable String notes, @Nullable String category) {
        PomodoroInfoDialog fragment = new PomodoroInfoDialog();
        Bundle args = new Bundle();
        args.putString(TITLE_EXTRA, title);
        args.putString(NOTES_EXTRA, notes);
        args.putString(CATEGORY_EXTRA, category);
        fragment.setArguments(args);
        return fragment;
    }

    private static final String TITLE_EXTRA = "title";
    private static final String NOTES_EXTRA = "notes";
    private static final String CATEGORY_EXTRA = "category";

    @BindView(R.id.info_et_title) EditText info_tied_title;
    @BindView(R.id.info_et_notes) EditText info_tied_notes;
    @BindView(R.id.info_et_category) EditText info_tied_category;

    private UpdateInfoCallback callback;

    private String initialTitle = "";
    private String initialNotes = "";
    private String initialCategory = "";

    public PomodoroInfoDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            initialTitle = args.getString(TITLE_EXTRA, "");
            initialNotes = args.getString(NOTES_EXTRA, "");
            initialCategory = args.getString(CATEGORY_EXTRA, "");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pomodoro_info, null);
        ButterKnife.bind(this, dialogView);
        info_tied_category.append(initialCategory);
        info_tied_notes.setText(initialNotes);
        info_tied_title.append(initialTitle);
        info_tied_category.clearFocus();
        info_tied_notes.clearFocus();
        info_tied_title.clearFocus();
        builder.setView(dialogView);

        final AlertDialog dialog = builder.setTitle(null)
                .setPositiveButton(R.string.info_dialog_save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (callback != null) {
                                    String title = info_tied_title.getText().toString();
                                    String notes = info_tied_notes.getText().toString();
                                    String category = info_tied_category.getText().toString();
                                    boolean anyChange = !initialTitle.equals(title) ||
                                            !initialCategory.equals(category) ||
                                            !initialNotes.equals(notes);
                                    if (anyChange) {
                                        callback.onInfoChange(title, notes, category);
                                    }
                                }
                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton(R.string.info_dialog_close,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                ).create();
        return dialog;
    }

    public void setCallback(UpdateInfoCallback callback) {
        this.callback = callback;
    }

}
