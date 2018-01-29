package io.stanwood.framework.analytics;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.fabric.FabricTracker;
import io.stanwood.framework.analytics.firebase.FirebaseTracker;
import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerContainer;
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;
import io.stanwood.framework.analytics.generic.TrackingKey;
import io.stanwood.framework.analytics.testfairy.TestfairyTracker;

public class BaseAnalyticsTracker {
    private final TrackerContainer trackerContainer;

    protected BaseAnalyticsTracker(@NonNull FabricTracker fabricTracker, @NonNull FirebaseTracker firebaseTracker,
                                   @NonNull TestfairyTracker bugfenderTracker, @Nullable Tracker... optional) {
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
        trackerContainer.trackKeys(TrackerKeys.builder().addCustomProperty(TrackingKey.USER_ID, id).addCustomProperty(TrackingKey.USER_EMAIL, email).build());
    }

    public void trackParameter(TrackerParams params) {
        trackerContainer.trackEvent(params);
    }

    public void trackKeys(TrackerKeys keys) {
        trackerContainer.trackKeys(keys);
    }
}
