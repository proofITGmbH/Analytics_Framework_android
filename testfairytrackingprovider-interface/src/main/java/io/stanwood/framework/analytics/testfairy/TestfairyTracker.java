package io.stanwood.framework.analytics.testfairy;


import io.stanwood.framework.analytics.generic.Tracker;

public abstract class TestfairyTracker extends Tracker {

    protected TestfairyTracker(Tracker.Builder builder) {
        super(builder);
    }
}
