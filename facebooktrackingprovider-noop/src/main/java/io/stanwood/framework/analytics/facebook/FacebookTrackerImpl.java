package io.stanwood.framework.analytics.facebook;


import android.app.Application;

public class FacebookTrackerImpl extends FacebookTracker {

    protected FacebookTrackerImpl(FacebookTracker.Builder builder) {
        super(builder);
    }

    public static Builder builder(Application context) {
        return new Builder(context);
    }

    public static class Builder extends FacebookTracker.Builder {
        Builder(Application context) {
            super(context, null);
        }
        public FacebookTrackerImpl build() {
            return new FacebookTrackerImpl(this);
        }
    }
}
