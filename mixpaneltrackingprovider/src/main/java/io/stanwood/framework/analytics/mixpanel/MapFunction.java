package io.stanwood.framework.analytics.mixpanel;


import android.support.annotation.Nullable;

import java.util.Map;

import io.stanwood.framework.analytics.TrackerParams;

public interface MapFunction {
    @Nullable
    Map<String, String> map(TrackerParams params);
}
