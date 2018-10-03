package io.github.nfdz.tomatime.common.model;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PomodoroInfoRealm extends RealmObject implements Comparable<PomodoroInfoRealm> {

    @PrimaryKey
    private String key;
    public static final String KEY_FIELD = "key";

    private String title;

    private String notes;

    private String category;

    public PomodoroInfoRealm() {
        this.key = "";
        this.title = "";
        this.notes = "";
        this.category = "";
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public String getCategory() {
        return category;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "PomodoroInfoRealm={" + key + ", " + title + ", " + notes + ", " + category + "}";
    }

    public static String buildKeyFromTitle(String title) {
        return title.trim().replace(" ", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PomodoroInfoRealm that = (PomodoroInfoRealm) o;
        return (key == that.key) || (key != null && key.equals(that.key));
    }

    @Override
    public int hashCode() {
        return 31 + (key == null ? 0 : key.hashCode());
    }

    @Override
    public int compareTo(@NonNull PomodoroInfoRealm o) {
        return title.compareTo(o.title);
    }
}
