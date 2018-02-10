package com.github.mautini.pickaxe;

import com.github.mautini.pickaxe.extractor.Extractor;
import com.github.mautini.pickaxe.extractor.JsonLdExtractor;
import com.github.mautini.pickaxe.extractor.MicrodataExtractor;
import com.google.schemaorg.core.Thing;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Scraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scraper.class);

    private List<Extractor> extractors;

    public Scraper() {
        extractors = Arrays.asList(
                new JsonLdExtractor(),
                new MicrodataExtractor()
        );
    }

    public List<Thing> extract(File file) throws IOException {
        Document document = Jsoup.parse(file, "UTF-8");
        return scrap(document);
    }

    public List<Thing> extract(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        return scrap(document);
    }

    private List<Thing> scrap(Document document) {
        return extractors.stream()
                .flatMap(extractor -> extractor.getThings(document).stream())
                .collect(Collectors.toList());
    }
}
