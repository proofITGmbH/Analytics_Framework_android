package io.stanwood.analyticstest.analytics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void samples() {
        AdvancedAppTracker.init(getApplication());
        AdvancedAppTracker.instance().trackUser("alice", "alice@bob.com");
        AdvancedAppTracker.instance().trackScreenView("home");
        AdvancedAppTracker.instance().trackAdLoaded("123456");
        AdvancedAppTracker.instance().trackShowDetails("id", "details of id");
        AdvancedAppTracker.instance().trackException(new IllegalStateException("error"));
        Timber.d("message");
        Timber.e(new IllegalStateException("error"));
    }
}
