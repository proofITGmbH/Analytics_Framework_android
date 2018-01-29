package io.stanwood.framework.analytics.firebase;


import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;

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
    public void init() {
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
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
        if (keys.getCustomKeys().containsKey("id")) {
            firebaseAnalytics.setUserId(keys.getCustomKeys().get("id").toString());
        }
        if (keys.getCustomKeys().containsKey("email")) {
            firebaseAnalytics.setUserProperty("email", keys.getCustomKeys().get("email").toString());
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