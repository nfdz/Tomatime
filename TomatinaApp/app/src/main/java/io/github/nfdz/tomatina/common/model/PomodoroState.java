package io.github.nfdz.tomatina.common.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({
        PomodoroState.NONE,
        PomodoroState.WORKING,
        PomodoroState.SHORT_BREAK,
        PomodoroState.LONG_BREAK,
        PomodoroState.FINISHED
})
public @interface PomodoroState {

    int NONE = 0;
    int WORKING = 100;
    int SHORT_BREAK = 200;
    int LONG_BREAK = 300;
    int FINISHED = 400;

}
