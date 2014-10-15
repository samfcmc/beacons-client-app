package sam.com.beaconsclientapp.bluetooth;

import org.altbeacon.beacon.BeaconParser;

/**
 * IBeaconParser: Beacon Parser for simulated beacons using the computer and a
 * bluetooth usb dongle
 */
public class IBeaconParser extends BeaconParser {
    private static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    public IBeaconParser() {
        super();
        setBeaconLayout(BEACON_LAYOUT);
    }
}
