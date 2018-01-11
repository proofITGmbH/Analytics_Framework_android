package io.stanwood.analyticstest.analytics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerContainer;
import io.stanwood.framework.analytics.TrackerParams;
import io.stanwood.framework.analytics.TrackingEvent;
import io.stanwood.framework.analytics.adjust.AdjustTracker;
import io.stanwood.framework.analytics.bugfender.BugfenderTracker;
import io.stanwood.framework.analytics.fabric.FabricTracker;
import io.stanwood.framework.analytics.firebase.FirebaseTracker;
import io.stanwood.framework.analytics.firebase.MapFunction;
import io.stanwood.framework.analytics.ga.DefaultMapFunction;
import io.stanwood.framework.analytics.ga.GoogleAnalyticsTracker;
import io.stanwood.framework.analytics.mixpanel.MixpanelTracker;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppTracker.init(TrackerContainer.builder()
                .addTracker(FirebaseTracker.builder(getApplication())
                        .setExceptionTrackingEnabled(true)
                        .setDebug(BuildConfig.DEBUG)
                        .build())
                .addTracker(FabricTracker.builder(getApplication())
                        .setExceptionTrackingEnabled(true)
                        .setDebug(BuildConfig.DEBUG)
                        .build())
                .addTracker(BugfenderTracker.builder(getApplication(), "KEY")
                        .enableLogcatLogging(false)
                        .enableUiLogging(false)
                        .setExceptionTrackingEnabled(true)
                        .setDebug(!BuildConfig.DEBUG)
                        .build())
                .addTracker(GoogleAnalyticsTracker.builder(getApplication(), "KEY")
                        .setExceptionTrackingEnabled(true)
                        .setDebug(BuildConfig.DEBUG)
                        .build())
                .addTracker(MixpanelTracker.builder(getApplication(), "KEY")
                        .setDebug(BuildConfig.DEBUG)
                        .build())
                .addTracker(AdjustTracker.builder(getApplication(), "KEY")
                        .setDebug(BuildConfig.DEBUG)
                        .build())
                .build());

    }

    void advancedConfig() {
        Tracker firebaseTracker = FirebaseTracker.builder(getApplication())
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
        Tracker adjustTracker = AdjustTracker.builder(getApplication(), "KEY")
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
        Tracker mixpanelTracker = MixpanelTracker.builder(getApplication(), "KEY")
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
        Tracker gaTracker = GoogleAnalyticsTracker.builder(getApplication(), "KEY")
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
        TrackerContainer container = TrackerContainer.builder().addTracker(firebaseTracker, adjustTracker, mixpanelTracker, gaTracker).build();
        AppTracker.init(container);
    }

}
