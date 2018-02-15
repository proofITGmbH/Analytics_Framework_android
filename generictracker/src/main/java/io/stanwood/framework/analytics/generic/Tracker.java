package io.stanwood.framework.analytics.generic;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class Tracker {
    protected Application context;
    protected boolean exceptionTrackingEnabled;
    protected int logLevel;
    private boolean enabled;

    protected Tracker(Builder builder) {
        this.context = builder.context;
        this.exceptionTrackingEnabled = builder.exceptionTrackingEnabled;
        this.enabled = builder.isEnabled;
        this.logLevel = builder.logLevel;
    }

    public abstract void ensureInited();

    public boolean isEnabled() {
        return enabled;
    }

    public void setDisabled() {
        enabled = false;
    }

    public void setEnabled() {
        enabled = true;
    }

    void trackEvent(@NonNull TrackerParams params) {
        if (logLevel > 0) {
            debug(params);
        }
        if (enabled) {
            ensureInited();
            track(params);
        }
    }

    void trackException(@NonNull Throwable throwable) {
        if (!exceptionTrackingEnabled) {
            return;
        }
        if (logLevel > 0) {
            debug(throwable);
        }
        if (enabled) {
            ensureInited();
            track(throwable);
        }
    }

    void trackCustomKeys(@NonNull TrackerKeys keys) {
        if (logLevel > 0) {
            debug(keys);
        }
        if (enabled) {
            ensureInited();
            track(keys);
        }
    }

    /**
     * Tracks a full-fledged event.
     *
     * @param params the {@link TrackerParams}
     */
    public abstract void track(@NonNull TrackerParams params);

    /**
     * Tracks an exception.
     *
     * @param throwable the exception
     */
    public abstract void track(@NonNull Throwable throwable);

    /**
     * Tracks custom properties.
     *
     * @param keys the {@link TrackerKeys}
     */
    public abstract void track(@NonNull TrackerKeys keys);

    public void debug(@Nullable TrackerParams params) {
    }

    public void debug(@Nullable Throwable throwable) {
    }

    public void debug(@Nullable TrackerKeys keys) {
    }

    public abstract static class Builder<T extends Builder<T>> {
        private boolean isEnabled = false;
        private int logLevel = 0;
        private boolean exceptionTrackingEnabled = false;
        private Application context;

        protected Builder(Application context) {
            this.context = context;
        }

        /**
         * Disables tracking: no calls to the tracking backend will be made.
         *
         * @param enable enables sandbox mode and thus disables tracking
         * @return the builder
         */
        public T setEnabled(boolean enable) {
            this.isEnabled = enable;
            return (T) this;
        }

        /**
         * Enables exception tracking: if not set no exceptions will be tracked.
         *
         * @param enable enables exception tracking
         * @return the builder
         */
        public T setExceptionTrackingEnabled(boolean enable) {
            this.exceptionTrackingEnabled = enable;
            return (T) this;
        }

        /**
         * Sets the log level - usually the Android log levels are used here
         *
         * @param level usually one of the Android log levels as found in {@link android.util.Log}
         * @return the builder
         */
        public T setLogLevel(int level) {
            this.logLevel = level;
            return (T) this;
        }

        /**
         * Constructs the tracker
         *
         * @return the tracker
         */
        abstract public Tracker build();
    }
}
