package io.stanwood.framework.analytics.mixpanel;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;

public class DefaultMapFunction implements MapFunction {

    @Nullable
    @Override
    public Map<String, String> map(TrackerParams params) {
        Map<String, String> mapped = null;
        if (!TextUtils.isEmpty(params.getItemId())) {
            mapped = new HashMap<>(2);
            mapped.put("action", params.getName());
        }
        if (!TextUtils.isEmpty(params.getName())) {
            mapped = mapped != null ? mapped : new HashMap<String, String>(1);
            mapped.put("label", params.getItemId());
        }
        return mapped;
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