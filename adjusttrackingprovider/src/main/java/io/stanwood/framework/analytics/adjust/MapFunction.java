package io.stanwood.framework.analytics.adjust;


import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.TrackerParams;

public interface MapFunction {
    @Nullable
    String mapContentToken(TrackerParams params);
}
