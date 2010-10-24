package com.googlecode.asmack.chat;

import android.database.DataSetObserver;
import android.widget.ListView;

/**
 * A simple observer to scroll to the bottom of the list on invalidate,
 * in case the list was at the bottem. This is especially usefull when an
 * on screen keyboard pops up.
 */
public class AutoscrollDataSetObserver extends DataSetObserver {

    /**
     * The list view that is observed.
     */
    private final ListView view;

    public AutoscrollDataSetObserver(ListView view) {
        this.view = view;
    }

    /**
     * Triggers a scroll event on change. if needed.
     */
    @Override
    public void onChanged() {
        onInvalidated();
    }

    /**
     * Triggers a scroll event if needed.
     */
    @Override
    public void onInvalidated() {
        int position = view.getLastVisiblePosition();
        if (position + 2 == view.getCount()) {
            view.smoothScrollToPosition(position + 1);
        }
    }

}
