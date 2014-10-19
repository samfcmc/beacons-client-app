package sam.com.beaconsclientapp;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;

import java.util.Collection;

import sam.com.beaconsclientapp.bluetooth.IBeaconManager;
import sam.com.beaconsclientapp.bluetooth.IBeaconParser;
import sam.com.beaconsclientapp.webstorage.WebStorageCallback;
import sam.com.beaconsclientapp.webstorage.entities.BeaconEntity;


public class MainActivity extends Activity implements BeaconConsumer {

    private BeaconClientApplication application;
    private BeaconManager beaconManager;
    private Region region;

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

        this.application.getClientWebStorage().init(new WebStorageCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(MainActivity.this, "Login failded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Void response) {
                MainActivity.this.beaconManager.bind(MainActivity.this);
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
        this.beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if(beacons.size() > 0) {
                    showContentToUser(beacons);
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

    private void showContentToUser(Collection<Beacon> beacons) {
        Beacon beacon = getNearestBeacon(beacons);

        try {
            this.beaconManager.stopRangingBeaconsInRegion(this.region);
        } catch (RemoteException e) {

        }

        this.application.getClientWebStorage().getBeacon(beacon, new WebStorageCallback<BeaconEntity>() {
            @Override
            public void onFailure(Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error ocurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess(final BeaconEntity response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(response != null) {
                            openBrowserActivity(response.getContent());
                        }
                    }
                });
            }
        });
    }

    private void openBrowserActivity(String url) {
        Intent intent = new Intent(this, ShowContentActivity.class);

        intent.putExtra("url", url);
        startActivity(intent);
    }
}
