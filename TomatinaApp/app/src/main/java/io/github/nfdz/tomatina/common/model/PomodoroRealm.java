package io.github.nfdz.tomatina.common.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PomodoroRealm extends RealmObject {

    @PrimaryKey
    private long id;

    private long startTimeMillis;

    private int state;

    private String title;

    private String notes;

    public PomodoroRealm() {
        this.id = System.currentTimeMillis();
        this.startTimeMillis = -1;
        this.state = PomodoroState.NONE;
        this.title = "";
        this.notes = "";
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "PomodoroRealm={" + id + ", " + title + ", " + startTimeMillis + ", " + state + "}";
    }

}
