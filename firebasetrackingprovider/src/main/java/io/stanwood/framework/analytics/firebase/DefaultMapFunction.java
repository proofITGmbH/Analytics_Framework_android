package io.stanwood.framework.analytics.firebase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.analytics.FirebaseAnalytics;

import io.stanwood.framework.analytics.TrackerParams;

public class DefaultMapFunction implements MapFunction {
    @Nullable
    @Override
    public Bundle map(TrackerParams params) {
        Bundle bundle = null;
        if (!TextUtils.isEmpty(params.getItemId())) {
            bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, params.getItemId());
        }
        if (!TextUtils.isEmpty(params.getCategory())) {
            bundle = bundle != null ? bundle : new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, params.getCategory());
        }
        if (!TextUtils.isEmpty(params.getContentType())) {
            bundle = bundle != null ? bundle : new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, params.getContentType());
        }
        if (!TextUtils.isEmpty(params.getName())) {
            bundle = bundle != null ? bundle : new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, params.getName());
        }
        return bundle;
    }
}