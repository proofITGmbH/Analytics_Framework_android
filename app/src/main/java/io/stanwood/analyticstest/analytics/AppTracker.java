package io.stanwood.analyticstest.analytics;

import io.stanwood.framework.analytics.BaseAnalyticsTracker;
import io.stanwood.framework.analytics.TrackerContainer;
import io.stanwood.framework.analytics.TrackerParams;

public class AppTracker extends BaseAnalyticsTracker {
    private AppTracker(TrackerContainer container) {
        super(container);
    }

    public void specialEvent(String value) {
        trackerContainer.event(TrackerParams.builder("special").setName(value).build());
    }
}