# k3b-geoHelper library

A j2se geo support library that is compatible with Android 2.3 (api 9) or later

## Status

* Current Release 
  * [![GitHub release](https://img.shields.io/github/release/k3b/k3b-geoHelper.svg?maxAge=3600)](https://github.com/k3b/k3b-geoHelper/wiki/History)
* Sourcecode 
  * https://github.com/k3b/k3b-geoHelper/
* Supported geo properties
  * https://github.com/k3b/k3b-geoHelper/wiki/data
* Last Release build 
  * [![Build Status](https://travis-ci.org/k3b/k3b-geoHelper.svg?branch=master)](https://travis-ci.org/k3b/k3b-geoHelper)
  * Automated Tests Code Coverage: [![codecov](https://codecov.io/gh/k3b/k3b-geoHelper/branch/master/graph/badge.svg)](https://codecov.io/gh/k3b/k3b-geoHelper)
  * Code quality [![Codacy Badge](https://api.codacy.com/project/badge/Grade/0d3f1717026c429bb02c04ad8b7ed76d)](https://www.codacy.com/app/klaus3b-github/k3b-geoHelper?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=k3b/k3b-geoHelper&amp;utm_campaign=Badge_Grade)
  * [![Maven Central](https://img.shields.io/maven-central/v/com.github.k3b/k3b-geoHelper.svg)](http://search.maven.org/#search|ga|1|k3b-geoHelper)
  * [JCenter/bintray](https://bintray.com/k3b/maven/k3b-geoHelper/view)
  
* Licence
  * [<img src="https://img.shields.io/github/license/k3b/k3b-geoHelper.svg"></img>](https://github.com/k3b/k3b-geoHelper/blob/master/LICENSE)

## Purpose

A j2se geo support library that is compatible with Android 2.3 (api 9) or later

* parse/format [geo-uri-s](https://github.com/k3b/k3b-geoHelper/wiki/data#geo) and geoarea-uri-s .
	* Example uri: geo:53,10?q=(Hamburg)&z=8
	* Java examples [GeoUriTests.java](https://github.com/k3b/k3b-geoHelper/blob/master/k3b-geoHelper/src/test/java/de/k3b/geo/io/GeoUriTests.java)
* read geo-points from or write to in these file formats
	* [gpx](https://github.com/k3b/k3b-geoHelper/wiki/data#gpx)
	* [kml/kmz](https://github.com/k3b/k3b-geoHelper/wiki/data#kml)
	* [poi](https://github.com/k3b/k3b-geoHelper/wiki/data#poi)
	* [wikimedia](https://github.com/k3b/k3b-geoHelper/wiki/data#wikimedia) that is used by web-apis of wikipedia and wikivoyage
	* [Example files](https://github.com/k3b/k3b-geoHelper/blob/master/k3b-geoHelper/src/test/resources/de/k3b/geo/io/regressionTests/)
* parse different flavours of [ISO_8601 Date Formats](https://en.wikipedia.org/wiki/ISO_8601). 
	* Java examples [IsoDateTimeParserTests.java](https://github.com/k3b/k3b-geoHelper/blob/master/k3b-geoHelper/src/test/java/de/k3b/util/IsoDateTimeParserTests.java)

For more details see [supported data formats](https://github.com/k3b/k3b-geoHelper/wiki/data)

## Usage (with gradle) ##

Add dependencies to local build.gradle 

    repositories {
        ...
        maven { url "https://jitpack.io" }
    }

	dependencies {
		compile 'com.github.k3b:k3b-geoHelper:v1.1.12'
	}

## Dependencies ##

* org.slf4j:slf4j-api

## References ##

* [Changelog](https://github.com/k3b/k3b-geoHelper/wiki/History)
* [List of Android apps that support geo-uri](https://github.com/k3b/k3b-geoHelper/wiki/Android-Geo-aware-apps)
