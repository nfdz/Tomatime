package io.github.nfdz.tomatina.historical.model;

import android.support.annotation.NonNull;

public class PomodoroHistoricalEntry implements Comparable<PomodoroHistoricalEntry> {

    public final String infoKey;

    public final String title;

    public final String notes;

    public final String category;

    public final int pomodorosCounter;

    public PomodoroHistoricalEntry(String infoKey,
                                   String title,
                                   String notes,
                                   String category,
                                   int pomodorosCounter) {
        this.infoKey = infoKey;
        this.title = title;
        this.notes = notes;
        this.category = category;
        this.pomodorosCounter = pomodorosCounter;
    }

    @Override
    public int compareTo(@NonNull PomodoroHistoricalEntry o) {
        return title.compareTo(o.title);
    }

}
