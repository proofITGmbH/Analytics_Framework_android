package io.stanwood.framework.analytics.firebase;


import android.os.Bundle;
import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.generic.TrackerKeys;
import io.stanwood.framework.analytics.generic.TrackerParams;

public interface MapFunction {
    @Nullable
    Bundle map(TrackerParams params);

    @Nullable
    TrackerKeys mapKeys(TrackerKeys keys);

}
