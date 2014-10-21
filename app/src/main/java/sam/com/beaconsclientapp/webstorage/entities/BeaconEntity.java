package sam.com.beaconsclientapp.webstorage.entities;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * BeaconEntity: Entity that represents Beacons json objects
 */
public class BeaconEntity extends GenericJson {

    @Key("_id")
    private String id;

    @Key
    private String name;

    @Key
    private String uuid;

    @Key
    private String major;

    @Key
    private String minor;

    @Key
    private String content;

    @Key
    private String description;

    public BeaconEntity() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
