package be.ugent.oomt.newsfeed;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.Locale;

import be.ugent.oomt.newsfeed.content.CustomContentProvider;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private boolean mDuelPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View detailsFrame = findViewById(R.id.detail_container);

        mDuelPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        //TODO: start service
        startService(new Intent(this, CustomContentProvider.class));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        //TODO: stop service when application is shutting down
        if(super.isFinishing()){
            stopService(new Intent(this, CustomContentProvider.class));
        }
        super.onStop();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, String.format(Locale.getDefault(), "Position: %d, id: %d", position, id));
        //TODO: open detail activity in portrait or replace fragment in landscape
    }
}
