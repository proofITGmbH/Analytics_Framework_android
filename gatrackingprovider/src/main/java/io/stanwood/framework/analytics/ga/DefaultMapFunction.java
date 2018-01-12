package io.stanwood.framework.analytics.ga;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;

public class DefaultMapFunction implements MapFunction {
    @Nullable
    @Override
    public String mapCategory(@NonNull TrackerParams params) {
        return params.getEventName();
    }

    @Nullable
    @Override
    public String mapAction(@NonNull TrackerParams params) {
        return params.getName();
    }

    @Nullable
    @Override
    public String mapLabel(@NonNull TrackerParams params) {
        return params.getItemId();
    }

    @Nullable
    @Override
    public String mapScreenName(@NonNull TrackerParams params) {
        if (params.getEventName().equalsIgnoreCase(TrackingEvent.VIEW_ITEM)) {
            return params.getName();
        }
        return null;
    }
}