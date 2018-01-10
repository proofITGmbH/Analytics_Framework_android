package io.stanwood.framework.analytics;


import android.support.annotation.NonNull;

public class BaseAnalyticsTracker {

    private static BaseAnalyticsTracker defaultInstance;
    protected TrackerContainer trackerContainer;

    protected BaseAnalyticsTracker(TrackerContainer container) {
        this.trackerContainer = container;
    }

    public static void init(TrackerContainer trackerContainer) {
        if (defaultInstance == null) {
            defaultInstance = new BaseAnalyticsTracker(trackerContainer);
        }
    }

    public static BaseAnalyticsTracker defaultTracker() {
        if (defaultInstance == null) {
            throw new IllegalArgumentException("Call init(TrackerContainer) first!");
        }
        return defaultInstance;
    }

    public void trackScreenView(@NonNull String screenName) {
        trackerContainer.event(TrackerParams.builder(TrackingEvent.VIEW_ITEM).setName(screenName).build());
    }

    public void trackException(Throwable throwable) {
        trackerContainer.exception(throwable);
    }

}
