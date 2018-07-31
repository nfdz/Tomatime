package io.github.nfdz.tomatina.historical.model;

import android.support.annotation.NonNull;

public class PomodoroHistoricalEntry implements Comparable<PomodoroHistoricalEntry> {

    public final String infoKey;

    public final String title;

    public final String notes;

    public final String category;

    public final int pomodorosCounter;

    public final long lastTimestamp;

    public PomodoroHistoricalEntry(String infoKey,
                                   String title,
                                   String notes,
                                   String category,
                                   int pomodorosCounter,
                                   long lastTimestamp) {
        this.infoKey = infoKey;
        this.title = title;
        this.notes = notes;
        this.category = category;
        this.pomodorosCounter = pomodorosCounter;
        this.lastTimestamp = lastTimestamp;
    }

    @Override
    public int compareTo(@NonNull PomodoroHistoricalEntry o) {
        return (o.lastTimestamp < lastTimestamp) ? -1 : ((o.lastTimestamp == lastTimestamp) ? 0 : 1);
    }

}
