package io.stanwood.framework.analytics.adjust;


import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;

import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;
import io.stanwood.framework.analytics.generic.TrackingKey;

public class AdjustTracker extends Tracker {
    private final String appKey;
    private final MapFunction mapFunc;

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
    public void init() {
        String environment = isDebug ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(context, appKey, environment);
        Adjust.onCreate(config);
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        String eventToken = mapFunc.mapContentToken(params);
        if (TextUtils.isEmpty(eventToken)) {
            return;
        }
        AdjustEvent event = new AdjustEvent(eventToken);
        if (params.getEventName().equalsIgnoreCase(TrackingEvent.PURCHASE)) {
            event.setRevenue((double) params.getCustomPropertys().get(TrackingKey.PURCHASE_PRICE), "EUR");
        }
        Adjust.trackEvent(event);
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        //noop
    }

    @Override
    public void track(@NonNull TrackerKeys keys) {
        TrackerKeys mapped = mapFunc.mapKeys(keys);
        if (mapped == null) {
            return;
        }
        if (mapped.getCustomKeys().containsKey(TrackingKey.PUSH_TOKEN)) {
            Adjust.setPushToken(mapped.getCustomKeys().get(TrackingKey.PUSH_TOKEN).toString(), context);
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
