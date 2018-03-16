package io.stanwood.analyticstest.analytics;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.perf.FirebasePerformance;

import java.util.HashMap;
import java.util.Map;

import io.stanwood.framework.analytics.BaseAnalyticsTracker;
import io.stanwood.framework.analytics.adjust.AdjustTracker;
import io.stanwood.framework.analytics.fabric.FabricTracker;
import io.stanwood.framework.analytics.firebase.DefaultMapFunction;
import io.stanwood.framework.analytics.firebase.FirebaseTracker;
import io.stanwood.framework.analytics.ga.GoogleAnalyticsTracker;
import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;
import io.stanwood.framework.analytics.mixpanel.MixpanelTracker;
import io.stanwood.framework.analytics.testfairy.TestfairyTracker;
import io.stanwood.framework.analytics.testfairy.TestfairyTrackerImpl;
import timber.log.Timber;

public class AdvancedAppTracker extends BaseAnalyticsTracker {
    private static AdvancedAppTracker instance;

    private AdvancedAppTracker(@NonNull FabricTracker fabricTracker, @NonNull FirebaseTracker firebaseTracker,
                               @NonNull TestfairyTracker testfairyTracker, @Nullable Tracker... optional) {
        super(fabricTracker, firebaseTracker, testfairyTracker, optional);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static synchronized void init(Application application) {
        if (instance == null) {
            FirebaseTracker firebaseTracker = FirebaseTracker.builder(application)
                    .setExceptionTrackingEnabled(true)
                    .setEnabled(!BuildConfig.DEBUG)
                    .mapFunction(new DefaultMapFunction() {
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
                    .setEnabled(!BuildConfig.DEBUG)
                    .mapFunction(new io.stanwood.framework.analytics.adjust.DefaultMapFunction() {
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
                    .setEnabled(!BuildConfig.DEBUG)
                    .mapFunction(new io.stanwood.framework.analytics.mixpanel.DefaultMapFunction() {
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
                    .setEnabled(!BuildConfig.DEBUG)
                    .build();
            FabricTracker fabricTracker = FabricTracker.builder(application).setEnabled(!BuildConfig.DEBUG).build();
            TestfairyTracker testfairyTracker = TestfairyTrackerImpl.builder(application, "KEY").setEnabled(BuildConfig.DEBUG).build();
            instance = new AdvancedAppTracker(fabricTracker, firebaseTracker, testfairyTracker, mixpanelTracker, adjustTracker, gaTracker);
            FirebasePerformance.getInstance().setPerformanceCollectionEnabled(!BuildConfig.DEBUG);
        }
    }

    public static AdvancedAppTracker instance() {
        if (instance == null) {
            throw new IllegalArgumentException("Call init() first!");
        }
        return instance;
    }

    public void trackAdLoaded(String adId) {
        trackEvent(TrackerParams.builder("ad").setName("loaded").setId(adId).build());
    }

    public void trackShowDetails(String id, String name) {
        trackKeys(TrackerKeys.builder("show_details").addCustomProperty("id", id).addCustomProperty("name", name).build());
        trackScreenView("details");
    }
}