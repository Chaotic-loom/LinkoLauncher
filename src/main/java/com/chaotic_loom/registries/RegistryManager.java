package com.chaotic_loom.registries;

import com.chaotic_loom.events.RegistryEvents;
import com.chaotic_loom.events.components.EventResult;
import com.chaotic_loom.registries.built_in.Shaders;
import com.chaotic_loom.util.Loggers;

import java.util.ArrayList;
import java.util.List;

public class RegistryManager {
    private static List<AbstractRegistryInitializer> builtInRegistries = new ArrayList<>();

    public static void init() {
        registerBuiltInRegistrySet(
                new Shaders()
        );

        registerBuiltIn();
    }

    private static void registerBuiltIn() {
        Loggers.REGISTRY.info("Starting all the built-in registries");

        for (AbstractRegistryInitializer abstractRegistryInitializer : builtInRegistries) {
            Loggers.REGISTRY.info("Starting built-in registry: {}", abstractRegistryInitializer.getClass().getName());
            abstractRegistryInitializer.register();
        }
    }


    private static void registerBuiltInRegistrySet(AbstractRegistryInitializer... abstractRegistryInitializers) {
        for (AbstractRegistryInitializer abstractRegistryInitializer : abstractRegistryInitializers) {
            registerBuiltInRegistrySet(abstractRegistryInitializer);
        }
    }

    private static void registerBuiltInRegistrySet(AbstractRegistryInitializer abstractRegistryInitializer) {
        EventResult eventResult = RegistryEvents.SET_REGISTRATION.invoker().onEvent(abstractRegistryInitializer, true);
        if (eventResult == EventResult.CANCELED) {
            return;
        }

        builtInRegistries.add(abstractRegistryInitializer);

        RegistryEvents.SET_REGISTRATION.invoker().onEvent(abstractRegistryInitializer, true);
    }
}
