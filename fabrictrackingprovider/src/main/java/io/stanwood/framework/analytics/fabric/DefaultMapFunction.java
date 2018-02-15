package io.stanwood.framework.analytics.fabric;

import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;

public class DefaultMapFunction implements MapFunction {


    @Nullable
    @Override
    public String map(TrackerParams params) {
        return String.format("[%s] [%s]", params.getName(), params.getItemId());
    }

    @Nullable
    @Override
    public TrackerKeys mapKeys(TrackerKeys params) {
        if (params.getTrackKeysEventId().equalsIgnoreCase(TrackingEvent.IDENTIFY_USER)) {
            return params;
        }
        return null;
    }
}