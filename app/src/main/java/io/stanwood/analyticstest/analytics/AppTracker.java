package io.stanwood.analyticstest.analytics;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.stanwood.framework.analytics.BaseAnalyticsTracker;
import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerContainer;
import io.stanwood.framework.analytics.TrackerKeys;
import io.stanwood.framework.analytics.TrackerParams;
import io.stanwood.framework.analytics.TrackingEvent;
import io.stanwood.framework.analytics.adjust.AdjustTracker;
import io.stanwood.framework.analytics.firebase.FirebaseTracker;
import io.stanwood.framework.analytics.firebase.MapFunction;
import io.stanwood.framework.analytics.ga.DefaultMapFunction;
import io.stanwood.framework.analytics.ga.GoogleAnalyticsTracker;
import io.stanwood.framework.analytics.mixpanel.MixpanelTracker;

public class AppTracker extends BaseAnalyticsTracker {
    private static AppTracker instance;
    private final TrackerContainer adTrackerContainer;

    private AppTracker(TrackerContainer container, TrackerContainer adTrackerContainer) {
        super(container);
        this.adTrackerContainer = adTrackerContainer;
    }

    public static synchronized void init(Application application) {
        if (instance == null) {
            Tracker firebaseTracker = FirebaseTracker.builder(application)
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
                    .mapFunction(new DefaultMapFunction() {
                        @Nullable
                        @Override
                        public Collection<String> mapCustomDimensions(TrackerParams params) {
                            if (!TextUtils.isEmpty(params.getContentType())) {
                                return Collections.singletonList(params.getContentType());
                            }
                            return null;
                        }
                    })
                    .build();
            TrackerContainer defaultContainer = TrackerContainer.builder().addTracker(firebaseTracker, adjustTracker, mixpanelTracker, gaTracker).build();
            Tracker sampledGaTracker = GoogleAnalyticsTracker.builder(application, "KEY")
                    .sampleRate(10)
                    .setExceptionTrackingEnabled(false)
                    .setDebug(BuildConfig.DEBUG)
                    .build();
            TrackerContainer sampledContainer = TrackerContainer.builder().addTracker(sampledGaTracker).build();
            instance = new AppTracker(defaultContainer, sampledContainer);
        }
    }

    public static AppTracker instance() {
        if (instance == null) {
            throw new IllegalArgumentException("Call init() first!");
        }
        return instance;
    }

    public void trackAdLoaded(String adId) {
        adTrackerContainer.trackEvent(TrackerParams.builder("ad").setName("loaded").setId(adId).build());
    }

    public void trackShowDetails(String id, String name) {
        trackerContainer.trackKeys(TrackerKeys.builder().addCustomProperty("id", id).addCustomProperty("name", name).build());
        trackScreenView("details");
    }
}