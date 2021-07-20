package de.k3b.geo.api;

import org.junit.Test;

import static org.junit.Assert.*;

public class GeoPointDtoTest {

    @Test
    public void testToString() {
        GeoPointDto sut = new GeoPointDto()
                .setLatLon(52.51, 13.38)
                .setName("Berlin")
                .setId("0815");
        assertEquals("sut.toString()", "Berlin", sut.toString());
        assertEquals("GeoPointDto.toString(sut)", "Berlin(52.51,13.38)", GeoPointDto.toString(sut));
        assertEquals("GeoPointDto.toString(null)", "", GeoPointDto.toString(null));
    }
}