package io.stanwood.framework.analytics.mixpanel;


import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.Map;

import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingKey;

public class MixpanelTracker extends Tracker {
    public static final String TRACKER_NAME = "mixpanel";
    private final String appKey;
    private final MapFunction mapFunc;
    private final String senderId;
    private MixpanelAPI mixpanelAPI;

    protected MixpanelTracker(Builder builder) {
        super(builder);
        this.appKey = builder.appKey;
        this.senderId = builder.senderId;
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
    protected void enable(boolean enabled) {
        // there is no way to disable after mixpanel is once inited
        if (enabled && mixpanelAPI == null) {
            mixpanelAPI = MixpanelAPI.getInstance(context, appKey);
        }
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        Map<String, String> mapped = mapFunc.map(params);
        if (mapped != null && !mapped.isEmpty()) {
            JSONObject props = new JSONObject(mapped);
            mixpanelAPI.track(params.getEventName(), props);
        }
        Map<String, Object> mappedKeys = mapFunc.mapKeys(params);
        if (mappedKeys != null) {
            MixpanelAPI.People p = mixpanelAPI.getPeople();
            for (Map.Entry<String, Object> entry : mappedKeys.entrySet()) {
                String key = entry.getKey();
                if (key.equalsIgnoreCase(TrackingKey.USER_EMAIL)) {
                    p.set("$email", entry.getValue());
                } else if (key.equalsIgnoreCase(TrackingKey.USER_ID)) {
                    p.identify((String) entry.getValue());
                    if (!TextUtils.isEmpty(senderId)) {
                        p.initPushHandling(senderId);
                    }
                } else {
                    p.set(key, entry.getValue());
                }
            }
        }
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        //noop
    }

    @Override
    public String getTrackerName() {
        return TRACKER_NAME;
    }

    public static class Builder extends Tracker.Builder<Builder> {
        private String appKey;
        private MapFunction mapFunc = null;
        private String senderId;

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

        /**
         * Set to enable push handling
         *
         * @param senderId of the Google API Project that registered for Google Cloud Messaging
         *                 You can find your ID by looking at the URL of in your Google API Console
         *                 at https://code.google.com/apis/console/; it is the twelve digit number after
         *                 after "#project:" in the URL address bar on console pages.
         */
        public Builder senderId(String senderId) {
            this.senderId = senderId;
            return this;
        }

    }
}
