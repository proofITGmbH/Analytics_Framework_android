package io.stanwood.framework.analytics;


import android.content.Context;
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

    /**
     * Set`s the container log level
     *
     * @param context  Context
     * @param logLevel > 0 to enable tracker container logging
     */
    public void setContainerLogLevel(@NonNull Context context, int logLevel) {
        trackerContainer.setLogLevel(context, logLevel);
    }

    /**
     * Tracks a screen view.
     * <br><br>
     * Will become PROTECTED in the future!
     *
     * @param screenName an unique screen name
     */
    public void trackScreenView(@NonNull String screenName) {
        trackerContainer.trackEvent(TrackerParams.builder(TrackingEvent.VIEW_ITEM).setName(screenName).build());
    }

    /**
     * Tracks an exception.
     * <br><br>
     * Will become PROTECTED in the future!
     *
     * @param throwable the exception
     */
    public void trackException(Throwable throwable) {
        trackerContainer.trackException(throwable);
    }

    /**
     * Tracks a user.
     * <br><br>
     * Will become PROTECTED in the future!
     *
     * @param id    the user ID
     * @param email the user's Email address
     */
    public void trackUser(@Nullable String id, @Nullable String email) {
        trackerContainer.trackKeys(TrackerKeys.builder().addCustomProperty(TrackingKey.USER_ID, id).addCustomProperty(TrackingKey.USER_EMAIL, email).build());
    }

    /**
     * Tracks a full-fledged event.
     * <br><br>
     * For simple screen views use {@link #trackScreenView(String)}
     * <br><br>
     * Will become PROTECTED in the future!
     *
     * @param params {@link TrackerParams}
     */
    public void trackEvent(TrackerParams params) {
        trackerContainer.trackEvent(params);
    }

    /**
     * Tracks custom properties.
     * <br><br>
     * Will become PROTECTED in the future!
     *
     * @param keys the {@link TrackerKeys}
     */
    public void trackKeys(TrackerKeys keys) {
        trackerContainer.trackKeys(keys);
    }
}
