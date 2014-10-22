package sam.com.beaconsclientapp;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.HashMap;
import java.util.Map;

import sam.com.beaconsclientapp.webstorage.ClientWebStorage;
import sam.com.beaconsclientapp.webstorage.KinveyClientWebStorage;

/**
 *
 */
public class BeaconClientApplication extends Application implements BootstrapNotifier {

    private RegionBootstrap regionBootstrap;
    private Region region;
    private ClientWebStorage clientWebStorage;
    private boolean detectedBeaconsSinceBoot = false;
    private BackgroundPowerSaver backgroundPowerSaver;
    private Map<String, Beacon> alreadyDetectedBeacons;

    @Override

    public void onCreate() {
        super.onCreate();
        this.alreadyDetectedBeacons = new HashMap<String, Beacon>();
        this.region = new Region("backgroundRegion", null, null, null);
        this.regionBootstrap = new RegionBootstrap(this, region);
        this.backgroundPowerSaver = new BackgroundPowerSaver(this);

        this.clientWebStorage = new KinveyClientWebStorage(this);
    }

    public void addDetectedBeacon(Beacon beacon) {
        String id = getBeaconCompleteId(beacon);
        this.alreadyDetectedBeacons.put(id, beacon);
    }

    public String getBeaconCompleteId(Beacon beacon) {
        return beacon.getId1().toHexString() + beacon.getId2().toHexString() + beacon.getId3().toHexString();
    }

    public boolean beaconWasAlreadyDetected(Beacon beacon) {
        String id = getBeaconCompleteId(beacon);
        return this.alreadyDetectedBeacons.containsKey(id);
    }

    public void resetAlreadyDetectedBeacons() {
        this.alreadyDetectedBeacons.clear();
    }

    public ClientWebStorage getClientWebStorage() {
        return clientWebStorage;
    }

    @Override
    public void didEnterRegion(Region region) {
        /*if(!this.detectedBeaconsSinceBoot) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.detectedBeaconsSinceBoot = true;
        }
        else {

        }*/

    }

    @Override
    public void didExitRegion(Region region) {
        //Don't care
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        //Don't care
    }

    private void logToDisplay(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
