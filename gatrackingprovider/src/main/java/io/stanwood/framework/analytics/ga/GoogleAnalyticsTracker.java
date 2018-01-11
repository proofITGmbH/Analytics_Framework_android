package io.stanwood.framework.analytics.ga;


import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;

import java.util.Collection;

import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerParams;

public class GoogleAnalyticsTracker extends Tracker {
    private final String appKey;
    private final short sampleRate;
    private final boolean activityTracking;
    private final boolean adIdCollection;
    private final MapFunction mapFunc;
    private com.google.android.gms.analytics.Tracker tracker;

    private GoogleAnalyticsTracker(Builder builder) {
        super(builder);
        this.appKey = builder.appKey;
        this.sampleRate = builder.sampleRate;
        this.activityTracking = builder.activityTracking;
        this.adIdCollection = builder.adIdCollection;
        if (builder.mapFunc == null) {
            mapFunc = new DefaultMapFunction();
        } else {
            mapFunc = builder.mapFunc;
        }
    }

    @RequiresPermission(
            allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"}
    )
    public static Builder builder(Application context, String appKey) {
        return new Builder(context, appKey);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void init() {
        tracker = GoogleAnalytics.getInstance(context).newTracker(appKey);
        tracker.enableExceptionReporting(exceptionTrackingEnabled);
        tracker.setSampleRate(sampleRate);
        tracker.enableAutoActivityTracking(activityTracking);
        tracker.enableAdvertisingIdCollection(adIdCollection);
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        String category = mapFunc.mapCategory(params);
        if (!TextUtils.isEmpty(category)) {
            HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder().setCategory(category);
            String action = mapFunc.mapAction(params);
            if (!TextUtils.isEmpty(action)) {
                builder.setAction(action);
                String label = mapFunc.mapLabel(params);
                if (!TextUtils.isEmpty(label)) {
                    builder.setLabel(label);
                }
            }
            Collection<String> values = mapFunc.mapCustomDimensions(params);
            if (values != null) {
                int i = 0;
                for (String entry : values) {
                    builder.setCustomDimension(i++, entry);
                }
            }
            tracker.send(builder.build());
        }
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(new StandardExceptionParser(context, null)
                        .getDescription(Thread.currentThread().getName(), throwable))
                .setFatal(false)
                .build());
    }


    public static class Builder extends Tracker.Builder<Builder> {
        private short sampleRate = 100;
        private String appKey;
        private boolean activityTracking = false;
        private boolean adIdCollection = false;
        private MapFunction mapFunc = null;

        Builder(Application context, String appKey) {
            super(context);
            this.appKey = appKey;
        }

        private Builder autoActivityTracking(boolean enabled) {
            this.activityTracking = enabled;
            return this;
        }

        private Builder adIdCollection(boolean enabled) {
            this.adIdCollection = enabled;
            return this;
        }

        public Builder sampleRate(short sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }

        public Builder mapFunction(MapFunction func) {
            this.mapFunc = func;
            return this;
        }

        public Tracker build() {
            return new GoogleAnalyticsTracker(this);
        }
    }
}
