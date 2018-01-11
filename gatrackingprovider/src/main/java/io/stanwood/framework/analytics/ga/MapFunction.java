package io.stanwood.framework.analytics.ga;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

import io.stanwood.framework.analytics.TrackerParams;

public interface MapFunction {
    @Nullable
    String mapCategory(@NonNull TrackerParams params);

    @Nullable
    String mapAction(@NonNull TrackerParams params);

    @Nullable
    String mapLabel(@NonNull TrackerParams params);

    @Nullable
    Collection<String> mapCustomDimensions(TrackerParams params);
}
