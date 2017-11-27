package io.stanwood.framework.analytics.bugfender;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.bugfender.sdk.Bugfender;

import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerParams;

public class BugfenderTracker extends Tracker {
    private final String appKey;
    private final boolean enableUiLogging;
    private final boolean enableLogcatLogging;

    private BugfenderTracker(Builder builder) {
        super(builder);
        this.appKey = builder.appKey;
        this.enableUiLogging = builder.enableUiLogging;
        this.enableLogcatLogging = builder.enableLogcatLogging;
    }

    @RequiresPermission(
            allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"}
    )
    public static Builder builder(Application context, String appKey) {
        return new Builder(context, appKey);
    }

    @Override
    protected void init(Application context) {
        Bugfender.init(context, appKey, BuildConfig.DEBUG);
        if (enableLogcatLogging) {
            Bugfender.enableLogcatLogging();
        }
        if (enableUiLogging) {
            Bugfender.enableUIEventLogging(context);
        }
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        Bugfender.d(params.getEventName(), String.format("[%s] [%s]", params.getItemId(), params.getName()));
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        Bugfender.sendIssue("Exception", throwable.getMessage());
    }

    public static class Builder extends Tracker.Builder {
        private boolean enableUiLogging;
        private boolean enableLogcatLogging;
        private String appKey;
        Builder(Application context, String appKey) {
            super(context);
            this.appKey = appKey;
        }

        public Builder enableUiLogging(boolean enable) {
            this.enableUiLogging = enable;
            return this;
        }

        public Builder enableLogcatLogging(boolean enable) {
            this.enableLogcatLogging = enable;
            return this;
        }

        public Tracker build() {
            return new BugfenderTracker(this);
        }

    }
}
