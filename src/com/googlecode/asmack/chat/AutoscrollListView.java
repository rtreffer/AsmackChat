/*
 * Licensed under Apache License, Version 2.0 or LGPL 2.1, at your option.
 * --
 *
 * Copyright 2010 Rene Treffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * --
 *
 * Copyright (C) 2010 Rene Treffer
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA
 */

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
