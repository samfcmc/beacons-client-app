package sam.com.beaconsclientapp;

import android.app.Application;
import android.content.Intent;

import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 *
 */
public class BeaconClientApplication extends Application implements BootstrapNotifier {

    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();
        Region region = new Region("myRegion", null, null, null);
        this.regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didEnterRegion(Region region) {
        this.regionBootstrap.disable();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
