package io.github.nfdz.tomatina.common.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PomodoroInfoRealm extends RealmObject {

    @PrimaryKey
    private String key;
    public static final String KEY_FIELD = "key";

    private String title;

    private String notes;

    private String category;

    public PomodoroInfoRealm() {
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

}
