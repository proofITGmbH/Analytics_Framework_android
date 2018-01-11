package io.stanwood.framework.analytics;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.bugfender.BugfenderTracker;
import io.stanwood.framework.analytics.fabric.FabricTracker;
import io.stanwood.framework.analytics.firebase.FirebaseTracker;

public class BaseAnalyticsTracker {
    protected final TrackerContainer trackerContainer;

    protected BaseAnalyticsTracker(@NonNull FabricTracker fabricTracker, @NonNull FirebaseTracker firebaseTracker,
                                   @NonNull BugfenderTracker bugfenderTracker, @Nullable Tracker... optional) {
        TrackerContainer.Builder builder = TrackerContainer.builder().addTracker(fabricTracker, firebaseTracker, bugfenderTracker);
        if (optional != null) {
            builder.addTracker(optional);
        }
        trackerContainer = builder.build();
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

    public void trackParameter(TrackerParams params) {
        trackerContainer.trackEvent(params);
    }

    public void trackKeys(TrackerKeys keys) {
        trackerContainer.trackKeys(keys);
    }
}
