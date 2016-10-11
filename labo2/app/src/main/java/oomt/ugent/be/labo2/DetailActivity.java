package oomt.ugent.be.labo2;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Notice","Detail On create method called");
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            Log.i("Notice","No saved instance");
            Bundle intent = getIntent().getExtras();

            // Check what fragment is currently shown, replace if needed.
            DetailFragment details = (DetailFragment)
                    getFragmentManager().findFragmentById(R.id.activity_detail_fragment);
            if (details == null) {
                // Make new fragment to show this selection.
                details = DetailFragment.newInstance(intent.getInt("id"));
                getFragmentManager().beginTransaction()
                        .replace(R.id.activity_detail_fragment, details)
                        .commit();
            }
        }

        setContentView(R.layout.activity_detail);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Notice","Detail On start method called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Notice","Detail On stop method called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Notice","Detail On destroy method called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Notice","Detail On pause method called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Notice","Detail On resume method called");
    }
}
