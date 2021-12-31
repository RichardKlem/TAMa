# How to install the application
Locate downloaded .apk file and install it with your package installer.
No other steps are required.

# How to compile source code
 - Import project to the Android Studio.
 - Install any required plugins (e.g. kotlin plugin).
 - Build project in wanted build variant (debug, release).
 - Bare in mind, that you should provide your Google Platform API key
 in `./local.properties` or in `./app/src/release/res/values/google_maps_api.xml` if you want
 Google Maps to work properly.