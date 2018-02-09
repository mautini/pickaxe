package com.github.mautini.pickaxe;

import com.github.mautini.pickaxe.model.Schema;
import com.google.schemaorg.core.CoreConstants;
import com.google.schemaorg.core.CoreFactory;
import com.google.schemaorg.core.Thing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SchemaToThingConverter {

    private static final String PACKAGE_SCHEMAORG = "com.google.schemaorg.core";

    public static Thing convert(Schema schema) {

        try {
            // Find the type of the schema we're currently converting
            String typeName = getTypeName(schema);
            // In order to build a schema.org thing, we use builders
            // We need the class of the builder to find the method to call for settings the properties
            Class<?> builderClass = getBuilderClass(typeName);
            /*
             * Create the builder for the thing we're building
             * Note that builderClass != thingBuilder.getClass(). We use the first one as it's an interface and all
             * the methods are public and can be called using reflection.
             */
            Thing.Builder thingBuilder = getBuilder(typeName);

            // Set all the properties
            for (String propertyName : schema.getProperties().keySet()) {
                String methodName = String.format("add%s", capitalize(propertyName));
                Method method = builderClass.getMethod(methodName, String.class);
                method.invoke(thingBuilder, schema.getProperties().get(propertyName).get(0));
            }

            return thingBuilder.build();

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return a schema.org build for the specified type
     * @param typeName the type of the builder we want
     * @return the builder
     * @throws NoSuchMethodException if we can't find the function used to get the builder
     * @throws InvocationTargetException if we can't invoke the function used to get the builder
     * @throws IllegalAccessException if the function used to get the builder is inaccessible
     */
    private static Thing.Builder getBuilder(String typeName) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {

        String builderName = String.format("new%sBuilder", typeName);
        return (Thing.Builder) CoreFactory.class.getMethod(builderName).invoke(null);
    }

    /**
     * Find the class builder for a schema
     *
     * @param typeName the type name of the schema
     * @return the class builder
     * @throws ClassNotFoundException if the class for the specified type name does not exist
     */
    private static Class<?> getBuilderClass(String typeName) throws ClassNotFoundException {
        String className = String.format("%s.%s$Builder", PACKAGE_SCHEMAORG, typeName);

        return Class.forName(className);
    }

    /**
     * Get the type name of a schema
     *
     * @param schema the schema
     * @return the schema.org type name of the schema
     */
    private static String getTypeName(Schema schema) {
        return schema.getType().substring(CoreConstants.NAMESPACE.length());
    }

    /**
     * Capitalize the first letter of the string in parameter
     *
     * @param name the string to capitalize
     * @return the capitalized string
     */
    private static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
