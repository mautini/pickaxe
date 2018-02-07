package com.github.mautini.pickaxe;

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
        List<Thing> thingList = scraper.extract(
                new File(getClass().getClassLoader().getResource("jsonld.html").getFile())
        );
        assertEvent(thingList);
    }

    @Test
    public void scraperMicrodataTest() throws IOException {
        Scraper scraper = new Scraper();
        List<Thing> thingList = scraper.extract(
                new File(getClass().getClassLoader().getResource("microdata.html").getFile())
        );
        assertEvent(thingList);
    }

    private void assertEvent(List<Thing> thingList) {
        Assertions.assertEquals(1, thingList.size());
        Assertions.assertTrue(thingList.get(0) instanceof Event);
        Event event = (Event) thingList.get(0);
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
        Assertions.assertEquals(((DataType) values.get(0)).getValue(), expected);
    }
}
