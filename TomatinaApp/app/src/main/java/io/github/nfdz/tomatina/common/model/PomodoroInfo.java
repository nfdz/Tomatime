package io.github.nfdz.tomatina.common.model;

public class PomodoroInfo {

    public final long pomodoroId;

    public final String title;

    public final String notes;

    public PomodoroInfo(long pomodoroId, String title, String notes) {
        this.pomodoroId = pomodoroId;
        this.title = title;
        this.notes = notes;
    }

}
