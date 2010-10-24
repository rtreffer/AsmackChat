package com.googlecode.asmack.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A list that automatically stays at the bottom if it was scrolled to the
 * bottom.
 */
public class AutoscrollListView extends ListView {

    /**
     * Create a new autoscroll list.
     * @param context The context of this list.
     * @param attrs Additinal attributes of this list.
     * @param defStyle Default style of this list.
     */
    public AutoscrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Create a new autoscroll list.
     * @param context The context of this list.
     * @param attrs Additinal attributes of this list.
     */
    public AutoscrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Create a new autoscroll list.
     * @param context The context of this list.
     */
    public AutoscrollListView(Context context) {
        super(context);
    }

    /**
     * Set the adapter of this list and add an autoscroll observer.
     * @param adapter The ListAdapter instance used as a data backend.
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        adapter.registerDataSetObserver(new AutoscrollDataSetObserver(this));
    }

    /**
     * Called on resize, triggers a scroll event if needed.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (h > oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            return;
        }
        boolean end = getLastVisiblePosition() + 1 == getCount();
        super.onSizeChanged(w, h, oldw, oldh);
        if (end) {
            smoothScrollToPosition(getCount() - 1);
        }
    }

}
