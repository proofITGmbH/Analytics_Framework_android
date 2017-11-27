package io.stanwood.analyticstest.analytics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.stanwood.framework.analytics.TrackerContainer;
import io.stanwood.framework.analytics.bugfender.BugfenderTracker;
import io.stanwood.framework.analytics.fabric.FabricTracker;
import io.stanwood.framework.analytics.firebase.FirebaseTracker;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppTracker.init(TrackerContainer.builder()
                .addTracker(FirebaseTracker.builder(getApplication())
                        .setExceptionTrackingEnabled(true)
                        .build())
                .addTracker(FabricTracker.builder(getApplication())
                        .setExceptionTrackingEnabled(true)
                        .build())
                .addTracker(BugfenderTracker.builder(getApplication(), "KEY")
                        .enableLogcatLogging(false)
                        .enableUiLogging(false)
                        .setExceptionTrackingEnabled(true)
                        .build())
                .build());

    }

}
