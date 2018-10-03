package io.github.nfdz.tomatime.common.utils;


import android.support.v7.util.DiffUtil;

import java.util.Collections;
import java.util.List;

public class SimpleDiffUtilListCallback<T> extends DiffUtil.Callback {

    public interface EqualsStrategy<T> {
        boolean sameItem(T item1, T item2);
        boolean sameContent(T item1, T item2);
    }

    private final EqualsStrategy<T> strategy;
    private final List<T> oldList;
    private final List<T> newList;

    public SimpleDiffUtilListCallback(List<T> oldList, List<T> newList, EqualsStrategy<T> strategy) {
        this.oldList = oldList != null ? oldList : Collections.<T>emptyList();
        this.newList = newList != null ? newList : Collections.<T>emptyList();
        this.strategy = strategy;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return strategy.sameItem(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return strategy.sameContent(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }

}