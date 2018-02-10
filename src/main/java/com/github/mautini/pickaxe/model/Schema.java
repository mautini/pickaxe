package com.github.mautini.pickaxe.model;

import java.util.List;
import java.util.Map;

public class Schema {

    private String type;

    private String propertyName;

    private Map<String, List<String>> properties;

    private List<Schema> children;

    public Schema() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Map<String, List<String>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, List<String>> properties) {
        this.properties = properties;
    }

    public List<Schema> getChildren() {
        return children;
    }

    public void setChildren(List<Schema> children) {
        this.children = children;
    }
}
