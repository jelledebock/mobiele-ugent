package be.ugent.oomt.pong;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import be.ugent.oomt.pong.GameLogic.GameState;
import be.ugent.oomt.pong.GameLogic.GameThread;

import static java.lang.StrictMath.abs;

/**
 * Created by elias on 21/09/15.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private final GameState gameState = new GameState();
    private GameThread _thread;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        _thread = new GameThread(getHolder(), gameState);
        _thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish
        _thread.stopAndWait();
        _thread = null;
    }

    // TODO: Override onTouchEvent to start the game and control the paddle

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            gameState.play();
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            Log.i("Notice","Moving paddle to "+event.getX());
            gameState.movePaddleTo((int)event.getX());
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float acceleration=0f;
        switch(getResources().getConfiguration().orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
                acceleration = event.values[1];
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                acceleration = event.values[0];
        }
        int pm=-1;

        switch(((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation()){
            case Surface.ROTATION_0:
                pm=-1;
                break;
            case Surface.ROTATION_90:
                pm=1;
                break;
            case Surface.ROTATION_180:
                pm=1;
                break;
            case Surface.ROTATION_270:
                pm=-1;
                break;
        }

        Log.d("Acceleration",""+acceleration);
        float factor = 90 / abs(SensorManager.STANDARD_GRAVITY);
        Log.d("Factor",""+factor);
        int guessed_position=pm*(int)(acceleration*factor);
        Log.d("Guessed position",""+guessed_position);
        gameState.movePaddle(guessed_position);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // TODO: implement SensorEventListener and implement its methods

}
