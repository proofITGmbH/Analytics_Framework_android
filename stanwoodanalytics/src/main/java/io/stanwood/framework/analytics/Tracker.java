package io.stanwood.framework.analytics;


import android.app.Application;
import android.support.annotation.NonNull;

public abstract class Tracker {

    private boolean exceptionTrackingEnabled;
    private boolean enabled;
    private Application context;
    private boolean isInited;

    protected Tracker(Builder builder) {
        this.exceptionTrackingEnabled = builder.exceptionTrackingEnabled;
        this.enabled = builder.enabled;
        this.context = builder.context;
    }

    private synchronized void ensureInited() {
        if (!isInited) {
            init(context);
            isInited = true;
        }
    }

    protected abstract void init(Application context);


    void trackEvent(@NonNull TrackerParams params) {
        if (!enabled) {
            return;
        }
        if (!shouldTrack(params)) {
            return;
        }
        ensureInited();
        track(params);
    }

    void trackException(@NonNull Throwable throwable) {
        if (!enabled) {
            return;
        }
        if (exceptionTrackingEnabled) {
            ensureInited();
            track(throwable);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected boolean shouldTrack(@NonNull TrackerParams params) {
        return true;
    }

    public abstract void track(@NonNull TrackerParams params);

    public abstract void track(@NonNull Throwable throwable);

    public abstract static class Builder {
        private boolean enabled = !BuildConfig.DEBUG;
        private boolean exceptionTrackingEnabled = false;
        private Application context;

        protected Builder(Application context) {
            this.context = context;
        }

        public Builder setEnabled(boolean enable) {
            this.enabled = enable;
            return this;
        }

        public Builder setExceptionTrackingEnabled(boolean enabled) {
            this.exceptionTrackingEnabled = enabled;
            return this;
        }

        abstract public Tracker build();

    }
}
