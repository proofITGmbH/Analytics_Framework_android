package io.stanwood.framework.analytics.fabric;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import java.util.Map;

import io.fabric.sdk.android.Fabric;
import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingKey;

public class FabricTracker extends Tracker {
    protected FabricTracker(Builder builder) {
        super(builder);
    }

    @RequiresPermission(
            allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"}
    )
    public static Builder builder(Application context) {
        return new Builder(context);
    }

    @Override
    public void init() {
        Fabric.with(context, new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().build())
                .build());
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        Crashlytics.log(0, params.getEventName(), String.format("[%s] [%s]", params.getName(), params.getItemId()));
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        Crashlytics.logException(throwable);
    }

    @Override
    public void track(@NonNull TrackerKeys keys) {
        for (Map.Entry<String, Object> entry : keys.getCustomKeys().entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            if (entry.getKey().equals(TrackingKey.USER_ID)) {
                Crashlytics.setUserIdentifier((String) entry.getValue());
            } else if (entry.getKey().equals(TrackingKey.USER_EMAIL)) {
                Crashlytics.setUserEmail((String) entry.getValue());
            } else if (entry.getValue() instanceof String) {
                Crashlytics.setString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                Crashlytics.setInt(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                Crashlytics.setBool(entry.getKey(), (Boolean) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                Crashlytics.setLong(entry.getKey(), (Long) entry.getValue());
            } else if (entry.getValue() instanceof Float) {
                Crashlytics.setFloat(entry.getKey(), (Float) entry.getValue());
            } else if (entry.getValue() instanceof Double) {
                Crashlytics.setDouble(entry.getKey(), (Double) entry.getValue());
            }
        }
    }

    public static class Builder extends Tracker.Builder<Builder> {
        Builder(Application context) {
            super(context);
        }

        public FabricTracker build() {
            return new FabricTracker(this);
        }

    }
}
