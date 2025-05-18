package com.chaotic_loom.events;

import com.chaotic_loom.events.components.Event;
import com.chaotic_loom.events.components.EventFactory;
import com.chaotic_loom.events.components.EventResult;
import com.chaotic_loom.graphics.Window;
import com.chaotic_loom.registries.AbstractRegistryInitializer;

public abstract class RegistryEvents {
    /**
        SET_REGISTRATION EVENT
        <br/><br/>

        Details:
            Gets called a set is being registered to the registry system.
        <br/><br/>

        Since: a-1
        <br/>
        Author: restonic4
     */

    public static final Event<SetRegistration> SET_REGISTRATION = EventFactory.createArray(SetRegistration.class, callbacks -> (abstractRegistryInitializer, builtIn) -> {
        for (SetRegistration callback : callbacks) {
            if (callback.onEvent(abstractRegistryInitializer, builtIn) == EventResult.CANCELED) {
                return EventResult.CANCELED;
            }
        }

        return EventResult.SUCCEEDED;
    });

    @FunctionalInterface
    public interface SetRegistration {
        EventResult onEvent(AbstractRegistryInitializer abstractRegistryInitializer, boolean builtIn);
    }
}
