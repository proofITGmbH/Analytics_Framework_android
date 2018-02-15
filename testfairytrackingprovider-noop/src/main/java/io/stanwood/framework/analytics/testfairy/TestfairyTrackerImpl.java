package io.stanwood.framework.analytics.testfairy;

import android.app.Application;
import android.support.annotation.NonNull;

import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;

public class TestfairyTrackerImpl extends TestfairyTracker {
    private final MapFunction mapFunc;

    protected TestfairyTrackerImpl(Builder builder) {
        super(builder);
        if (builder.mapFunc == null) {
            mapFunc = new DefaultMapFunction();
        } else {
            mapFunc = builder.mapFunc;
        }
    }

    public static Builder builder(Application context, String appKey) {
        return new Builder(context);
    }

    @Override
    public void ensureInited() {
        // no-op
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        // no-op
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        // no-op
    }

    @Override
    public void track(@NonNull TrackerKeys keys) {
        // no-op
    }

    public static class Builder extends TestfairyTracker.Builder<Builder> {

        private MapFunction mapFunc = null;

        Builder(Application context) {
            super(context);
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
