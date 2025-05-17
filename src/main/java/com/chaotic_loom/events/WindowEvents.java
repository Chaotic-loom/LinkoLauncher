package com.chaotic_loom.events;

import com.chaotic_loom.graphics.Window;
import com.chaotic_loom.events.components.Event;
import com.chaotic_loom.events.components.EventFactory;

public abstract class WindowEvents {
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
