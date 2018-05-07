package io.stanwood.framework.analytics.debug;


import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerParams;

public class DebugViewTracker extends Tracker {
    public static final String TRACKER_NAME = "debugvieww";

    protected DebugViewTracker(Builder builder) {
        super(builder);
    }

    public static Builder builder(Application context) {
        return new Builder(context);
    }

    @Override
    public String getTrackerName() {
        return TRACKER_NAME;
    }

    @Override
    protected void enable(boolean enabled) {
        //noop
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        JSONObject object = new JSONObject();
        try {
            object.put("time", System.currentTimeMillis());
            object.put("eventname", params.getEventName());
            if (params.getCustomPropertys() != null) {
                object.put("itemid", params.getCustomPropertys());
            } else if (!TextUtils.isEmpty(params.getItemId())) {
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

    @Override
    public void track(@NonNull Throwable throwable) {

    }

    public static class Builder extends Tracker.Builder<Builder> {

        Builder(Application context) {
            super(context);
        }

        @Override
        public DebugViewTracker build() {
            return new DebugViewTracker(this);
        }

    }

}