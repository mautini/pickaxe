package com.github.mautini.pickaxe.extractor;

import com.github.mautini.pickaxe.model.Entity;
import com.google.schemaorg.JsonLdSerializer;
import com.google.schemaorg.JsonLdSyntaxException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonLdExtractor implements Extractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonLdSerializer.class);

    private JsonLdSerializer jsonLdSerializer;

    public JsonLdExtractor() {
        jsonLdSerializer = new JsonLdSerializer(true);
    }

    @Override
    public List<Entity> getThings(Document document) {
        Elements elements = getElements(document);

        return elements.stream()
                .flatMap(element -> parseThings(element).stream())
                .collect(Collectors.toList());
    }

    /**
     * Get the useful elements for this extractor (the json ld scripts)
     *
     * @param document the original document
     * @return the list of elements
     */
    private Elements getElements(Document document) {
        return document.select("script[type$=application/ld+json]");
    }

    /**
     * Return a list of things from an element of the source (an application/ld+json script)
     * using the jsonLdSerializer
     *
     * @param element the element to parse
     * @return the list of entity if any
     */
    private List<Entity> parseThings(Element element) {

        try {
            return jsonLdSerializer.deserialize(element.html()).stream()
                    .map(thing -> new Entity(element.toString(), thing))
                    .collect(Collectors.toList());
        } catch (JsonLdSyntaxException e) {
            // Fail to parse the json-ld, return an empty array list
            LOGGER.warn("Error during the json-ld parsing", e);
            return new ArrayList<>();
        }
    }
}
