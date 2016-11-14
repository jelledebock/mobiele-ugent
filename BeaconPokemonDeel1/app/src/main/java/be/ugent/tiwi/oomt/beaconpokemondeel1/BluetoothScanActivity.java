package be.ugent.tiwi.oomt.beaconpokemondeel1;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


/**
 * Deze activity bevat de code van het eerste deel: scannen naar BluetoothBeacons
 * Vooraleer het scannen kan beginnen moeten er een aantal zaken gecontroleerd worden:
 *  - is Bluetooth ingeschakeld ? Indien niet: vraag aan de gebruiker om dit in te schakelen
 *  - zijn de locationservices ingeschakeld ? indien niet: vraag aan de gebruiker om dit in te schakelen
 *  - hebben we alle permissies om te scannen ? indien niet: vraag ze aan de gebruiker
 */
public class BluetoothScanActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 123;
    private static final int REQUEST_ENABLE_LOCATION = 456;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 789;
    private BluetoothAdapter mBluetoothAdapter;
    private ScanCallback clb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        clb = new PokemonScanCallback();

        checkBluetoothEnabled();

    }

    private void checkBluetoothEnabled(){
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        // https://developer.android.com/guide/topics/connectivity/bluetooth-le.html#connect
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else{
            checkLocationServicesEnabled();
        }


    }

    private void checkLocationServicesEnabled(){
        int locationMode = Settings.Secure.LOCATION_MODE_OFF;
        try {
            locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {
            Toast t = Toast.makeText(this, "Please enable the location services", Toast.LENGTH_LONG);
            t.show();

            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(viewIntent, REQUEST_ENABLE_LOCATION);
        }
        else{
            checkPermissions();
        }
    }

    private void checkPermissions(){
        // De Location permission is een dangerous permission
        // We moeten deze at runtime aanvragen
        // https://developer.android.com/training/permissions/requesting.html
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        }
        else{
            startScan();
        }

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("OnActivityResult",requestCode+" -> "+resultCode);

        if (requestCode == REQUEST_ENABLE_BT){
            // Controleer opnieuw (je kan ook de resultcode vergelijken met RESULT_OK)
            checkBluetoothEnabled();

        }
        else if(requestCode == REQUEST_ENABLE_LOCATION){
            // controleer opnieuw
            checkLocationServicesEnabled();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startScan();
            }
            else{
                // TODO: handel dit proper af
            }
        }
    }

    public void startScan(){
        mBluetoothAdapter.getBluetoothLeScanner().startScan(clb);
    }

    public void stopScan(){
        if (mBluetoothAdapter != null && mBluetoothAdapter.getBluetoothLeScanner() != null) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(clb);
        }
    }


    private class PokemonScanCallback extends ScanCallback{
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i("scan", result.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }
}
