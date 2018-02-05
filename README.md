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
    implementation 'com.github.stanwood.Analytics_Framework_android:gatrackingprovider:$latest_version'

    // Adjust tracker - optional
    implementation 'com.github.stanwood.Analytics_Framework_android:adjusttrackingprovider:$latest_version'

    // Bugfender tracker - optional
    implementation 'com.github.stanwood.Analytics_Framework_android:bugfendertrackingprovider:$latest_version'

    // Mixpanel Tracker - optional
    implementation 'com.github.stanwood.Analytics_Framework_android:mixpaneltrackingprovider:$latest_version'
}
```

## Usage

The recommended way to integrate the library into an app is by subclassing the `BaseAnalyticsTracker` class and supplying it using the Singleton pattern:

```java
public class SimpleAppTracker extends BaseAnalyticsTracker {
    private static SimpleAppTracker instance;

    private SimpleAppTracker(@NonNull FabricTracker fabricTracker, @NonNull FirebaseTracker firebaseTracker,
                             @NonNull TestfairyTracker testfairyTracker, @Nullable Tracker... optional) {
        super(fabricTracker, firebaseTracker, testfairyTracker, optional);
    }

    public static synchronized void init(Application application) {
        if (instance == null) {
            instance = new SimpleAppTracker(
                FabricTracker.builder(application).isSandbox(BuildConfig.DEBUG).build(),
                FirebaseTracker.builder(application).setExceptionTrackingEnabled(true).isSandbox(BuildConfig.DEBUG).build(),
                TestfairyTrackerImpl.builder(application, "KEY").isSandbox(!BuildConfig.DEBUG).build()
            );

            // we opted to not calling this within the Firebase tracker module for you because enabling/disabling FirebasePerformance often differs from the sandbox setting for this module
            FirebasePerformance.getInstance().setPerformanceCollectionEnabled(!BuildConfig.DEBUG);
        }
    }

    // Singleton
    public static SimpleAppTracker instance() {
        if (instance == null) {
            throw new IllegalArgumentException("Call init() first!");
        }
        return instance;
    }

    // add your tracking methods here - that will give you a nice overview over all tracking that's happening in your app
    public void trackAdLoaded(String adId) {
        trackEvent(
            TrackerParams.builder("ad").setName("loaded").setId(adId).build()
        );
    }

    ...
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

The `TrackingEvent` and `TrackingKey` classes contain predefined event names and keys you should use whenever possible when tracking events and keys.

For a more complete example refer to the `AdvancedAppTracker.java` class in the sample app module.

## Map functions

All trackers support so-called _map functions_. These functions map `TrackerParams` to whatever the tracking module can work with.

_Every tracker has a default map function. In general it makes sense to take a look at the various tracker implementations (esp. the `*Tracker.java` classes) to understand how they work and what needs to be adapted to have them working as you want. Keep in mind that the default configurations have been carefully done and represent the stanwood defaults to be used for all apps if possible._

For example if you have custom labels for Firebase Analytics property keys you can set them on your own using a map function:

```java
FirebaseTracker firebaseTracker = FirebaseTracker.builder(application)
    .setExceptionTrackingEnabled(true)
    .isSandbox(BuildConfig.DEBUG)
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
    .isSandbox(BuildConfig.DEBUG)
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