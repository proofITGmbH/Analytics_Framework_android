package io.stanwood.framework.analytics.ga;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

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
    public Collection<String> mapCustomDimensions(TrackerParams params) {
        return params.getCustomPropertys() != null ? params.getCustomPropertys().values() : null;
    }
}