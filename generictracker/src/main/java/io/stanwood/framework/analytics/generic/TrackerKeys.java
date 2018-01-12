package io.stanwood.framework.analytics.generic;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class TrackerKeys {
    private Map<String, Object> customKeys;

    private TrackerKeys(@NonNull Builder builder) {
        customKeys = builder.customKeys;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Object> getCustomKeys() {
        return customKeys;
    }

    public static class Builder {
        private Map<String, Object> customKeys;

        public Builder addCustomProperty(String key, Object value) {
            if (customKeys == null) {
                customKeys = new HashMap<>();
            }
            customKeys.put(key, value);
            return this;
        }

        public TrackerKeys build() {
            return new TrackerKeys(this);
        }
    }
}