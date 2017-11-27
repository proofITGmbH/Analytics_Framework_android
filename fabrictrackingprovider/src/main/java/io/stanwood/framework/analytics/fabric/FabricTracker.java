package io.stanwood.framework.analytics.fabric;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;
import io.stanwood.framework.analytics.Tracker;
import io.stanwood.framework.analytics.TrackerParams;

public class FabricTracker extends Tracker {
    private FabricTracker(Builder builder) {
        super(builder);
    }

    @RequiresPermission(
            allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"}
    )
    public static Builder builder(Application context) {
        return new Builder(context);
    }

    @Override
    protected void init(Application context) {
        Fabric.with(context, new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().build())
                .build());
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        Crashlytics.log(0, params.getEventName(), String.format("[%s] [%s]", params.getItemId(), params.getName()));
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        Crashlytics.logException(throwable);
    }

    public static class Builder extends Tracker.Builder {
        Builder(Application context) {
            super(context);
        }

        public Tracker build() {
            return new FabricTracker(this);
        }

    }
}
