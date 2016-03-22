#k3b-geoHelper v1.1

A j2se geo support library that is compatible with Android

* parse/format geo and geoarea uri-s like geo:53,10?q=(Hamburg)&z=8
* read geo-points from gpx/kml/poi files
* parse different flavours of [ISO_8601 Date Formats](en.wikipedia.org/wiki/ISO_8601)

##Usage: (with gradle)##

Add repository to root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}

Add dependencies to local build.gradle

	dependencies {
		compile 'com.github.k3b:k3b-geoHelper:v1.1'
	}

for details see https://jitpack.io/

##Licence:##

[GPLv3](http://www.gnu.org/licenses/gpl-3.0)<br/>

##Dependencies##

* org.slf4j:slf4j-api

##Other Projects using this lib##

* https://github.com/k3b/LocationMapViewer
* https://github.com/k3b/APhotoManager

