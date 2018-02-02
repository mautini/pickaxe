package com.github.mautini.pickaxe;

import com.github.mautini.pickaxe.extractor.Extractor;
import com.google.schemaorg.core.Thing;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Scraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scraper.class);

    private List<Extractor> extractors;

    public Scraper() {
        extractors = new ArrayList<>();
    }

    public List<Thing> extract(File file) throws IOException {
        Document document = Jsoup.parse(file, "UTF-8");

        return extractors.stream()
                .flatMap(extractor -> extractor.getEvents(document).stream())
                .collect(Collectors.toList());
    }
}
