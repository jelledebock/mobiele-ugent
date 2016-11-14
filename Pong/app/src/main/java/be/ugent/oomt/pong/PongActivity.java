package be.ugent.oomt.pong;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class PongActivity extends Activity{
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pong);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        gameView = (GameView) this.findViewById(R.id.gamefield);

        // TODO: list all sensors available for this device in Logcat
        for ( Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            Log.d("Sensor: ", "Found a type " + sensor.getType() + " sensor, name= "+sensor.getName());
        }
        // TODO: get the sensor
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }
    // TODO: override onResume and onPause to register and unregister the listener for the sensor


    @Override
    protected void onResume() {
        sensorManager.registerListener(gameView,gravitySensor,SensorManager.SENSOR_DELAY_FASTEST);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(gameView);
        super.onPause();
    }
}
