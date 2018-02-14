package io.stanwood.framework.analytics.generic;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackerContainer {
    private static final Tracker[] TRACKER_EMPTY = new Tracker[0];
    private volatile Tracker[] trackersArray = TRACKER_EMPTY;
    private int logLevel = 0;
    private Context context;

    private TrackerContainer(Builder builder) {
        this.trackersArray = builder.trackers.toArray(new Tracker[builder.trackers.size()]);
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setLogLevel(@NonNull Context context, int logLevel) {
        this.logLevel = logLevel;
        this.context = context.getApplicationContext();
    }

    public void trackEvent(@NonNull TrackerParams params) {
        Tracker[] trackers = trackersArray;
        for (int i = 0, count = trackers.length; i < count; i++) {
            trackers[i].trackEvent(params);
        }
        if (logLevel > 0) {
            sendLogBroadcast(params);
        }
    }

    public void trackException(@NonNull Throwable throwable) {
        Tracker[] trackers = trackersArray;
        for (int i = 0, count = trackers.length; i < count; i++) {
            trackers[i].trackException(throwable);
        }
    }

    public void trackKeys(@NonNull TrackerKeys keys) {
        Tracker[] trackers = trackersArray;
        for (int i = 0, count = trackers.length; i < count; i++) {
            trackers[i].trackCustomKeys(keys);
        }
        if (logLevel > 0) {
            sendLogBroadcast(keys);
        }
    }

    private void sendLogBroadcast(TrackerKeys keys) {
        JSONObject object = new JSONObject();
        try {
            object.put("time", System.currentTimeMillis());
            object.put("eventname", "TRACK_KEYS");
            if (keys.getCustomKeys() != null) {
                object.put("name", keys.getCustomKeys().toString());
            }
            Intent intent = new Intent("io.stanwood.action.log.tracker");
            intent.putExtra("data", object.toString());
            intent.putExtra("appid", context.getPackageName());
            context.sendBroadcast(intent);

        } catch (JSONException e) {
            // noop
        }
    }

    private void sendLogBroadcast(TrackerParams params) {
        JSONObject object = new JSONObject();
        try {
            object.put("time", System.currentTimeMillis());
            object.put("eventname", params.getEventName());
            if (!TextUtils.isEmpty(params.getItemId())) {
                object.put("itemid", params.getItemId());
            }
            if (!TextUtils.isEmpty(params.getName())) {
                object.put("name", params.getName());
            }
            Intent intent = new Intent("io.stanwood.action.log.tracker");
            intent.putExtra("data", object.toString());
            intent.putExtra("appid", context.getPackageName());
            context.sendBroadcast(intent);

        } catch (JSONException e) {
            // noop
        }
    }

    public static class Builder {
        private List<Tracker> trackers = new ArrayList<>();

        public Builder addTracker(@NonNull Tracker tracker) {
            this.trackers.add(tracker);
            return this;
        }

        public Builder addTracker(@NonNull Tracker... trackers) {
            for (Tracker tracker : trackers) {
                if (!tracker.isDebug) {
                    tracker.init();
                }
            }
            Collections.addAll(this.trackers, trackers);
            return this;
        }

        public TrackerContainer build() {
            return new TrackerContainer(this);
        }
    }

}
