package io.stanwood.framework.analytics.testfairy;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import com.testfairy.TestFairy;

import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingKey;

/**
 * WHEN ADAPTING THIS CLASS ALWAYS ALSO CHECK THE NO-OP VARIANT!
 */
public class TestfairyTrackerImpl extends TestfairyTracker {
    private final String appKey;
    private final MapFunction mapFunc;
    private boolean isInited;

    protected TestfairyTrackerImpl(Builder builder) {
        super(builder);
        this.appKey = builder.appKey;
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

    @Override
    public void ensureInitialized() {
        if (!isInited) {
            isInited = true;
            TestFairy.begin(context, appKey);
        }
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        String mapped = mapFunc.map(params);
        if (!TextUtils.isEmpty(mapped)) {
            TestFairy.addEvent(mapped);
        }
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        //noop
    }

    @Override
    public void track(@NonNull TrackerKeys keys) {
        TrackerKeys mapped = mapFunc.mapKeys(keys);
        if (mapped == null) {
            return;
        }
        if (keys.getCustomKeys().get(TrackingKey.USER_ID) != null) {
            TestFairy.setUserId(keys.getCustomKeys().get(TrackingKey.USER_ID).toString());
        }
    }

    public static class Builder extends TestfairyTracker.Builder<Builder> {
        private String appKey;

        private MapFunction mapFunc = null;

        Builder(Application context, String appKey) {
            super(context);
            this.appKey = appKey;
        }

        public TestfairyTracker build() {
            return new TestfairyTrackerImpl(this);
        }

        public Builder mapFunction(MapFunction func) {
            this.mapFunc = func;
            return this;
        }
    }
}
