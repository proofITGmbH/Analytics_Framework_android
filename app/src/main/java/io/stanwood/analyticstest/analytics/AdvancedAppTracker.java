package io.stanwood.analyticstest.analytics;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.perf.FirebasePerformance;

import java.util.HashMap;
import java.util.Map;

import io.stanwood.framework.analytics.BaseAnalyticsTracker;
import io.stanwood.framework.analytics.TrackerTree;
import io.stanwood.framework.analytics.adjust.AdjustTracker;
import io.stanwood.framework.analytics.fabric.FabricTracker;
import io.stanwood.framework.analytics.firebase.FirebaseTracker;
import io.stanwood.framework.analytics.firebase.MapFunction;
import io.stanwood.framework.analytics.ga.GoogleAnalyticsTracker;
import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;
import io.stanwood.framework.analytics.mixpanel.MixpanelTracker;
import io.stanwood.framework.analytics.testfairy.TestfairyTracker;
import timber.log.Timber;

public class AdavancedAppTracker extends BaseAnalyticsTracker {
    private static AdavancedAppTracker instance;

    private AdavancedAppTracker(@NonNull FabricTracker fabricTracker, @NonNull FirebaseTracker firebaseTracker,
                                @NonNull TestfairyTracker testfairyTracker, @Nullable Tracker... optional) {
        super(fabricTracker, firebaseTracker, testfairyTracker, optional);
        if (!BuildConfig.DEBUG) {
            Timber.plant(new TrackerTree(this));
        }
    }

    public static synchronized void init(Application application) {
        if (instance == null) {
            FirebaseTracker firebaseTracker = FirebaseTracker.builder(application)
                    .setExceptionTrackingEnabled(true)
                    .isSandbox(BuildConfig.DEBUG)
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
            Tracker mixpanelTracker = MixpanelTracker.builder(application, "KEY")
                    .isSandbox(BuildConfig.DEBUG)
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
                    .isSandbox(BuildConfig.DEBUG)
                    .build();
            FabricTracker fabricTracker = FabricTracker.builder(application).isSandbox(BuildConfig.DEBUG).build();
            TestfairyTracker testfairyTracker = TestfairyTracker.builder(application, "KEY").isSandbox(!BuildConfig.DEBUG).build();
            instance = new AdavancedAppTracker(fabricTracker, firebaseTracker, testfairyTracker, mixpanelTracker, adjustTracker, gaTracker);
            FirebasePerformance.getInstance().setPerformanceCollectionEnabled(!BuildConfig.DEBUG);
        }
    }

    public static AdavancedAppTracker instance() {
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