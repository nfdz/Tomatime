package io.github.nfdz.tomatime.historical.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;

public class PomodoroHistoricalEntry implements Comparable<PomodoroHistoricalEntry> {

    public final String infoKey;

    public final String title;

    public final String notes;

    public final String category;

    public final int pomodorosCounter;

    public final int durationMin;

    public final long lastTimestamp;

    public PomodoroHistoricalEntry(String infoKey,
                                   String title,
                                   String notes,
                                   String category,
                                   int pomodorosCounter,
                                   int durationMin,
                                   long lastTimestamp) {
        this.infoKey = infoKey;
        this.title = title;
        this.notes = notes;
        this.category = category;
        this.pomodorosCounter = pomodorosCounter;
        this.durationMin = durationMin;
        this.lastTimestamp = lastTimestamp;
    }

    @Override
    public int compareTo(@NonNull PomodoroHistoricalEntry o) {
        return (o.lastTimestamp < lastTimestamp) ? -1 : ((o.lastTimestamp == lastTimestamp) ? 0 : 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PomodoroHistoricalEntry entry = (PomodoroHistoricalEntry) o;
        return pomodorosCounter == entry.pomodorosCounter &&
                durationMin == entry.durationMin &&
                lastTimestamp == entry.lastTimestamp &&
                TextUtils.equals(infoKey, entry.infoKey) &&
                TextUtils.equals(title, entry.title) &&
                TextUtils.equals(notes, entry.notes) &&
                TextUtils.equals(category, entry.category);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (infoKey == null ? 0 : infoKey.hashCode());
        result = 31 * result + (title == null ? 0 : title.hashCode());
        result = 31 * result + (notes == null ? 0 : notes.hashCode());
        result = 31 * result + (category == null ? 0 : category.hashCode());
        result = 31 * result + pomodorosCounter;
        result = 31 * result + durationMin;
        result = 31 * result + (int)(lastTimestamp ^ (lastTimestamp >>> 32));
        return result;
    }
}
