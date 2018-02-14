package io.stanwood.framework.analytics.testfairy;


import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;

public interface MapFunction {
    @Nullable
    String map(TrackerParams params);

    @Nullable
    TrackerKeys mapKeys(TrackerKeys params);

}
