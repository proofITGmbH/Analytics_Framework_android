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
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingKey;

public class FirebaseTracker extends Tracker {
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

    @SuppressLint("MissingPermission")
    @Override
    public void ensureInitialized() {
        if (firebaseAnalytics == null) {
            this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        Bundle mapped = mapFunc.map(params);
        if (mapped != null) {
            firebaseAnalytics.logEvent(params.getEventName(), mapFunc.map(params));
        }
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        FirebaseCrash.report(throwable);
    }

    @Override
    public void track(@NonNull TrackerKeys keys) {
        TrackerKeys mapped = mapFunc.mapKeys(keys);
        if (mapped == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : mapped.getCustomKeys().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(TrackingKey.USER_ID)) {
                firebaseAnalytics.setUserId(entry.getValue().toString());
            } else {
                firebaseAnalytics.setUserProperty(entry.getKey(), entry.getValue().toString());
            }
        }
    }


    public static class Builder extends Tracker.Builder<Builder> {
        private MapFunction mapFunc = null;

        Builder(Application context) {
            super(context);
        }

        @Override
        public FirebaseTracker build() {
            return new FirebaseTracker(this);
        }

        public Builder mapFunction(MapFunction func) {
            this.mapFunc = func;
            return this;
        }
    }

}