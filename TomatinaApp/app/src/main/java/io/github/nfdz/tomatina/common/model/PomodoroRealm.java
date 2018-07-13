package io.github.nfdz.tomatina.common.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PomodoroRealm extends RealmObject {

    @PrimaryKey
    private long id;
    public static final String ID_FIELD = "id";

    private long startTimeMillis;
    public static final String START_TIME_FIELD = "startTimeMillis";

    private int state;

    private int counter;

    private String title;

    private String notes;

    private long pomodoroTimeInMillis;

    private long shortBreakTimeInMillis;

    private long longBreakTimeInMillis;

    private int pomodorosToLongBreak;

    public PomodoroRealm() {
        this.id = System.currentTimeMillis();
        this.startTimeMillis = -1;
        this.state = PomodoroState.NONE;
        this.counter = -1;
        this.title = "";
        this.notes = "";
        this.pomodoroTimeInMillis = -1;
        this.shortBreakTimeInMillis = -1;
        this.longBreakTimeInMillis = -1;
        this.pomodorosToLongBreak = -1;

    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setState(@PomodoroState int state) {
        this.state = state;
    }

    public @PomodoroState int getState() {
        return state;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public long getPomodoroTimeInMillis() {
        return pomodoroTimeInMillis;
    }

    public long getShortBreakTimeInMillis() {
        return shortBreakTimeInMillis;
    }

    public long getLongBreakTimeInMillis() {
        return longBreakTimeInMillis;
    }

    public int getPomodorosToLongBreak() {
        return pomodorosToLongBreak;
    }

    public void setPomodoroTimeInMillis(long pomodoroTimeInMillis) {
        this.pomodoroTimeInMillis = pomodoroTimeInMillis;
    }

    public void setShortBreakTimeInMillis(long shortBreakTimeInMillis) {
        this.shortBreakTimeInMillis = shortBreakTimeInMillis;
    }

    public void setLongBreakTimeInMillis(long longBreakTimeInMillis) {
        this.longBreakTimeInMillis = longBreakTimeInMillis;
    }

    public void setPomodorosToLongBreak(int pomodorosToLongBreak) {
        this.pomodorosToLongBreak = pomodorosToLongBreak;
    }

    @Override
    public String toString() {
        return "PomodoroRealm={" + id + ", " + title + ", " + startTimeMillis + ", " + state + "}";
    }

}
