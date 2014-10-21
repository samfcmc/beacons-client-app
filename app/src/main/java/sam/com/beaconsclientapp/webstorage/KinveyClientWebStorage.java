package sam.com.beaconsclientapp.webstorage;

import android.content.Context;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

import org.altbeacon.beacon.Beacon;

import sam.com.beaconsclientapp.webstorage.entities.BeaconEntity;

/**
 * Kinvey Web Storage: Implementation of web storage for Kinvey
 */
public class KinveyClientWebStorage implements ClientWebStorage {
    private final Client client;

    private static final String COLLECTION_NAME = "beacons";
    private static final String UUID = "uuid";

    public KinveyClientWebStorage(Context context) {
        this.client = new Client.Builder(context.getApplicationContext()).build();
    }

    @Override
    public void ping(final WebStorageCallback<Boolean> callback) {
        this.client.ping(new KinveyPingCallback() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                callback.onSuccess(aBoolean);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }

    @Override
    public void init(final WebStorageCallback<Void> callback) {
        //Login with an implicit user
        if(this.client.user().isUserLoggedIn()) {
            callback.onSuccess(null);
        }
        else {
            this.client.user().login(new KinveyClientCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    callback.onSuccess(null);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    callback.onFailure(throwable);
                }
            });
        }
    }

    @Override
    public void getBeacon(Beacon beacon, final WebStorageCallback<BeaconEntity> callback) {
        Query query = new Query();
        String uuid = beacon.getId1().toHexString();
        query.equals(UUID, uuid);
        AsyncAppData<BeaconEntity> appData = this.client.appData(COLLECTION_NAME, BeaconEntity.class);

        appData.get(query, new KinveyListCallback<BeaconEntity>() {
            @Override
            public void onSuccess(BeaconEntity[] beaconEntities) {
                if(beaconEntities.length > 0) {
                    callback.onSuccess(beaconEntities[0]);
                }
                else {
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }

}
