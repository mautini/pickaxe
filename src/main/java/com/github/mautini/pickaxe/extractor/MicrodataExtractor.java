package com.github.mautini.pickaxe.extractor;

import com.github.mautini.pickaxe.SchemaToThingConverter;
import com.github.mautini.pickaxe.model.Entity;
import com.github.mautini.pickaxe.model.Schema;
import com.google.schemaorg.core.Thing;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MicrodataExtractor implements Extractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrodataExtractor.class);

    private static final String ITEM_TYPE = "itemtype";

    private static final String ITEM_SCOPE = "itemscope";

    private static final String ITEM_PROP = "itemprop";

    private static final String HYPERLINK_TAG = "a";

    @Override
    public List<Entity> getThings(Document document) {
        Elements elements = getElements(document);

        return elements.stream()
                .map(element -> {
                    Schema schema = getTree(element);
                    Thing thing = SchemaToThingConverter.convert(schema);

                    return new Entity(element.toString(), thing);
                })
                .collect(Collectors.toList());
    }

    private Elements getElements(Document document) {
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
        // Find all the attributes (itemprop)
        Elements elements = parent.select(String.format("> [%s]:not([%s])", ITEM_PROP, ITEM_SCOPE));
        Map<String, List<String>> properties = elements.stream()
                .collect(
                        Collectors.groupingBy(
                                element -> element.attr(ITEM_PROP),
                                Collectors.mapping(
                                        this::getValue, Collectors.toList()
                                )
                        )
                );

        Schema schema = new Schema();
        schema.setType(parent.attr(ITEM_TYPE));
        schema.setPropertyName(parent.attr(ITEM_PROP));
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

    private String getValue(Element element) {
        if (HYPERLINK_TAG.equals(element.tagName()) && element.hasAttr("href")) {
            return element.attr("href");
        }

        if (element.hasAttr("content")) {
            return element.attr("content");
        }

        return element.html();
    }
}
