package be.ugent.tiwi.oomt.beaconpokemondeel1;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class ScanService extends Service {

    private ScanCallback clb;
    private BluetoothAdapter mBluetoothAdapter;
    private int pokemonId=1;
    private HashSet<String> pokeSet = new HashSet();

    public ScanService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service", "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i("service", "oncreate");
        super.onCreate();
        clb = new PokemonScanCallback();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter.getBluetoothLeScanner().startScan(clb);
    }

    @Override
    public void onDestroy() {
        Log.i("service", "onDestroy");
        if (mBluetoothAdapter != null && mBluetoothAdapter.getBluetoothLeScanner() != null) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(clb);
        }
        super.onDestroy();
    }

    private class PokemonScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i("scan", result.toString());
            if(!pokeSet.contains(result.getDevice().getAddress())){
                pokeSet.add(result.getDevice().getAddress());
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(android.R.drawable.star_on)
                        .setContentTitle("New Pokemon Found!")
                        .setContentText(result.getDevice().getAddress());
                NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(pokemonId++, mBuilder.build());
            }
            else{
                Log.i("already discovered",result.getDevice().getAddress());
            }
        }
    }
}
