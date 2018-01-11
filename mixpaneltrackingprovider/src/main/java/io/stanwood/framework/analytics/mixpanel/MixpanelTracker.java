package io.stanwood.framework.analytics.mixpanel;


import android.app.Application;
import android.support.annotation.NonNull;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.Map;

import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerKeys;
import io.stanwood.framework.analytics.TrackerParams;

public class MixpanelTracker extends Tracker {
    private final String appKey;
    private final MapFunction mapFunc;
    private MixpanelAPI mixpanelAPI;

    protected MixpanelTracker(Builder builder) {
        super(builder);
        this.appKey = builder.appKey;
        if (builder.mapFunc == null) {
            mapFunc = new DefaultMapFunction();
        } else {
            mapFunc = builder.mapFunc;
        }
    }

    public static Builder builder(Application context, String appKey) {
        return new Builder(context, appKey);
    }

    @Override
    protected void init() {
        mixpanelAPI = MixpanelAPI.getInstance(context, appKey);
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        Map<String, String> mapped = mapFunc.map(params);
        if (mapped != null && !mapped.isEmpty()) {
            JSONObject props = new JSONObject(mapped);
            mixpanelAPI.track(params.getEventName(), props);
        }
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        //noop
    }

    @Override
    public void track(@NonNull TrackerKeys keys) {
        MixpanelAPI.People p = mixpanelAPI.getPeople();
        for (Map.Entry<String, Object> entry : keys.getCustomKeys().entrySet()) {
            String key = entry.getKey();
            if (key.equalsIgnoreCase("email")) {
                p.set("$email", entry.getValue());
            } else if (key.equalsIgnoreCase("id")) {
                p.identify((String) entry.getValue());
            } else {
                p.set(key, entry.getValue());
            }
        }
    }

    public static class Builder extends Tracker.Builder<Builder> {
        private String appKey;
        private MapFunction mapFunc = null;

        Builder(Application context, String appKey) {
            super(context);
            this.appKey = appKey;
        }

        public MixpanelTracker build() {
            return new MixpanelTracker(this);
        }

        public Builder mapFunction(MapFunction func) {
            this.mapFunc = func;
            return this;
        }

    }
}
