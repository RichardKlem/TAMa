# How to install the application
Locate downloaded .apk file and install it with your package installer.
No other steps are required.

# How to compile source code
 - Import project to the Android Studio.
 - Install any required plugins (e.g. kotlin plugin).
 - Add this line to the `./local.properties` file:<br>
`apiKey=<whatever_or_your_google_api_key>`
 - Build project in wanted build variant (debug, release).
 - Bare in mind, that you should provide your functional Google Platform API key
 in `./local.properties` or in `./app/src/release/res/values/google_maps_api.xml` if you want
 Google Maps to work properly.
If you do not provide API key you can still add predefined street Husova. In area or single street version. Just click on the plus button on the home page, then collapse keyboard and click Add button. You should see Husova street in your list of locations on the home page.
