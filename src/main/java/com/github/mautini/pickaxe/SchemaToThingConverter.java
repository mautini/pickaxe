package com.github.mautini.pickaxe;

import com.github.mautini.pickaxe.model.Schema;
import com.google.schemaorg.core.CoreConstants;
import com.google.schemaorg.core.CoreFactory;
import com.google.schemaorg.core.Thing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SchemaToThingConverter {

    public static Thing convert(Schema schema) {

        try {
            Thing.Builder thingBuilder = getBuilder(schema);

            // Set all the properties
            for (String propertyName : schema.getProperties().keySet()) {
                String methodName = String.format("add%s", capitalize(propertyName));
                Method method = thingBuilder.getClass().getMethod(methodName, String.class);
                method.setAccessible(true);
                method.invoke(thingBuilder, schema.getProperties().get(propertyName).get(0));
            }

            return thingBuilder.build();

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Thing.Builder getBuilder(Schema schema) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {

        String typeName = schema.getType().substring(CoreConstants.NAMESPACE.length());
        String builderName = String.format("new%sBuilder", typeName);
        return (Thing.Builder) CoreFactory.class.getMethod(builderName).invoke(null);
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
