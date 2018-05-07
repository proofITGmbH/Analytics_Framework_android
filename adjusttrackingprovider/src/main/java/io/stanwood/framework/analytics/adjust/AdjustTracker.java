package io.stanwood.framework.analytics.adjust;


import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;

import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;
import io.stanwood.framework.analytics.generic.TrackingKey;

public class AdjustTracker extends Tracker {
    public static final String TRACKER_NAME = "adjust";
    private final String appKey;
    private final MapFunction mapFunc;
    private boolean isInited;

    protected AdjustTracker(Builder builder) {
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
    public void track(@NonNull TrackerParams params) {
        String eventToken = mapFunc.mapContentToken(params);
        if (!TextUtils.isEmpty(eventToken)) {
            AdjustEvent event = new AdjustEvent(eventToken);
            if (params.getEventName().equalsIgnoreCase(TrackingEvent.PURCHASE)) {
                event.setRevenue((double) params.getCustomPropertys().get(TrackingKey.PURCHASE_PRICE), "EUR");
            }
            Adjust.trackEvent(event);
        }
        String token = params.getCustomProperty(TrackingKey.PUSH_TOKEN);
        if (!TextUtils.isEmpty(token)) {
            Adjust.setPushToken(token, context);
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

    @Override
    protected void enable(boolean enabled) {
        if (enabled && !isInited) {
            isInited = true;
            String environment = isEnabled() ? AdjustConfig.ENVIRONMENT_PRODUCTION : AdjustConfig.ENVIRONMENT_SANDBOX;
            AdjustConfig config = new AdjustConfig(context, appKey, environment);
            Adjust.onCreate(config);
        } else if (isInited) {
            Adjust.setEnabled(enabled);
        }
    }

    public static class Builder extends Tracker.Builder<Builder> {
        private String appKey;
        private MapFunction mapFunc = null;

        Builder(Application context, String appKey) {
            super(context);
            this.appKey = appKey;
        }

        public AdjustTracker build() {
            return new AdjustTracker(this);
        }

        public Builder mapFunction(MapFunction func) {
            this.mapFunc = func;
            return this;
        }
    }
}
