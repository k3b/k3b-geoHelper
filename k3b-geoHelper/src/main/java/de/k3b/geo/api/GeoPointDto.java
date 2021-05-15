/*
 * Copyright (c) 2015-2021 by k3b.
 *
 * This file is part of k3b-geoHelper library.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.k3b.geo.api;

import java.util.Date;

/**
 * A minimal location or trackpoint implementation of {@link ILocation}, {@link IGeoPointInfo}.
 *
 * Created by k3b on 07.01.2015.
 */
public class GeoPointDto implements ILocation, IGeoPointInfo, Cloneable   {
    /** Latitude, in degrees north. {@link #NO_LAT_LON} means "not set" */
    private double latitude = NO_LAT_LON;

    /** Longitude, in degrees east. {@link #NO_LAT_LON} means "not set" */
    private double longitude = NO_LAT_LON;

    /** Date when the measurement was taken. Null if unknown. */
    private Date timeOfMeasurement = null;

    /** Short non-unique text used as marker label. Null means "not set". */
    private String name = null;

    /** Detailed descript of the point displayed in popup on long-click . Null means "not set". */
    private String description = null;

    /** Filter: This item is only shown if current zoom-level is >= this value.
     * NO_ZOOM means no lower bound. */
    private int zoomMin = NO_ZOOM;

    /** Filter: This item is only shown if current zoom-level is <= this value.
     * NO_ZOOM means no upper bound. */
    private int zoomMax = NO_ZOOM;

    /** If not null: A unique id for this item. */
    private String id = null;

    /** Optional: If not null: Link-url belonging to this item.
     *
     * In show view after clicking on ">" in marker: opens this url.
     *
     * Persistet in geo-uri as geo:...&link=https://path/to/file.html. */
    private String link = null;

    /** Optional: If not null: Url to an icon belonging to this item.<br/>
     * Persistet in geo-uri as geo:...&s=https://path/to/file.html . */
    private String symbol = null;

    public GeoPointDto() {
    }

    public GeoPointDto(double latitude, double longitude,
                       String name, String description) {
        this(latitude, longitude,name, null, null, null, description, NO_ZOOM, NO_ZOOM, null);
    }

    public GeoPointDto(double latitude, double longitude,int zoomMin) {
        this(latitude, longitude,null, null, null, null, null, zoomMin, NO_ZOOM, null);
    }

    public GeoPointDto(double latitude, double longitude,
                       String name, String link, String symbol,
                       String id,
                       String description, int zoomMin, int zoomMax, Date timeOfMeasurement) {
        setLatLon(latitude, longitude);
        this.name = name;
        this.link = link;
        this.symbol = symbol;
        this.id = id;
        this.description = description;
        this.zoomMin = zoomMin;
        this.zoomMax = zoomMax;
        this.timeOfMeasurement = timeOfMeasurement;
    }

    public GeoPointDto setLatLon(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
        return this;
    }

    public GeoPointDto(IGeoPointInfo src) {
        if (src != null) {
            setLatLon(src.getLatitude(), src.getLongitude());
            this.name = src.getName();
            this.link = src.getLink();
            this.symbol = src.getSymbol();
            this.id = src.getId();
            this.description = src.getDescription();
            this.zoomMin = src.getZoomMin();
            this.zoomMax = src.getZoomMax();
            this.timeOfMeasurement = src.getTimeOfMeasurement();
        }
    }

    /** Latitude, in degrees north.
     *
     * {@link #NO_LAT_LON} means "not set". */
    public GeoPointDto setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    /** Latitude, in degrees north.
     *
     * {@link #NO_LAT_LON} means "not set". */
    @Override
    public double getLatitude() {
        return latitude;
    }

    /** Longitude, in degrees east.
     *
     * {@link #NO_LAT_LON} means "not set". */
    public GeoPointDto setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    /** Longitude, in degrees east.
     *
     * {@link #NO_LAT_LON} means "not set". */
    @Override
    public double getLongitude() {
        return longitude;
    }

    /** Date when the measurement was taken.
     *
     * Null means unknown. */
    public GeoPointDto setTimeOfMeasurement(Date timeOfMeasurement) {
        this.timeOfMeasurement = timeOfMeasurement;
        return this;
    }

    /**
     * Date when the measurement was taken. Null if unknown.
     */
    @Override
    public Date getTimeOfMeasurement() {
        return timeOfMeasurement;
    }

    /**
     * Short non-unique text used as marker label. Null means "not set".
     */
    public GeoPointDto setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Short non-unique text used as marker label. Null means "not set".
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Detailed descript of the point displayed in popup on long-click . Null means "not set".
     */
    public GeoPointDto setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Detailed descript of the point displayed in popup on long-click . Null means "not set".
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Filter: This item is only shown if current zoom-level is >= this value. NO_ZOOM means no lower bound.
     */
    @Override
    public int getZoomMin() {
        return zoomMin;
    }

    /**
     * Filter: This item is only shown if current zoom-level is >= this value. NO_ZOOM means no lower bound.
     */
    public GeoPointDto setZoomMin(int zoomMin) {
        this.zoomMin = zoomMin;
        return this;
    }

    /**
     * Filter: This item is only shown if current zoom-level is <= this value. NO_ZOOM means no upper bound.
     */
    @Override
    public int getZoomMax() {
        return zoomMax;
    }

    /**
     * Filter: This item is only shown if current zoom-level is <= this value. NO_ZOOM means no upper bound.
     */
    public GeoPointDto setZoomMax(int zoomMax) {
        this.zoomMax = zoomMax;
        return this;
    }

    /**
     * If not null: A unique id for this item.
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * If not null: A unique id for this item.
     */
    public GeoPointDto setId(String id) {
        this.id = id;
        return this;
    }

    /** Optional: If not null: Link-url belonging to this item.
     *
     * After clicking on ">"-button in "show view" of a marker: opens this url.
     *
     * Persistet in geo-uri as geo:...&link=https://path/to/file.html. */
    @Override
    public String getLink() {
        return link;
    }

    /** Optional: If not null: Link-url belonging to this item.
     *
     * After clicking on ">"-button in "show view" of a marker: opens this url.
     *
     * Persistet in geo-uri as geo:...&link=https://path/to/file.html. */
    public GeoPointDto setLink(String link) {
        this.link = link;
        return this;
    }

    /** Optional: If not null: Icon-url belonging to this item.
     *
     * Persistet in geo-uri as geo:...&s=https://path/to/file.png */
    @Override
    public String getSymbol() {
        return symbol;
    }

    /** Optional: If not null: Icon-url belonging to this item.
     *
     * Persistet in geo-uri as geo:...&s=https://path/to/file.png */
    public GeoPointDto setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    /** Sets all members back to defaultvalue to allow reuse of class.
     *
     * {@link #clone()} and {@link #clear()} are used as factory method when reading a list.
     * 
     * @return this to to allow chains
     */
    public GeoPointDto clear() {
        setLatLon(GeoPointDto.NO_LAT_LON, GeoPointDto.NO_LAT_LON);
        this.name = null;
        this.link = null;
        this.symbol = null;
        this.id = null;
        this.description = null;
        this.zoomMin = NO_ZOOM;
        this.zoomMax = NO_ZOOM;
        this.timeOfMeasurement = null;
        return this;
    }

    /** Create a copy.
     *
     * {@link #clone()} and {@link #clear()} are used as factory method when reading a list. */
    @Override
    public GeoPointDto clone() {
        try {
            GeoPointDto result = (GeoPointDto) super.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** For display/debugging: Either the {@link #getName()}
     * or #{@link #getId()}. */
    @Override
    public String toString() {
        if (this.name != null) return this.name;
        if (this.id != null) return "#" + this.id;
        return super.toString();
    }

    /** return true if either lat or lon is not set (NaN) or if both are 0. */
    public static boolean isEmpty(ILocation point) {
        if (point != null) {
            return (isEmpty(point.getLatitude(), point.getLongitude()));
        }
        return true;
    }

    public static boolean isEmpty(double value) {
        return (value == NO_LAT_LON); // (Double.isNaN(value)|| (value == NO_LAT_LON) || (value < -180) || (value > 180));
    }


    /** return true if either lat or lon is not set (NaN) or if both are 0. */
    public static boolean isEmpty(double latitude, double longitude) {
        if (isEmpty(latitude) || isEmpty(longitude)) return true;
        return ((latitude == 0.0f) && (longitude == 0.0f));
    }

    @Override
    public boolean equals(Object other) {
        if ((other == null) || !(other instanceof IGeoPointInfo)) return false;
        return equals(this, (IGeoPointInfo) other);
    }

    public static boolean equals(IGeoPointInfo lhs, IGeoPointInfo rhs) {
        if ((lhs == null) && (rhs == null)) return true;
        if ((lhs == null) || (rhs == null)) return false;

        if (lhs.getId() != null) return lhs.getId().compareTo(rhs.getId()) == 0;

        return (lhs.getLatitude() == rhs.getLatitude()) && (lhs.getLongitude() == rhs.getLongitude());
    }
}
