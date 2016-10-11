package oomt.ugent.be.labo2;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE){
            Log.i("Switched to landscape","notice");
            DetailFragment details = new DetailFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.maincontainer, details)
                    .commit();
        }
        setContentView(R.layout.activity_main);
    }

    public void showDetails(int id){
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            Log.i("notice","in landscape: updating container");

            // Check what fragment is currently shown, replace if needed.
            DetailFragment details = (DetailFragment)
                    getFragmentManager().findFragmentById(R.id.activity_detail);
            if (details == null) {
                // Make new fragment to show this selection.
                details = DetailFragment.newInstance(id);
                getFragmentManager().beginTransaction()
                        .replace(R.id.maincontainer, details)
                        .commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Log.i("notice","in portrait: going to detailpage");
            Intent intent = new Intent();
            intent.setClass(this, DetailActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Notice","On start method called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Notice","On stop method called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Notice","On destroy method called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Notice","On pause method called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Notice","On resume method called");
    }
}
