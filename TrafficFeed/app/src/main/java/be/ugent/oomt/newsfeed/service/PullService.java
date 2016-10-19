package be.ugent.oomt.newsfeed.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import be.ugent.oomt.newsfeed.content.CustomContentProvider;
import be.ugent.oomt.newsfeed.content.database.DatabaseContract;

public class PullService extends Service {

    private static final String TAG = PullService.class.getCanonicalName();
    private static final String FETCH_URL = "https://datatank.stad.gent/4/mobiliteit/verkeersmeldingenactueel.json";
    private static RequestQueue requestQueue;
    private static final long DELAY = 1000 * 60 * 5; // 5 minutes

    // Create the Handler object (on the main thread by default)
    private Handler handler;

    // Define the code block to be executed
    private final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Service request received. fetching new data.");
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // fetch data from url (this method is already executing in a thread)
                fetchData();
            } else {
                Log.w(TAG, "No network connectivity. Unable to fetch new data.");
            }

            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, DELAY);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // do not allow binding.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        HandlerThread thread = new HandlerThread("background thread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.post(runnableCode);
        Log.d(TAG, "Service created.");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed.");
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                Log.d(TAG,"request running: "+request.getTag());
                return true;
            }
        });
        handler.removeCallbacks(runnableCode);
        requestQueue = null;
        handler = null;
        super.onDestroy();
    }

    private void fetchData() {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(FETCH_URL, null, future, future);
        requestQueue.add(request);

        try {
            // synchronize Volley request.
            JSONObject response = future.get(30, TimeUnit.SECONDS); // Blocks for at most 30 seconds.
            Log.v(TAG, "Response: " + response.toString());
            updateContentProvider(response.getJSONArray("result"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Malformed json.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed fetching new data.");
        }
    }

    /**
     * Update the content provider from the json data. This data has a lot of errors and needs to be
     * as robust as possible therefor we allow empty strings if the keys are not found.
     * @param json
     * @throws JSONException
     */
    private void updateContentProvider(JSONArray json) throws JSONException {
        List<ContentValues> bulkValues = new ArrayList<>();
        int countBadItems = 0;
        for (int i=0; i<json.length(); i++) {
            JSONObject entity = json.getJSONObject(i);
            JSONObject parent = entity;
            if (entity.has("objectsToStore")) // to fix some strange data of tweets
                entity = entity.optJSONArray("objectsToStore").optJSONObject(0) != null ? entity.optJSONArray("objectsToStore").optJSONObject(0) : entity;

            JSONObject payload = entity.optJSONObject("payload"); // can be null
            if (payload == null) {
                payload = new JSONObject("{}"); // fix bad data
            }
            JSONObject sourcePayload = entity.optJSONObject("sourcePayload"); // can be null
            if (sourcePayload == null)
                sourcePayload = new JSONObject("{}"); // fix bad data
            String source = entity.optString("source", null);
            if (source == null)
                source = payload.optString("source");

            ContentValues values = new ContentValues();

            // status update
            try {
                if (source.equalsIgnoreCase("waze")) {
                    values.put(DatabaseContract.Item.COLUMN_NAME_MESSAGE, payload.optString("message",null));
                    values.put(DatabaseContract.Item.COLUMN_NAME_ID, sourcePayload.optString("uuid", null));
                } else if (source.equalsIgnoreCase("irail")) {
                    values.put(DatabaseContract.Item.COLUMN_NAME_MESSAGE, payload.optString("message", null));
                    values.put(DatabaseContract.Item.COLUMN_NAME_ID, String.format(Locale.getDefault(), "%d-%f-%f", parent.optLong("timestamp"), parent.optDouble("longitude"), parent.optDouble("latitude"))); // no real ID on iRail
                } else if (source.equalsIgnoreCase("coyote")) {
                    values.put(DatabaseContract.Item.COLUMN_NAME_ID, payload.optString("id",null));
                    values.put(DatabaseContract.Item.COLUMN_NAME_MESSAGE, String.format(Locale.getDefault(), "Speed limit of %d km/h on %s.", payload.optInt("speed_limit", -1), payload.optString("road_name", "unknown"))); // no real message for coyote
                }
            } catch (Exception ex) {
            } finally {
                // fix bad json
                if (values.get(DatabaseContract.Item.COLUMN_NAME_ID) == null) {
                    values.put(DatabaseContract.Item.COLUMN_NAME_ID, UUID.randomUUID().toString());
                    countBadItems++;
                } if (values.get(DatabaseContract.Item.COLUMN_NAME_MESSAGE) == null)
                    values.put(DatabaseContract.Item.COLUMN_NAME_MESSAGE, "Unknown alert.");
            }

            values.put(DatabaseContract.Item.COLUMN_NAME_CONCAT_NAME, String.format("%s - %s", source, entity.optString("type")));
            values.put(DatabaseContract.Item.COLUMN_NAME_SOURCE, source);
            values.put(DatabaseContract.Item.COLUMN_NAME_TYPE, entity.optString("type"));
            values.put(DatabaseContract.Item.COLUMN_NAME_TRANSPORT, entity.optString("transport"));
            values.put(DatabaseContract.Item.COLUMN_NAME_ALARM_NAME, entity.optString("alarmName"));
            values.put(DatabaseContract.Item.COLUMN_NAME_LONGITUDE, payload.optDouble("longitude"));
            values.put(DatabaseContract.Item.COLUMN_NAME_LATITUDE, payload.optDouble("latitude"));
            values.put(DatabaseContract.Item.COLUMN_NAME_TIME_STAMP, parent.optLong("timestamp"));

            bulkValues.add(values);
        }
        if (countBadItems > 0)
            Log.d(TAG, "Number of bad samples with random id: " + countBadItems);
        Log.d(TAG, String.format("Inserted or updated %d rows.", this.getContentResolver().bulkInsert(CustomContentProvider.ITEMS_CONTENT_URL, bulkValues.toArray(new ContentValues[bulkValues.size()]))));
    }
}
