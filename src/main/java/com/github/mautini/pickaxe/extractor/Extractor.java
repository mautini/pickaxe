package com.github.mautini.pickaxe.extractor;

import com.google.schemaorg.core.Event;
import org.jsoup.nodes.Document;

import java.util.List;

@FunctionalInterface
public interface Extractor {

    List<Event> getEvents(Document document);
}
