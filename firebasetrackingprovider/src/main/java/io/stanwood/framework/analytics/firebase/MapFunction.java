package io.stanwood.framework.analytics.firebase;


import android.os.Bundle;
import android.support.annotation.Nullable;

import io.stanwood.framework.analytics.generic.TrackerParams;

public interface MapFunction {
    @Nullable
    Bundle map(TrackerParams params);

}