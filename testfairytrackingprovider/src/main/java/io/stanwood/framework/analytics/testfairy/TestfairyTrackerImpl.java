package io.stanwood.framework.analytics.testfairy;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import com.testfairy.TestFairy;

import java.util.Map;

import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingKey;

/**
 * WHEN ADAPTING THIS CLASS ALWAYS ALSO CHECK THE NO-OP VARIANT!
 */
public class TestfairyTrackerImpl extends TestfairyTracker {
    private static final String TRACKER_NAME = "testfairy";
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
    public void track(@NonNull TrackerParams params) {
        String mapped = mapFunc.map(params);
        if (!TextUtils.isEmpty(mapped)) {
            TestFairy.addEvent(mapped);
        }
        Map<String, Object> mappedKeys = mapFunc.mapKeys(params);
        if (mappedKeys != null) {
            String userId = mappedKeys.get(TrackingKey.USER_ID).toString();
            if (!TextUtils.isEmpty(userId)) {
                TestFairy.setUserId(userId);
            }
        }
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        //noop
    }


    @Override
    public String getTrackerName() {
        return TRACKER_NAME;
    }

    @Override
    protected void enable(boolean enabled) {
        // there is no way to disable after testfairy is once inited
        if (enabled && !isInited) {
            isInited = true;
            TestFairy.begin(context, appKey);
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
