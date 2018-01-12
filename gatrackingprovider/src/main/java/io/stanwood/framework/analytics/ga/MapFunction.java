package io.stanwood.framework.analytics.ga;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.generic.TrackerParams;

public interface MapFunction {
    @Nullable
    String mapCategory(@NonNull TrackerParams params);

    @Nullable
    String mapAction(@NonNull TrackerParams params);

    @Nullable
    String mapLabel(@NonNull TrackerParams params);

    @Nullable
    String mapScreenName(@NonNull TrackerParams params);
}
