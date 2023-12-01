package org.example;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
class FunctionDefinition {
    private final String name;
    private final String description;
    private final JsonSchema parameters;

    public FunctionDefinition(String name, String description, JsonSchema parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public JsonSchema getParameters() {
        return parameters;
    }
}