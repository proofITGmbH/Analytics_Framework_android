[![Release](https://jitpack.io/v/stanwood/Analytics_Framework_android.svg?style=flat-square)](https://jitpack.io/#stanwood/Analytics_Framework_android)

# Analytics Framework (Android)

This library contains a whole bunch of (mostly optional) Analytics and Reporting trackers for easy integration in your app.

## Import

The stanwood Analytics Framework is hosted on JitPack. Therefore you can simply import it by adding

```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

to your project's `build.gradle`.

Then add this to you app's `build.gradle`:

```groovy
dependencies {
    // AAR versions are available for all artifacts as well!

    // includes Fabric tracker and Firebase tracker by default
    implementation 'com.github.stanwood.Analytics_Framework_android:stanwoodanalytics:$latest_version'

    // Testfairy tracker - not optional, but also available as a no-op variant which doesn't pull in any dependencies or permissions
    debugImplementation 'com.github.stanwood.Analytics_Framework_android:testfairytrackingprovider:$latest_version'
    releaseImplementation 'com.github.stanwood.Analytics_Framework_android:testfairytrackingprovider-noop:$latest_version'

    // Google Analytics tracker - optional
    debugImplementation 'com.github.stanwood.Analytics_Framework_android:gatrackingprovider-noop:$latest_version'
    releaseImplementation 'com.github.stanwood.Analytics_Framework_android:gatrackingprovider:$latest_version'
    
    // Adjust tracker - optional
    debugImplementation 'com.github.stanwood.Analytics_Framework_android:adjusttrackingprovider-noop:$latest_version'
    releaseImplementation 'com.github.stanwood.Analytics_Framework_android:adjusttrackingprovider:$latest_version'

    // Bugfender tracker - optional & deprecated
    debugImplementation 'com.github.stanwood.Analytics_Framework_android:bugfendertrackingprovider-noop:$latest_version'
    releaseImplementation 'com.github.stanwood.Analytics_Framework_android:bugfendertrackingprovider:$latest_version'

    // Mixpanel Tracker - optional
    debugImplementation 'com.github.stanwood.Analytics_Framework_android:mixpaneltrackingprovider-noop:$latest_version'
    releaseImplementation 'com.github.stanwood.Analytics_Framework_android:mixpaneltrackingprovider:$latest_version'

    // Debugview Tracker - optional
    debugImplementation 'com.github.stanwood.Analytics_Framework_android:loggingtrackingprovider:$latest_version'
    releaseImplementation 'com.github.stanwood.Analytics_Framework_android:loggingtrackingprovider-noop:$latest_version'

    // Infoonline Tracker - optional
    debugImplementation "com.github.stanwood.Infonline_Analytics_Tracker_android:infoonlinetrackingprovider-noop:$analytics_version"
    releaseImplementation "com.github.stanwood.Infonline_Analytics_Tracker_android:infoonlinetrackingprovider:$analytics_version"
}
```

## Usage

The recommended way to integrate the library into an app is by subclassing the `BaseAnalyticsTracker` class and supplying it using the Singleton pattern:

```java
public class SimpleAppTracker extends BaseAnalyticsTracker {
    private static SimpleAppTracker instance;

    private SimpleAppTracker(@NonNull Context context, @NonNull FabricTracker fabricTracker, @NonNull FirebaseTracker firebaseTracker,
                             @NonNull TestfairyTracker testfairyTracker, @Nullable Tracker... optional) {
        super(context, fabricTracker, firebaseTracker, testfairyTracker, optional);
    }

    public static synchronized void init(Application application) {
        if (instance == null) {
            instance = new SimpleAppTracker(application, FabricTrackerImpl.builder(application).build(),
                    FirebaseTrackerImpl.builder(application).setExceptionTrackingEnabled(true).build(),
                    TestfairyTrackerImpl.builder(application, "KEY").build());
            FirebasePerformance.getInstance().setPerformanceCollectionEnabled(!BuildConfig.DEBUG);
            if (BuildConfig.DEBUG) {
                Timber.plant(new Timber.DebugTree());
            }
        }
    }

    public static SimpleAppTracker instance() {
        if (instance == null) {
            throw new IllegalArgumentException("Call init() first!");
        }
        return instance;
    }
}
```

Init it before calling through to it (best very early in the app, e.g. in your application's `onCreate()` method):

```java
AppTracker.init(getApplication());
```

Then you may call through to the `AppTracker` singleton from wherever you want to track something:

```java
AppTracker tracker = AppTracker.instance();

tracker.trackUser("alice", "alice@bob.com");
tracker.trackScreenView("home"); // this can be handy during migration from existing trackers, usually you should better define a more specific method like trackHome() in AppTracker
tracker.trackAdLoaded("123456");
tracker.trackShowDetails("id", "details of id");
tracker.trackException(new IllegalStateException("error"));
```

The `TrackingEvent` class contain predefined event names and keys you should use whenever possible when tracking events and keys.

For a more complete example refer to the `AdvancedAppTracker.java` class in the sample app module.


## Map functions

All trackers support so-called _map functions_. These functions map `TrackerParams` to whatever the tracking module can work with.

_Every tracker has a default map function. In general it makes sense to take a look at the various tracker implementations (esp. the `*Tracker.java` classes) to understand how they work and what needs to be adapted to have them working as you want. Keep in mind that the default configurations have been carefully done and represent the stanwood defaults to be used for all apps if possible._

For example if you have custom labels for Firebase Analytics property keys you can set them on your own using a map function:

```java
FirebaseTracker firebaseTracker = FirebaseTracker.builder(application)
    .mapFunction(new io.stanwood.framework.analytics.firebase.MapFunction() {
        @Override
        public Bundle map(TrackerParams params) {
            Bundle bundle = new Bundle();
            bundle.putString("category", params.getEventName());
            bundle.putString("action", params.getName());
            bundle.putString("label", params.getItemId());
            return bundle;
        }
    }).build();
```

Or if you just want to track a specific token to Adjust:

```java
Tracker adjustTracker = AdjustTracker.builder(application, "KEY")
    .mapFunction(new io.stanwood.framework.analytics.adjust.MapFunction() {
        @Override
        public String mapContentToken(TrackerParams params) {
            if (params.getEventName().equals(TrackingEvent.VIEW_ITEM) && params.getName().equals("home")) {
                return "ADJUST_CONTENT_ID";
            }
            return null;
        }
    })
    .build();
```

## Opt-in/out

### All trackers are DISABLED by default!

Use BaseAnalyticsTrackers `enable(boolean)` function to change the state of your trackers.

e.g. Set all trackers to enabled:

```enable(true);```

Set Fabric and Firebase to enabled (This will not change the enabled state of any other tracker)

```enable(true, FabricTracker.TRACKER_NAME, FirebaseTracker.TRACKER_NAME);```

Trackers enabled state is persisted during app sessions.

Check if a tracker is enabled:

```isTrackerEnabled(FabricTracker.TRACKER_NAME)```


**Always double-check your app by actively testing it after implementing opt-in/out to ensure that all trackers have been properly configured!**

#### _noop_ module`s
All tracking providers are also implemented as an `noop` version. 
They don`t execute any tracking code and doesn't pull in any dependencies or permissions.
Use this to e.g disable tracking in debug or qa builds.

You could configure the dependency in your app's `build.gradle` like so:

```groovy
debugImplementation 'com.github.stanwood.Analytics_Framework_android:testfairytrackingprovider:$latest_version'
releaseImplementation 'com.github.stanwood.Analytics_Framework_android:testfairytrackingprovider-noop:$latest_version'
```

## Tracker specific documentation

### Testfairy

#### okhttp interceptor

The Testfairy module also contains an okhttp `Interceptor` called `TestfairyHttpInterceptor`.

This interceptor is needed to log calls to testfairy and is purely optional.

Add it to your okhttp client as an _app interceptor_:

```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new TestfairyHttpInterceptor())
    .build();
```

You can also use this without modification in release builds, just make sure to use the _noop_ module for these builds instead of the regular one. The _noop_ version doesn't execute any own code and thus doesn't track network calls to Testfairy.


### Firebase Crashlytics (for future reference, currently this library only offers plain old Fabric)

#### opt-in/out

_It is **not** possible to reenable crash tracking for a running session. The user has to restart the app to get crash tracking back to work._

### Firebase Performance (not included in the library)

#### opt-in/out

To disable auto-intialisation of Firebase Performance at app start (e.g. because you want to wait for user-consent) you need to add this line to your manifest:

```xml
<meta-data android:name="firebase_performance_collection_enabled" android:value="false" />
```

Later on you can enable it with

```java
FirebasePerformance.getInstance().setPerformanceCollectionEnabled(true);
```

as outlined in the example above.

### Infoonline
As the SDK is only available as AAR file the library needs to be copied into application's library folder and include via:
```groovy
releaseImplementation 'de.infonline.lib:infonlinelib_x.x.x@aar'
```
