package com.github.mautini.pickaxe.extractor;

import com.google.schemaorg.core.Thing;
import org.jsoup.nodes.Document;

import java.util.List;

@FunctionalInterface
public interface Extractor {

    List<Thing> getThings(Document document);
}
