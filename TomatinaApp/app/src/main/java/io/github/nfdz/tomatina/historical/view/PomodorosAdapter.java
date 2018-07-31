package io.github.nfdz.tomatina.historical.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.TomatinaApp;
import io.github.nfdz.tomatina.historical.model.PomodoroHistoricalEntry;
import timber.log.Timber;

public class PomodorosAdapter extends RecyclerView.Adapter<PomodorosAdapter.PomodoroViewHolder> {

    public interface Callback {
        void onPomodoroClick(PomodoroHistoricalEntry entry);
        void onStartPomodoroClick(PomodoroHistoricalEntry entry);
        void onDeletePomodoroClick(PomodoroHistoricalEntry entry);
    }

    private final LayoutInflater layoutInflater;
    private final Callback callback;

    private List<PomodoroHistoricalEntry> data;

    public PomodorosAdapter(Context context, Callback callback) {
        this.callback = callback;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<PomodoroHistoricalEntry> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PomodoroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_pomodoro, parent, false);
        return new PomodoroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PomodoroViewHolder holder, int position) {
        try {
            PomodoroHistoricalEntry entry = data.get(position);
            holder.bindEntry(entry);
        } catch (Exception e) {
            Timber.e(e, "Cannot bind pomodoro entry view holder");
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class PomodoroViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_pomodoro_tv_counter) TextView item_pomodoro_tv_counter;
        @BindView(R.id.item_pomodoro_tv_counter_label) TextView item_pomodoro_tv_counter_label;
        @BindView(R.id.item_pomodoro_tv_title) TextView item_pomodoro_tv_title;

        PomodoroViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindEntry(PomodoroHistoricalEntry entry) {
            item_pomodoro_tv_counter.setText(String.valueOf(entry.pomodorosCounter));
            item_pomodoro_tv_title.setText(entry.title);
        }

        @OnClick(R.id.item_pomodoro_root)
        public void onPomodoroClick(View v) {
            try {
                PomodoroHistoricalEntry entry = data.get(getAdapterPosition());
                callback.onPomodoroClick(entry);
            } catch (Exception e) {
                Timber.e(e, "Cannot handle pomodoro view holder click");
            }
        }

        @OnClick(R.id.item_pomodoro_iv_play)
        public void onStartPomodoroClick() {
            try {
                PomodoroHistoricalEntry entry = data.get(getAdapterPosition());
                callback.onStartPomodoroClick(entry);
            } catch (Exception e) {
                Timber.e(e, "Cannot handle pomodoro view holder play click");
            }
        }

        @OnClick(R.id.item_pomodoro_iv_delete)
        public void onDeletePomodorosClick() {
            try {
                PomodoroHistoricalEntry entry = data.get(getAdapterPosition());
                callback.onDeletePomodoroClick(entry);
            } catch (Exception e) {
                Timber.e(e, "Cannot handle pomodoro view holder delete click");
            }
        }

    }

}
