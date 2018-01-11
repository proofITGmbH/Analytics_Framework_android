package io.stanwood.framework.analytics.firebase;


import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerParams;

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
    protected void init() {
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


    public static class Builder extends Tracker.Builder<Builder> {
        private MapFunction mapFunc = null;

        Builder(Application context) {
            super(context);
        }

        @Override
        public Tracker build() {
            return new FirebaseTracker(this);
        }

        public Builder mapFunction(MapFunction func) {
            this.mapFunc = func;
            return this;
        }
    }

}