package io.stanwood.framework.analytics.adjust;

import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;

public class DefaultMapFunction implements MapFunction {

    @Override
    public String mapContentToken(TrackerParams params) {
        return null;
    }

    @Nullable
    @Override
    public TrackerKeys mapKeys(TrackerKeys keys) {
        if (keys.getTrackKeysEventId().equalsIgnoreCase(TrackingEvent.IDENTIFY_USER)) {
            return keys;
        }
        return null;
    }
}