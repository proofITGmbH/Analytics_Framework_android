package io.stanwood.analyticstest.analytics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.stanwood.framework.analytics.fabric.FabricTracker;
import io.stanwood.framework.analytics.firebase.FirebaseTracker;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void samples() {
        AdvancedAppTracker.init(getApplication());
        AdvancedAppTracker.instance().trackUser("alice", "alice@bob.com", null);
        AdvancedAppTracker.instance().trackAdLoaded("123456");
        AdvancedAppTracker.instance().trackShowDetails("id", "details of id");
        AdvancedAppTracker.instance().enable(true);
        AdvancedAppTracker.instance().enable(true, FabricTracker.TRACKER_NAME, FirebaseTracker.TRACKER_NAME);
        AdvancedAppTracker.instance().isTrackerEnabled(FabricTracker.TRACKER_NAME);
        Timber.d("message");
        Timber.e(new IllegalStateException("error"));
    }
}
