# Weather App

This is simple app that uses google map to display weather,
and gps feature that allows to display weather in your current location.

### Prerequisite
Target Sdk 23 and Min SDK requirement 21

 Becauses it uses googlemap, and googleApiClient for location you will need to have play service installed on your emulator

 ### General architecture
 This app builds on gradle 1.5

 It uses dagger2 to inject the manager service into activity and application
 depending on the scope. Retrofit2 is used to query openmap api. The application uses a
 simplified version of MVP on the main view, and two way databinding on the WeatherDetailActivity.
 I was thinking about caching the api calls on the client side to minimize the amount of data calls,
  but I assume for weather app user always wants the most updated info, so instead we only do
  http cache.
  http Image query we use universal image loader, and saves images to local file system, if the
  same image is queried again.


 #### Tests
 * Instrumental test - Uses mockito espresso to do the use case testing, and by using DI
 to inject mocked object for the services.

 * Unit test - uses mockito and dagger2 to inject mock into service, tried to use
  powermockito to mock some static services, but encountered some strange issues
  (needs revisit), but besides searchManager, most of the other services
   are wrapper of external libraries, so simply ignored tests on these wrapper services (ImageManager, WeatherApiManager)


 #### Requirements and use cases
 1. Search by city name or zip code

 In api service, defined 2 type of search functions "getWeatherByCity" and "getWeatherByLatLon"

 * getWeatherByCity simply takes in both city name and postcode, so no need to do post code parsing,
 the api simply handles it

 * after search, the result will appear as a weather image marker on googlemap, by clicking the marker
 will show you infoWindow with summary description of weather, and by clicking infoWindow it will take you to
 detailed weather page


 2. Search by GPS

 * getWeatherByLatLon , is used when GPS location is requested.
  If you use Android M, it will request permission at runtime



 3. Most recent search location loads automatically

* the app stores your last location onPause in sharedpreferences and restores your location on onResume event



 4. Recent Searches

 * implemented a fragment with recycler view which appears when clicked on search icon on the toolbar, the reason I use framgnet instead
 of autosearchcomplete is so you can have more control, like for example add a clear all button and can apply more styling to it.
 * For simplicity sake, I store the recently search as a arraylist in sharedPreference, for real applications, I would probably use a database
 * if a search history item is clicked it will move your recently clicked history on top of the list



 5. Delete recent searches

 * clear button visible on the search history fragment on the top right, when clicked a confirmation box shows up.



 6. Multi-market

 * created separate en-rAU/colors, en-rAU/styles, en-rCA/styles, en-rCA/styles, so by
 determinine your local, it will populate the appropiate app name and theme color.

#### Libaries used
Support Libraries :
```javascript
    compile 'com.android.support:appcompat-v7:23.1.1'
    compile 'com.android.support:design:23.1.1'
    compile 'com.google.android.gms:play-services-maps:8.4.0'
    compile 'com.google.android.gms:play-services-location:8.4.0'
```

Debug tools
```javascript
    //detect leaks
    debugCompile 'com.squareup.leakcanary:leakcanary-android:1.3.1'
    releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.3.1'


    //chrome hookup debugger
        compile 'com.facebook.stetho:stetho:1.3.1'
        compile 'com.facebook.stetho:stetho-okhttp3:1.3.1'

```

General development tool
```javascript
    compile 'com.jakewharton:butterknife:7.0.1' //template binding

    //http libraries
    compile 'com.squareup.retrofit2:retrofit:2.0.0'  //restful api service
    compile 'io.reactivex:rxjava:1.1.1'
    compile 'io.reactivex:rxandroid:1.1.0'
    compile 'com.squareup.retrofit2:adapter-rxjava:2.0.0'
    compile 'com.google.code.gson:gson:2.6.2'  //parsing json
    compile 'com.squareup.retrofit2:converter-gson:2.0.0'
    compile 'com.squareup.okhttp3:okhttp:3.1.2'

    //url image loader
    compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.4'

    //dagger2 dependency injection
    compile 'com.google.dagger:dagger:2.0'
    provided 'com.google.dagger:dagger-compiler:2.0'
    provided 'org.glassfish:javax.annotation:10.0-b28'

    //library to restore state
    compile 'frankiesardo:icepick:3.2.0'
    provided 'frankiesardo:icepick-processor:3.2.0'

    //parceler library
    compile 'org.parceler:parceler-api:1.0.4'
    apt 'org.parceler:parceler:1.0.4'

```

Testing libraries
```javascript
//testing suits
    testCompile 'junit:junit:4.12'
    testCompile 'org.mockito:mockito-core:1.10.8'
    testCompile 'org.powermock:powermock-api-easymock:1.6.4'
    testCompile 'org.powermock:powermock-module-junit4:1.6.4'
    testCompile 'org.powermock:powermock-module-junit4-legacy:1.6.4'
    testCompile 'org.powermock:powermock-api-mockito:1.6.4'

    androidTestCompile "org.mockito:mockito-core:1.10.8"
    androidTestCompile 'com.google.dexmaker:dexmaker-mockito:1.2'
    androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2.2'
    androidTestCompile 'com.android.support.test:runner:0.5'

```