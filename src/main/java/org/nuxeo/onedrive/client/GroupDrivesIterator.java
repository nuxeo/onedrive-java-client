package org.nuxeo.onedrive.client;

import java.util.Iterator;

import com.eclipsesource.json.JsonObject;

import org.nuxeo.onedrive.client.resources.GroupItem;

public class GroupDrivesIterator implements Iterator<OneDriveDrive.Metadata> {
    private final static URLTemplate GROUP_DRIVELIST_URL = new URLTemplate("/groups/%1/drives");

    private final OneDriveAPI api;
    private final GroupItem group;
    private final JsonObjectIterator jsonObjectIterator;

    public GroupDrivesIterator(final OneDriveAPI api, final GroupItem group) {
        this.api = api;
        this.group = group;
        this.jsonObjectIterator = new JsonObjectIterator(api,
                GROUP_DRIVELIST_URL.build(api.getBaseURL(), group.getId()));
    }

    @Override
    public boolean hasNext() throws OneDriveRuntimeException {
        return jsonObjectIterator.hasNext();
    }

    @Override
    public OneDriveDrive.Metadata next() {
        final JsonObject nextObject = jsonObjectIterator.next();
        final String id = nextObject.get("id").asString();

        final OneDriveDrive drive = new OneDriveDrive(api, id);
        return drive.new Metadata(nextObject);
    }
}
