package com.github.mautini.pickaxe.extractor;

import com.github.mautini.pickaxe.model.Entity;
import org.jsoup.nodes.Document;

import java.util.List;

@FunctionalInterface
public interface Extractor {

    List<Entity> getThings(Document document);
}
