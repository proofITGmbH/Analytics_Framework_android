package io.stanwood.framework.analytics.firebase;


import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerParams;

public class FirebaseTracker extends Tracker {
    private FirebaseAnalytics firebaseAnalytics;

    FirebaseTracker(Tracker.Builder builder) {
        super(builder);
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
        firebaseAnalytics.logEvent(params.getEventName(), createBundle(params));
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        FirebaseCrash.report(throwable);
    }

    private Bundle createBundle(TrackerParams params) {
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(params.getItemId())) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, params.getItemId());
        }
        if (!TextUtils.isEmpty(params.getCategory())) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, params.getCategory());
        }
        if (!TextUtils.isEmpty(params.getContentType())) {
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, params.getContentType());
        }
        if (!TextUtils.isEmpty(params.getName())) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, params.getName());
        }
        return bundle;
    }

    public static class Builder extends Tracker.Builder {

        Builder(Application context) {
            super(context);
        }

        @Override
        public Tracker build() {
            return new FirebaseTracker(this);
        }
    }

}