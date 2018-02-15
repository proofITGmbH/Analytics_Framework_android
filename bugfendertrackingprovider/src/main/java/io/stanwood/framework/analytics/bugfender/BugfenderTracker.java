package io.stanwood.framework.analytics.bugfender;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.bugfender.sdk.Bugfender;

import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;

public class BugfenderTracker extends Tracker {
    private final String appKey;
    private final boolean enableUiLogging;
    private final boolean enableLogcatLogging;
    private boolean isInited;

    protected BugfenderTracker(Builder builder) {
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
    public void ensureInited() {
        if (!isInited) {
            isInited = true;
            Bugfender.init(context, appKey, logLevel > 0);
            if (enableLogcatLogging) {
                Bugfender.enableLogcatLogging();
            }
            if (enableUiLogging) {
                Bugfender.enableUIEventLogging(context);
            }
        }
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        Bugfender.d(params.getEventName(), String.format("[%s] [%s]", params.getName(), params.getItemId()));
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        Bugfender.sendIssue("Exception", throwable.getMessage());
    }

    @Override
    public void track(@NonNull TrackerKeys keys) {
        //noop
    }

    public static class Builder extends Tracker.Builder<Builder> {
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

        public BugfenderTracker build() {
            return new BugfenderTracker(this);
        }

    }
}
