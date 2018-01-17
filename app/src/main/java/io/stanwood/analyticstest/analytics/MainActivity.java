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
        AdavancedAppTracker.init(getApplication());
        AdavancedAppTracker.instance().trackUser("666-666", "go@to.hell");
        AdavancedAppTracker.instance().trackScreenView("home");
        AdavancedAppTracker.instance().trackAdLoaded("123456");
        AdavancedAppTracker.instance().trackShowDetails("id", "details of id");
        AdavancedAppTracker.instance().trackException(new IllegalStateException("error"));
        Timber.d("message");
        Timber.e(new IllegalStateException("error"));
    }
}
