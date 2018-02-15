package io.stanwood.framework.analytics.generic;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackerContainer {
    private static final Tracker[] TRACKER_EMPTY = new Tracker[0];
    private volatile Tracker[] trackersArray = TRACKER_EMPTY;

    private TrackerContainer(Builder builder) {
        this.trackersArray = builder.trackers.toArray(new Tracker[builder.trackers.size()]);
    }

    public static Builder builder() {
        return new Builder();
    }

    public void trackEvent(@NonNull TrackerParams params) {
        Tracker[] trackers = trackersArray;
        for (int i = 0, count = trackers.length; i < count; i++) {
            trackers[i].trackEvent(params);
        }
    }

    public void trackException(@NonNull Throwable throwable) {
        Tracker[] trackers = trackersArray;
        for (int i = 0, count = trackers.length; i < count; i++) {
            trackers[i].trackException(throwable);
        }
    }

    public void trackKeys(@NonNull TrackerKeys keys) {
        Tracker[] trackers = trackersArray;
        for (int i = 0, count = trackers.length; i < count; i++) {
            trackers[i].trackCustomKeys(keys);
        }
    }

    public static class Builder {
        private List<Tracker> trackers = new ArrayList<>();

        public Builder addTracker(@NonNull Tracker tracker) {
            this.trackers.add(tracker);
            return this;
        }

        public Builder addTracker(@NonNull Tracker... trackers) {
            for (Tracker tracker : trackers) {
                if (!tracker.isDebug) {
                    tracker.init();
                }
            }
            Collections.addAll(this.trackers, trackers);
            return this;
        }

        public TrackerContainer build() {
            return new TrackerContainer(this);
        }
    }

}
