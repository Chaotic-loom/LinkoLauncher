package com.chaotic_loom.events;

import com.chaotic_loom.graphics.Window;
import com.chaotic_loom.events.components.Event;
import com.chaotic_loom.events.components.EventFactory;

public abstract class WindowEvents {
    /**
        RESIZE EVENT
        <br/><br/>

        Details:
            Gets called when the window changes its size.
            It also gets called on Windows if it loses the focus.
        <br/><br/>

        Since: a-0
        <br/>
        Author: restonic4
     */

    public static final Event<Resize> RESIZE = EventFactory.createArray(Resize.class, callbacks -> (window, width, height) -> {
        for (Resize callback : callbacks) {
            callback.onEvent(window, width, height);
        }
    });

    @FunctionalInterface
    public interface Resize {
        void onEvent(Window window, int width, int height);
    }
}
