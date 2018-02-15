package io.stanwood.framework.analytics.adjust;


import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;

public interface MapFunction {
    @Nullable
    String mapContentToken(TrackerParams params);

    @Nullable
    TrackerKeys mapKeys(TrackerKeys keys);
}
