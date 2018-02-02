package com.github.mautini.pickaxe;

import com.google.schemaorg.core.Thing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScraperTest {

    @Test
    public void scraperJsonLdTest() throws IOException {
        Scraper scraper = new Scraper();
        List<Thing> thingList = scraper.extract(
                new File(getClass().getClassLoader().getResource("jsonld.html").getFile())
        );
        Assertions.assertEquals(1, thingList.size());
    }

    @Test
    public void scraperMicrodataTest() throws IOException {
        Scraper scraper = new Scraper();
        List<Thing> thingList = scraper.extract(
                new File(getClass().getClassLoader().getResource("microdata.html").getFile())
        );
        Assertions.assertEquals(1, thingList.size());
    }
}
