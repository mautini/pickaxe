package com.github.mautini.pickaxe.model;

import com.google.schemaorg.core.Thing;

public class Entity {

    private final String rawEntity;

    private final Thing thing;

    public Entity(String rawEntity, Thing thing) {
        this.rawEntity = rawEntity;
        this.thing = thing;
    }

    public String getRawEntity() {
        return rawEntity;
    }

    public Thing getThing() {
        return thing;
    }
}
