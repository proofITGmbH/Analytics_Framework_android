package io.stanwood.framework.analytics.testfairy;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.testfairy.TestFairy;

import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;

public class TestfairyTracker extends Tracker {
    private final String appKey;

    protected TestfairyTracker(Builder builder) {
        super(builder);
        this.appKey = builder.appKey;
    }

    @RequiresPermission(
            allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"}
    )
    public static Builder builder(Application context, String appKey) {
        return new Builder(context, appKey);
    }

    @Override
    public void init() {
        TestFairy.begin(context, appKey);
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        TestFairy.addEvent(String.format("%s -> [%s] [%s]", params.getEventName(), params.getName(), params.getItemId()));
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        //noop
    }

    @Override
    public void track(@NonNull TrackerKeys keys) {
        if (keys.getCustomKeys().get("id") != null) {
            TestFairy.setUserId(keys.getCustomKeys().get("id").toString());
        }
    }

    public static class Builder extends Tracker.Builder<Builder> {
        private String appKey;

        Builder(Application context, String appKey) {
            super(context);
            this.appKey = appKey;
        }

        public TestfairyTracker build() {
            return new TestfairyTracker(this);
        }

    }
}
