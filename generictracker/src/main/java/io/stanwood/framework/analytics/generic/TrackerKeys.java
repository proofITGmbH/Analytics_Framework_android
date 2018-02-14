package io.stanwood.framework.analytics.generic;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class TrackerKeys {
    private Map<String, Object> customKeys;
    private String trackKeysEventId;

    private TrackerKeys(@NonNull Builder builder) {
        trackKeysEventId = builder.trackKeysEventId;
        customKeys = builder.customKeys;
    }

    public static Builder builder(String trackKeysEventid) {
        return new Builder(trackKeysEventid);
    }

    public Map<String, Object> getCustomKeys() {
        return customKeys;
    }

    public String getTrackKeysEventId() {
        return trackKeysEventId;
    }

    public static class Builder {
        private final String trackKeysEventId;
        private Map<String, Object> customKeys;

        Builder(String trackKeysEventid) {
            this.trackKeysEventId = trackKeysEventid;
        }

        public Builder addCustomProperty(String key, Object value) {
            if (value != null) {
                if (customKeys == null) {
                    customKeys = new HashMap<>();
                }
                customKeys.put(key, value);
            }
            return this;
        }

        public TrackerKeys build() {
            return new TrackerKeys(this);
        }
    }
}