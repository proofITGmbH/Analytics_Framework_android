package io.stanwood.framework.analytics;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class Tracker {

    private boolean exceptionTrackingEnabled;
    protected boolean isDebug;
    private int logLevel;
    protected Application context;

    protected Tracker(Builder builder) {
        this.context = builder.context;
        this.exceptionTrackingEnabled = builder.exceptionTrackingEnabled;
        this.isDebug = builder.isDebug;
        this.logLevel = builder.logLevel;
        if (!isDebug) {
            init();
        }
    }

    protected abstract void init();


    void trackEvent(@NonNull TrackerParams params) {
        if (!shouldTrack(params)) {
            return;
        }
        if (isDebug) {
            if (logLevel > 0) {
                debug(params, null);
            }
        } else {
            track(params);
        }
    }


    void trackException(@NonNull Throwable throwable) {
        if (!exceptionTrackingEnabled) {
            return;
        }
        if (isDebug) {
            if (logLevel > 0) {
                debug(null, throwable);
            }
        } else {
            track(throwable);
        }
    }

    protected boolean shouldTrack(@NonNull TrackerParams params) {
        return true;
    }

    public abstract void track(@NonNull TrackerParams params);

    public abstract void track(@NonNull Throwable throwable);

    public void debug(@Nullable TrackerParams params, @Nullable Throwable throwable) {
    }

    public abstract static class Builder {
        private boolean isDebug = BuildConfig.DEBUG;
        private int logLevel = 0;
        private boolean exceptionTrackingEnabled = false;
        private Application context;

        protected Builder(Application context) {
            this.context = context;
        }

        public Builder setDebug(boolean enable) {
            this.isDebug = enable;
            return this;
        }

        public Builder setExceptionTrackingEnabled(boolean enabled) {
            this.exceptionTrackingEnabled = enabled;
            return this;
        }

        public Builder setLogLevel(int level) {
            this.logLevel = level;
            return this;
        }

        abstract public Tracker build();

    }
}
