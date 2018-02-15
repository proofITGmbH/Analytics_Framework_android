package io.stanwood.framework.analytics.ga;


import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.util.Map;

import io.stanwood.framework.analytics.generic.Tracker;
import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;
import io.stanwood.framework.analytics.generic.TrackingEvent;
import io.stanwood.framework.analytics.generic.TrackingKey;

public class GoogleAnalyticsTracker extends Tracker {
    private final String appKey;
    private final int sampleRate;
    private final boolean activityTracking;
    private final boolean adIdCollection;
    private final MapFunction mapFunc;
    private com.google.android.gms.analytics.Tracker tracker;

    protected GoogleAnalyticsTracker(Builder builder) {
        super(builder);
        this.appKey = builder.appKey;
        this.sampleRate = builder.sampleRate;
        this.activityTracking = builder.activityTracking;
        this.adIdCollection = builder.adIdCollection;
        if (builder.mapFunc == null) {
            mapFunc = new DefaultMapFunction();
        } else {
            mapFunc = builder.mapFunc;
        }
    }

    @RequiresPermission(
            allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"}
    )
    public static Builder builder(Application context, String appKey) {
        return new Builder(context, appKey);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void ensureInitialized() {
        if (tracker == null) {
            tracker = GoogleAnalytics.getInstance(context).newTracker(appKey);
            tracker.enableExceptionReporting(exceptionTrackingEnabled);
            tracker.setSampleRate(sampleRate);
            tracker.enableAutoActivityTracking(activityTracking);
            tracker.enableAdvertisingIdCollection(adIdCollection);
        }
    }

    @Override
    public void track(@NonNull TrackerParams params) {
        TrackerParams mapped = mapFunc.mapParams(params);
        if (mapped != null) {
            if (mapped.getEventName().equalsIgnoreCase(TrackingEvent.VIEW_ITEM)) {
                tracker.setScreenName(params.getName());
                tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } else if (mapped.getEventName().equalsIgnoreCase(TrackingEvent.PURCHASE)) {
                trackPurchase(mapped);
            } else {
                HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder().setCategory(params.getEventName());
                if (!TextUtils.isEmpty(params.getName())) {
                    builder.setAction(params.getName());
                    if (!TextUtils.isEmpty(params.getItemId())) {
                        builder.setLabel(params.getItemId());
                    }
                }
                tracker.send(builder.build());
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        GoogleAnalytics.getInstance(context).setAppOptOut(!enabled);
    }

    private void trackPurchase(TrackerParams params) {
        Product product = new Product()
                .setId(params.getItemId())
                .setName(params.getName())
                .setCategory(params.getCategory())
                .setBrand(params.getCustomPropertys().get(TrackingKey.PURCHASE_BRAND).toString())
                .setPrice((Double) params.getCustomPropertys().get(TrackingKey.PURCHASE_PRICE))
                .setQuantity((Integer) params.getCustomPropertys().get(TrackingKey.PURCHASE_QUANTITY));
        ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                .setTransactionId(params.getCustomPropertys().get(TrackingKey.PURCHASE_ORDERID).toString())
                .setTransactionAffiliation("Google Play Store")
                .setTransactionRevenue((Double) params.getCustomPropertys().get(TrackingKey.PURCHASE_PRICE));
        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                .addProduct(product)
                .setProductAction(productAction);
        tracker.setScreenName("transaction");
        tracker.send(builder.build());
    }

    @Override
    public void track(@NonNull Throwable throwable) {
        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(new StandardExceptionParser(context, null)
                        .getDescription(Thread.currentThread().getName(), throwable))
                .setFatal(false)
                .build());
    }

    @Override
    public void track(@NonNull TrackerKeys keys) {
        Map<Integer, Object> mapped = mapFunc.mapKeys(keys);
        if (mapped == null) {
            return;
        }
        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder();
        for (Map.Entry<Integer, Object> entry : mapped.entrySet()) {
            builder.setCustomDimension(entry.getKey(), (String) entry.getValue());
        }
        tracker.send(builder.build());
    }

    public void setClientId(String id) {
        if (tracker != null) {
            tracker.setClientId(id);
        }
    }


    public static class Builder extends Tracker.Builder<Builder> {
        private int sampleRate = 100;
        private String appKey;
        private boolean activityTracking = false;
        private boolean adIdCollection = false;
        private MapFunction mapFunc = null;

        Builder(Application context, String appKey) {
            super(context);
            this.appKey = appKey;
        }

        public Builder autoActivityTracking(boolean enabled) {
            this.activityTracking = enabled;
            return this;
        }

        public Builder adIdCollection(boolean enabled) {
            this.adIdCollection = enabled;
            return this;
        }

        public Builder sampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }

        public Builder mapFunction(MapFunction func) {
            this.mapFunc = func;
            return this;
        }

        public GoogleAnalyticsTracker build() {
            return new GoogleAnalyticsTracker(this);
        }
    }
}
