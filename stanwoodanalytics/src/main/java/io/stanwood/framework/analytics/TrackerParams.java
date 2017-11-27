package io.stanwood.framework.analytics;

import android.support.annotation.NonNull;

public class TrackerParams {
    private final String eventName;
    private String itemId;
    private String name;
    private String category;
    private String contentType;

    private TrackerParams(@NonNull Builder builder) {
        eventName = builder.eventName;
        itemId = builder.itemId;
        name = builder.name;
        category = builder.category;
        contentType = builder.contentType;
    }

    public static Builder builder(String eventName) {
        return new Builder(eventName);
    }

    public String getEventName() {
        return eventName;
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "TrackerParams{" +
                "eventName='" + eventName + '\'' +
                ", itemId='" + itemId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }

    public static class Builder {
        private String eventName;
        private String itemId = null;
        private String name = null;
        private String category = null;
        private String contentType = null;

        Builder(@NonNull String eventName) {
            this.eventName = eventName;
        }

        public Builder setId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public TrackerParams build() {
            return new TrackerParams(this);
        }
    }
}