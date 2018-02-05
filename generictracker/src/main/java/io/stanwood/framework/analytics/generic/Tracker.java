package io.stanwood.framework.analytics.generic;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class Tracker {

    public boolean isDebug;
    protected Application context;
    protected boolean exceptionTrackingEnabled;
    protected int logLevel;

    protected Tracker(Builder builder) {
        this.context = builder.context;
        this.exceptionTrackingEnabled = builder.exceptionTrackingEnabled;
        this.isDebug = builder.isDebug;
        this.logLevel = builder.logLevel;
    }

    public abstract void init();

    void trackEvent(@NonNull TrackerParams params) {
        if (isDebug) {
            if (logLevel > 0) {
                debug(params);
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
                debug(throwable);
            }
        } else {
            track(throwable);
        }
    }

    void trackCustomKeys(@NonNull TrackerKeys keys) {
        if (isDebug) {
            if (logLevel > 0) {
                debug(keys);
            }
        } else {
            track(keys);
        }
    }

    /**
     * Tracks a full-fledged event.
     * @param params the {@link TrackerParams}
     */
    public abstract void track(@NonNull TrackerParams params);

    /**
     * Tracks an exception.
     * @param throwable the exception
     */
    public abstract void track(@NonNull Throwable throwable);

    /**
     * Tracks custom properties.
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
        private boolean isDebug = false;
        private int logLevel = 0;
        private boolean exceptionTrackingEnabled = false;
        private Application context;

        protected Builder(Application context) {
            this.context = context;
        }

        /**
         * Disables tracking: no calls to the tracking backend will be made.
         * @param enable enables sandbox mode and thus disables tracking
         * @return the builder
         */
        public T isSandbox(boolean enable) {
            this.isDebug = enable;
            return (T) this;
        }

        /**
         * Enables exception tracking: if not set no exceptions will be tracked.
         * @param enable enables exception tracking
         * @return the builder
         */
        public T setExceptionTrackingEnabled(boolean enable) {
            this.exceptionTrackingEnabled = enable;
            return (T) this;
        }

        /**
         * Sets the log level - usually the Android log levels are used here
         * @param level usually one of the Android log levels as found in {@link android.util.Log}
         * @return the builder
         */
        public T setLogLevel(int level) {
            this.logLevel = level;
            return (T) this;
        }

        /**
         * Constructs the tracker
         * @return the tracker
         */
        abstract public Tracker build();
    }
}
