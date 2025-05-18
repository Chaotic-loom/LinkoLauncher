package com.chaotic_loom.registries;

public abstract class RegistryObject {
    private Identifier identifier;
    private boolean isPopulated = false;

    public Identifier getIdentifier() {
        if (identifier == null) {
            throw new RuntimeException("This RegistryObject is not populated yet");
        }

        return this.identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    //Gets called when the game registers the item
    public void onPopulate() {
        this.isPopulated = true;
    }

    public boolean isPopulated() {
        return this.isPopulated;
    }
}
