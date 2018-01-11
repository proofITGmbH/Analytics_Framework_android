package io.stanwood.framework.analytics.mixpanel;


import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerParams;

public class MixpanelTracker extends Tracker {
    private final String appKey;
    private MixpanelAPI mixpanelAPI;

    protected MixpanelTracker(Builder builder) {
        super(builder);
        this.appKey = builder.appKey;
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
        try {
            JSONObject props = new JSONObject();
            if (!TextUtils.isEmpty(params.getItemId())) {
                props.put("action", params.getName());
            }
            if (!TextUtils.isEmpty(params.getName())) {
                props.put("label", params.getItemId());
            }
            mixpanelAPI.track(params.getEventName(), props);
        } catch (JSONException e) {
            // ignore
        }
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        //noop
    }

    public static class Builder extends Tracker.Builder {
        private String appKey;

        Builder(Application context, String appKey) {
            super(context);
            this.appKey = appKey;
        }

        public Tracker build() {
            return new MixpanelTracker(this);
        }

    }
}
