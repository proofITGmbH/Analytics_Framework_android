package io.stanwood.framework.analytics.ga;


import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;

import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerParams;

public class GoogleAnalyticsTracker extends Tracker {
    private final String appKey;
    private final short sampleRate;


    private GoogleAnalyticsTracker(Builder builder) {
        super(builder);
        this.appKey = builder.appKey;
        this.sampleRate = builder.sampleRate;
    }

    @RequiresPermission(
            allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"}
    )
    public static Builder builder(Application context, String appKey) {
        return new Builder(context, appKey);
    }

    private com.google.android.gms.analytics.Tracker tracker;

    @SuppressLint("MissingPermission")
    @Override
    protected void init() {
        tracker = GoogleAnalytics.getInstance(context).newTracker(appKey);
        tracker.enableExceptionReporting(exceptionTrackingEnabled);
        tracker.setSampleRate(sampleRate);
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        tracker.send(new HitBuilders.EventBuilder().setCategory(params.getEventName()).setAction(params.getName()).setLabel(params.getItemId()).build());
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(new StandardExceptionParser(context, null)
                        .getDescription(Thread.currentThread().getName(), throwable))
                .setFatal(false)
                .build());
    }

    public static class Builder extends Tracker.Builder {
        private short sampleRate = 100;
        private String appKey;

        Builder(Application context, String appKey) {
            super(context);
            this.appKey = appKey;
        }

        public Builder sampleRate(short sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }

        public Tracker build() {
            return new GoogleAnalyticsTracker(this);
        }
    }
}
