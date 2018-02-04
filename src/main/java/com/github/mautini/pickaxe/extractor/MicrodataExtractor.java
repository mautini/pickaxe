package com.github.mautini.pickaxe.extractor;

import com.github.mautini.pickaxe.model.Schema;
import com.google.schemaorg.core.CoreConstants;
import com.google.schemaorg.core.CoreFactory;
import com.google.schemaorg.core.Thing;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MicrodataExtractor implements Extractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrodataExtractor.class);

    private static final String NAMESPACE = "http://schema.org/";

    private static final String ITEM_TYPE = "itemtype";

    private static final String ITEM_SCOPE = "itemscope";

    private static final String ITEM_PROP = "itemprop";

    @Override
    public List<Thing> getThings(Document document) {
        Elements elements = getElements(document);
        return elements.stream()
                .map(this::getTree)
                .map(this::toThing)
                .collect(Collectors.toList());
    }

    public Elements getElements(Document document) {
        // All the itemscope that don't have an itemscope in parent (to get the top level item scope)
        String query = String.format("[%s]:not([%<s] > [%<s])", ITEM_SCOPE);
        return document.select(query);
    }

    /**
     * Transform a microdata element into a tree
     *
     * @param parent the microdata element
     * @return a tree with the attributes and the objects of the element
     */
    private Schema getTree(Element parent) {
        String type = parent.attr(ITEM_TYPE);

        // Find all the attributes (itemprop)
        Elements elements = parent.select(String.format("> [%s]", ITEM_PROP));
        Map<String, List<String>> properties = elements.stream()
                .collect(
                        Collectors.groupingBy(
                                element -> element.attr(ITEM_PROP),
                                Collectors.mapping(
                                        Element::html, Collectors.toList()
                                )
                        )
                );

        Schema schema = new Schema();
        schema.setType(type);
        schema.setProperties(properties);

        // Find all the objects in this object and map them to Schema
        Elements children = parent.select(String.format("> [%s]", ITEM_SCOPE));
        schema.setChildren(
                children.stream()
                        .map(this::getTree)
                        .collect(Collectors.toList())
        );

        return schema;
    }

    private Thing toThing(Schema schema) {
        String typeName = schema.getType().substring(CoreConstants.NAMESPACE.length());
        String builderName = String.format("new%sBuilder", typeName);

        try {
            Thing.Builder thingBuilder = (Thing.Builder) CoreFactory.class.getMethod(builderName).invoke(null);

            // Set all the properties
            for (String propertyName : schema.getProperties().keySet()) {
                String methodName = String.format("add%s", capitalize(propertyName));
                Method method = thingBuilder.getClass().getMethod(methodName, String.class);
                method.setAccessible(true);
                method.invoke(thingBuilder, schema.getProperties().get(propertyName).get(0));
            }

            thingBuilder.build();

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return CoreFactory.newThingBuilder().build();
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
