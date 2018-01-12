package io.stanwood.analyticstest.analytics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void samples() {
        AppTracker.init(getApplication());
        AppTracker.instance().trackUser("666-666", "go@to.hell");
        AppTracker.instance().trackScreenView("home");
        AppTracker.instance().trackAdLoaded("123456");
        AppTracker.instance().trackShowDetails("id", "details of id");
        AppTracker.instance().trackException(new IllegalStateException("error"));
    }
}
