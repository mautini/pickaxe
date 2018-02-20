package com.github.mautini.pickaxe;

import com.github.mautini.pickaxe.model.Entity;
import com.google.common.collect.ImmutableList;
import com.google.schemaorg.SchemaOrgType;
import com.google.schemaorg.core.Event;
import com.google.schemaorg.core.Offer;
import com.google.schemaorg.core.Place;
import com.google.schemaorg.core.PostalAddress;
import com.google.schemaorg.core.Thing;
import com.google.schemaorg.core.datatype.DataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScraperTest {

    @Test
    public void scraperJsonLdTest() throws IOException {
        Scraper scraper = new Scraper();
        List<Entity> entityList = scraper.extract(
                new File(getClass().getClassLoader().getResource("jsonld.html").getFile())
        );

        Assertions.assertEquals(1, entityList.size());
        Entity entity = entityList.get(0);
        Assertions.assertEquals("<script type=\"application/ld+json\">\n" +
                "{\n" +
                "  \"@context\": \"http://schema.org\",\n" +
                "  \"@type\": \"Event\",\n" +
                "  \"location\": {\n" +
                "    \"@type\": \"Place\",\n" +
                "    \"address\": {\n" +
                "      \"@type\": \"PostalAddress\",\n" +
                "      \"addressLocality\": \"Denver\",\n" +
                "      \"addressRegion\": \"CO\",\n" +
                "      \"postalCode\": \"80209\",\n" +
                "      \"streetAddress\": \"7 S. Broadway\"\n" +
                "    },\n" +
                "    \"name\": \"The Hi-Dive\"\n" +
                "  },\n" +
                "  \"name\": \"Typhoon with Radiation City\",\n" +
                "  \"offers\": {\n" +
                "    \"@type\": \"Offer\",\n" +
                "    \"price\": \"13.00\",\n" +
                "    \"priceCurrency\": \"USD\",\n" +
                "    \"url\": \"http://www.ticketfly.com/purchase/309433\"\n" +
                "  },\n" +
                "  \"startDate\": \"2013-09-14T21:30\"\n" +
                "}\n" +
                "</script>", entity.getRawEntity());
        assertEvent(entity.getThing());
    }

    @Test
    public void scraperMicrodataTest() throws IOException {
        Scraper scraper = new Scraper();
        List<Entity> entityList = scraper.extract(
                new File(getClass().getClassLoader().getResource("microdata.html").getFile())
        );
        Assertions.assertEquals(1, entityList.size());
        Entity entity = entityList.get(0);
        Assertions.assertEquals("<div class=\"event-wrapper\" itemscope itemtype=\"http://schema.org/Event\">\n" +
                "    <div class=\"event-date\" itemprop=\"startDate\" content=\"2013-09-14T21:30\">Sat Sep 14</div>\n" +
                "    <div class=\"event-title\" itemprop=\"name\">Typhoon with Radiation City</div>\n" +
                "    <div class=\"event-venue\" itemprop=\"location\" itemscope itemtype=\"http://schema.org/Place\">\n" +
                "        <span itemprop=\"name\">The Hi-Dive</span>\n" +
                "        <div class=\"address\" itemprop=\"address\" itemscope itemtype=\"http://schema.org/PostalAddress\">\n" +
                "            <span itemprop=\"streetAddress\">7 S. Broadway</span>\n" +
                "            <br>\n" +
                "            <span itemprop=\"addressLocality\">Denver</span>,\n" +
                "            <span itemprop=\"addressRegion\">CO</span>\n" +
                "            <span itemprop=\"postalCode\">80209</span>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class=\"event-time\">9:30 PM</div>\n" +
                "    <span itemprop=\"offers\" itemscope itemtype=\"http://schema.org/Offer\">\n" +
                "        <div class=\"event-price\" itemprop=\"price\" content=\"13.00\">$13.00</div>\n" +
                "        <span itemprop=\"priceCurrency\" content=\"USD\" />\n" +
                "        <a itemprop=\"url\" href=\"http://www.ticketfly.com/purchase/309433\">Tickets</a>\n" +
                "    </span>\n" +
                "</div>", entity.getRawEntity());
        assertEvent(entity.getThing());
    }

    private void assertEvent(Thing thing) {
        Assertions.assertTrue(thing instanceof Event);
        Event event = (Event) thing;
        assertUniqueValue(event.getStartDateList(), "2013-09-14T21:30");
        assertUniqueValue(event.getNameList(), "Typhoon with Radiation City");

        Assertions.assertEquals(1, event.getLocationList().size());
        Assertions.assertTrue(event.getLocationList().get(0) instanceof Place);
        Place place = (Place) event.getLocationList().get(0);
        assertUniqueValue(place.getNameList(), "The Hi-Dive");

        Assertions.assertEquals(1, place.getAddressList().size());
        Assertions.assertTrue(place.getAddressList().get(0) instanceof PostalAddress);
        PostalAddress postalAddress = (PostalAddress) place.getAddressList().get(0);
        assertUniqueValue(postalAddress.getStreetAddressList(), "7 S. Broadway");
        assertUniqueValue(postalAddress.getAddressLocalityList(), "Denver");
        assertUniqueValue(postalAddress.getAddressRegionList(), "CO");
        assertUniqueValue(postalAddress.getPostalCodeList(), "80209");

        Assertions.assertEquals(1, event.getOffersList().size());
        Assertions.assertTrue(event.getOffersList().get(0) instanceof Offer);
        Offer offer = (Offer) event.getOffersList().get(0);
        assertUniqueValue(offer.getPriceList(), "13.00");
        assertUniqueValue(offer.getPriceCurrencyList(), "USD");
        assertUniqueValue(offer.getUrlList(), "http://www.ticketfly.com/purchase/309433");
    }

    private void assertUniqueValue(ImmutableList<SchemaOrgType> values, String expected) {
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals(expected, ((DataType) values.get(0)).getValue());
    }
}
