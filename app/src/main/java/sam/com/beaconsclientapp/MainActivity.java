package sam.com.beaconsclientapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import sam.com.beaconsclientapp.bluetooth.IBeaconManager;
import sam.com.beaconsclientapp.webstorage.WebStorageCallback;
import sam.com.beaconsclientapp.webstorage.entities.BeaconEntity;


public class MainActivity extends Activity implements BeaconConsumer {

    private static final long FOREGROUND_BETWEEN_SCAN_PERIOD = 5000;
    private BeaconClientApplication application;
    private BeaconManager beaconManager;
    private Region region;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        this.application = (BeaconClientApplication) getApplication();
        this.beaconManager = IBeaconManager.getInstance(this);
        this.region = new Region("myRegion", null, null, null);
        this.handler = new Handler();

        this.application.getClientWebStorage().init(new WebStorageCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(MainActivity.this, "Login failded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Void response) {
                MainActivity.this.beaconManager.bind(MainActivity.this);
                //initResetDetectedBeaconsTimer();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.beaconManager.isBound(this)) {
            this.beaconManager.setBackgroundMode(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(this.beaconManager.isBound(this)) {
            this.beaconManager.setBackgroundMode(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.beaconManager.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBeaconServiceConnect() {
        this.beaconManager.setForegroundBetweenScanPeriod(FOREGROUND_BETWEEN_SCAN_PERIOD);
        this.beaconManager.setBackgroundBetweenScanPeriod(FOREGROUND_BETWEEN_SCAN_PERIOD);

        this.beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if(beacons.size() > 0) {
                    onBeaconsDetected(beacons);
                }
            }
        });
        try {
            this.beaconManager.startRangingBeaconsInRegion(this.region);
        } catch (RemoteException e) {
            Toast.makeText(this, "Remote exception", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    private Beacon getNearestBeacon(Collection<Beacon> beacons) {
        Beacon nearestBeacon = beacons.iterator().next();

        for(Beacon beacon : beacons) {
            if(beacon.getDistance() < nearestBeacon.getDistance()) {
                nearestBeacon = beacon;
            }
        }

        return nearestBeacon;
    }

    private void onBeaconsDetected(Collection<Beacon> beacons) {

        final Beacon beacon = getNearestBeacon(beacons);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logToDisplay("Beacon detected");
            }
        });

        if(this.application.beaconWasAlreadyDetected(beacon)) {
            return;
        }

        this.application.addDetectedBeacon(beacon);

        this.application.getClientWebStorage().getBeacon(beacon, new WebStorageCallback<BeaconEntity>() {
            @Override
            public void onFailure(Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logToDisplay("Error ocurred");
                        MainActivity.this.application.resetAlreadyDetectedBeacons();
                    }
                });
            }

            @Override
            public void onSuccess(final BeaconEntity response) {
                if(response != null) {
                    showNotification(response);
                }
            }
        });
        //this.application.addDetectedBeacon(beacon);
        restartRanging();
    }

    private void initResetDetectedBeaconsTimer() {
        this.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logToDisplay("Handler test");
                        MainActivity.this.application.resetAlreadyDetectedBeacons();
                    }
                });
                //MainActivity.this.application.resetAlreadyDetectedBeacons();
                MainActivity.this.handler.postDelayed(this, 20000);
            }
        }, 10000);
    }

    private void logToDisplay(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void restartRanging() {
        try {
            this.beaconManager.startRangingBeaconsInRegion(this.region);
        } catch (RemoteException e) {

        }
    }

    private void openBrowserActivity(String url) {
        Intent intent = new Intent(this, ShowContentActivity.class);

        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void showNotification(BeaconEntity beacon) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(android.R.drawable.stat_notify_chat);
        builder.setContentTitle(beacon.getName());
        builder.setContentText(beacon.getDescription());
        builder.setAutoCancel(true);
        long vibration[] = {0, 500, 200, 500, 200, 500};
        builder.setVibrate(vibration);

        Intent intent = new Intent(this, ShowContentActivity.class);
        intent.putExtra("url", beacon.getContent());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ShowContentActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0xFF, builder.build());
    }
}
