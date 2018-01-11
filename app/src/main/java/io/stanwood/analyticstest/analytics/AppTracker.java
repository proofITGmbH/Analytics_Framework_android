package io.stanwood.analyticstest.analytics;

import android.app.Application;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import io.stanwood.framework.analytics.BaseAnalyticsTracker;
import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerKeys;
import io.stanwood.framework.analytics.TrackerParams;
import io.stanwood.framework.analytics.TrackingEvent;
import io.stanwood.framework.analytics.adjust.AdjustTracker;
import io.stanwood.framework.analytics.bugfender.BugfenderTracker;
import io.stanwood.framework.analytics.fabric.FabricTracker;
import io.stanwood.framework.analytics.firebase.FirebaseTracker;
import io.stanwood.framework.analytics.firebase.MapFunction;
import io.stanwood.framework.analytics.ga.GoogleAnalyticsTracker;
import io.stanwood.framework.analytics.mixpanel.MixpanelTracker;

public class AppTracker extends BaseAnalyticsTracker {
    private static AppTracker instance;

    private AppTracker(FabricTracker fabricTracker, FirebaseTracker firebaseTracker, BugfenderTracker bugfenderTracker, Tracker... optional) {
        super(fabricTracker, firebaseTracker, bugfenderTracker, optional);
    }

    public static synchronized void init(Application application) {
        if (instance == null) {
            FirebaseTracker firebaseTracker = FirebaseTracker.builder(application)
                    .setExceptionTrackingEnabled(true)
                    .setDebug(BuildConfig.DEBUG)
                    .mapFunction(new MapFunction() {
                        @Override
                        public Bundle map(TrackerParams params) {
                            Bundle bundle = new Bundle();
                            bundle.putString("category", params.getCategory());
                            bundle.putString("action", params.getName());
                            bundle.putString("label", params.getItemId());
                            return bundle;
                        }
                    }).build();
            Tracker adjustTracker = AdjustTracker.builder(application, "KEY")
                    .setDebug(BuildConfig.DEBUG)
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
            Tracker mixpanelTracker = MixpanelTracker.builder(application, "KEY")
                    .setDebug(BuildConfig.DEBUG)
                    .mapFunction(new io.stanwood.framework.analytics.mixpanel.MapFunction() {
                        @Override
                        public Map<String, String> map(TrackerParams params) {
                            Map<String, String> mapped = new HashMap<>(3);
                            mapped.put("category", params.getCategory());
                            mapped.put("action", params.getName());
                            mapped.put("label", params.getItemId());
                            return mapped;
                        }
                    })
                    .build();
            Tracker gaTracker = GoogleAnalyticsTracker.builder(application, "KEY")
                    .setExceptionTrackingEnabled(true)
                    .setDebug(BuildConfig.DEBUG)
                    .build();
            FabricTracker fabricTracker = FabricTracker.builder(application).setDebug(BuildConfig.DEBUG).build();
            BugfenderTracker bugfenderTracker = BugfenderTracker.builder(application, "KEY").setDebug(!BuildConfig.DEBUG).build();
            instance = new AppTracker(fabricTracker, firebaseTracker, bugfenderTracker, mixpanelTracker, adjustTracker, gaTracker);
        }
    }

    public static AppTracker instance() {
        if (instance == null) {
            throw new IllegalArgumentException("Call init() first!");
        }
        return instance;
    }

    public void trackAdLoaded(String adId) {
        trackParameter(TrackerParams.builder("ad").setName("loaded").setId(adId).build());
    }

    public void trackShowDetails(String id, String name) {
        trackKeys(TrackerKeys.builder().addCustomProperty("id", id).addCustomProperty("name", name).build());
        trackScreenView("details");
    }
}