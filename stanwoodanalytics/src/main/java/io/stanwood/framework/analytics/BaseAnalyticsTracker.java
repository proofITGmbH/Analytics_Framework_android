package io.stanwood.framework.analytics;


import android.support.annotation.NonNull;

public class BaseAnalyticsTracker {
    protected final TrackerContainer trackerContainer;

    protected BaseAnalyticsTracker(TrackerContainer container) {
        this.trackerContainer = container;
    }

    public void trackScreenView(@NonNull String screenName) {
        trackerContainer.trackEvent(TrackerParams.builder(TrackingEvent.VIEW_ITEM).setName(screenName).build());
    }

    public void trackException(Throwable throwable) {
        trackerContainer.trackException(throwable);
    }

    public void trackUser(String id, String email) {
        trackerContainer.trackKeys(TrackerKeys.builder().addCustomProperty("id", id).addCustomProperty("email", email).build());
    }
}
