package io.stanwood.framework.analytics.generic;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TrackerContainer {
    private static final Tracker[] TRACKER_EMPTY = new Tracker[0];
    private final TrackerSettingsService settingsService;
    private volatile Tracker[] trackersArray = TRACKER_EMPTY;

    private TrackerContainer(Builder builder) {
        this.trackersArray = builder.trackers.toArray(new Tracker[builder.trackers.size()]);
        this.settingsService = new TrackerSettingsService(builder.context);
        initContainer();
    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    private void initContainer() {
        Tracker[] trackers = trackersArray;
        for (int i = 0, count = trackers.length; i < count; i++) {
            Tracker tracker = trackers[i];
            tracker.setEnabled(settingsService.isTrackerEnabled(tracker.getTrackerName()));
        }
    }

    /***
     * Set enable state of given tracker names
     * @param enable State to set tracker to
     * @param trackerNames List of tracker names or null to apply to all trackers
     */
    public void enableTrackers(boolean enable, @Nullable String... trackerNames) {
        Tracker[] trackers = trackersArray;
        List<String> names = null;
        if (trackerNames != null) {
            names = Arrays.asList(trackerNames);
        }
        for (int i = 0, count = trackers.length; i < count; i++) {
            Tracker tracker = trackers[i];
            if (names == null || names.contains(tracker.getTrackerName())) {
                tracker.setEnabled(enable);
                settingsService.storeTrackerState(tracker.getTrackerName(), enable);
            }
        }
    }

    public boolean isTrackerEnabled(String trackerName) {
        Tracker[] trackers = trackersArray;
        for (int i = 0, count = trackers.length; i < count; i++) {
            Tracker tracker = trackers[i];
            if (tracker.getTrackerName().equals(trackerName)) {
                return tracker.isEnabled();
            }
        }
        return false;
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

    public static class Builder {
        private List<Tracker> trackers = new ArrayList<>();
        private Context context;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder addTracker(@NonNull Tracker tracker) {
            this.trackers.add(tracker);
            return this;
        }

        public Builder addTracker(@NonNull Tracker... trackers) {
            Collections.addAll(this.trackers, trackers);
            return this;
        }

        public TrackerContainer build() {
            return new TrackerContainer(this);
        }
    }

}
