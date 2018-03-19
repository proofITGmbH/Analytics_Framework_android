package io.stanwood.framework.analytics.generic;


import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;

public interface AnalyticsTracker {
    void trackEvent(TrackerParams params);

    void trackKeys(TrackerKeys keys);
}
