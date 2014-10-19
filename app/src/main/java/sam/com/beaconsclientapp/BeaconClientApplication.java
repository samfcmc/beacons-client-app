package sam.com.beaconsclientapp;

import android.app.Application;
import android.content.Intent;
import android.os.RemoteException;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

import sam.com.beaconsclientapp.bluetooth.IBeaconManager;
import sam.com.beaconsclientapp.webstorage.ClientWebStorage;
import sam.com.beaconsclientapp.webstorage.KinveyClientWebStorage;
import sam.com.beaconsclientapp.webstorage.WebStorageCallback;

/**
 *
 */
public class BeaconClientApplication extends Application implements BootstrapNotifier {

    private RegionBootstrap regionBootstrap;
    private Region region;
    private ClientWebStorage clientWebStorage;
    private boolean detectedBeaconsSinceBoot = false;
    private BackgroundPowerSaver backgroundPowerSaver;

    @Override

    public void onCreate() {
        super.onCreate();
        this.region = new Region("backgroundRegion", null, null, null);
        this.regionBootstrap = new RegionBootstrap(this, region);
        this.backgroundPowerSaver = new BackgroundPowerSaver(this);

        this.clientWebStorage = new KinveyClientWebStorage(this);
    }

    public ClientWebStorage getClientWebStorage() {
        return clientWebStorage;
    }

    @Override
    public void didEnterRegion(Region region) {
        if(!this.detectedBeaconsSinceBoot) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.detectedBeaconsSinceBoot = true;
        }
        else {

        }

    }

    @Override
    public void didExitRegion(Region region) {
        //Don't care
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        //Don't care
    }

}
