package sam.com.beaconsclientapp.webstorage;

import org.altbeacon.beacon.Beacon;

import sam.com.beaconsclientapp.webstorage.entities.BeaconEntity;

/**
 * Web Storage Interface
 */
public interface ClientWebStorage {
    void ping(WebStorageCallback<Boolean> callback);
    void init(WebStorageCallback<Void> callback);
    void getBeacon(Beacon beacon, WebStorageCallback<BeaconEntity> callback);
}
