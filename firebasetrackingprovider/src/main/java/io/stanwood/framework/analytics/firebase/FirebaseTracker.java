package io.stanwood.framework.analytics.firebase;


import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Map;

import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingKey;

public class FirebaseTracker extends Tracker {
    public static final String TRACKER_NAME = "firebase";
    private final MapFunction mapFunc;
    private FirebaseAnalytics firebaseAnalytics;

    protected FirebaseTracker(Builder builder) {
        super(builder);
        if (builder.mapFunc == null) {
            mapFunc = new DefaultMapFunction();
        } else {
            mapFunc = builder.mapFunc;
        }
    }

    @RequiresPermission(
            allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"}
    )
    public static Builder builder(Application context) {
        return new Builder(context);
    }


    @Override
    @SuppressLint("MissingPermission")
    protected void enable(boolean enabled) {
        if (firebaseAnalytics == null) {
            this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
        firebaseAnalytics.setAnalyticsCollectionEnabled(enabled);
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        Bundle mapped = mapFunc.map(params);
        if (mapped != null) {
            firebaseAnalytics.logEvent(params.getEventName(), mapFunc.map(params));
        }
        Map<String, Object> mappedKeys = mapFunc.mapKeys(params);
        if (mappedKeys != null) {
            for (Map.Entry<String, Object> entry : mappedKeys.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(TrackingKey.USER_ID)) {
                    firebaseAnalytics.setUserId(entry.getValue().toString());
                } else {
                    firebaseAnalytics.setUserProperty(entry.getKey(), entry.getValue().toString());
                }
            }
        }
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        FirebaseCrash.report(throwable);
    }

    @Override
    public String getTrackerName() {
        return TRACKER_NAME;
    }

    public static class Builder extends Tracker.Builder<Builder> {
        private MapFunction mapFunc = null;

        Builder(Application context) {
            super(context);
            this.exceptionTrackingEnabled = true;
        }

        @Override
        public FirebaseTracker build() {
            return new FirebaseTracker(this);
        }

        public Builder mapFunction(MapFunction func) {
            this.mapFunc = func;
            return this;
        }


        /**
         * Enables exception tracking: sends handled exceptions to firebase
         *
         * @param enable enables exception tracking , default true
         * @return the builder
         */
        public Builder setExceptionTrackingEnabled(boolean enable) {
            this.exceptionTrackingEnabled = enable;
            return this;
        }
    }

}