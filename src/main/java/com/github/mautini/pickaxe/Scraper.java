package com.github.mautini.pickaxe;

import com.github.mautini.pickaxe.extractor.Extractor;
import com.github.mautini.pickaxe.extractor.JsonLdExtractor;
import com.github.mautini.pickaxe.extractor.MicrodataExtractor;
import com.github.mautini.pickaxe.model.Entity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Scraper {

    private List<Extractor> extractors;

    public Scraper() {
        extractors = Arrays.asList(
                new JsonLdExtractor(),
                new MicrodataExtractor()
        );
    }

    public List<Entity> extract(File file) throws IOException {
        Document document = Jsoup.parse(file, "UTF-8");
        return scrap(document);
    }

    public List<Entity> extract(URL url, int timeout) throws IOException {
        Document document = Jsoup.parse(url, timeout);
        return scrap(document);
    }

    public List<Entity> extract(String html) {
        Document document = Jsoup.parse(html);
        return scrap(document);
    }

    public List<Entity> scrap(Document document) {
        return extractors.stream()
                .flatMap(extractor -> extractor.getThings(document).stream())
                .collect(Collectors.toList());
    }
}
