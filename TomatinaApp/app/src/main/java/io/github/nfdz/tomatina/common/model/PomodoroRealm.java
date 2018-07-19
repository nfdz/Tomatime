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

    private long pomodoroTimeInMillis;

    private long shortBreakTimeInMillis;

    private long longBreakTimeInMillis;

    private int pomodorosToLongBreak;

    private PomodoroInfoRealm info;

    public PomodoroRealm() {
        this.id = 0;
        this.startTimeMillis = 0;
        this.state = PomodoroState.NONE;
        this.counter = 0;
        this.pomodoroTimeInMillis = 0;
        this.shortBreakTimeInMillis = 0;
        this.longBreakTimeInMillis = 0;
        this.pomodorosToLongBreak = 0;
        this.info = null;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getId() {
        return id;
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

    public void setPomodoroInfo(PomodoroInfoRealm info) {
        this.info = info;
    }

    public PomodoroInfoRealm getPomodoroInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "PomodoroRealm={" + id + ", " + startTimeMillis + ", " + state + "}";
    }

    public boolean isOngoing() {
        return state == PomodoroState.WORKING || state == PomodoroState.SHORT_BREAK || state == PomodoroState.LONG_BREAK;
    }

}
