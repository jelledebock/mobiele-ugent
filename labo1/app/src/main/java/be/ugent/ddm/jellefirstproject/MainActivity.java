package be.ugent.ddm.jellefirstproject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("INFO","Main activity : onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("INFO","Main activity : onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("INFO","Main activity : onDestroy()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("INFO","Main activity : onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("INFO","Main activity : onRestart()");
    }

    public void getHint(View view) {
        Context context = getApplicationContext();
        CharSequence text = "He lived in the 16th century";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
