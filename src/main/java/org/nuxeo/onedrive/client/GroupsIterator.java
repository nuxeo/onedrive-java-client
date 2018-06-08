package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.resources.GroupItem;

import java.util.Iterator;
import java.util.Objects;

public class GroupsIterator implements Iterator<GroupItem.Metadata> {
    private final static URLTemplate GROUP_LIST_URL = new URLTemplate("/groups");

    private final OneDriveAPI api;

    private final JsonObjectIterator jsonObjectIterator;

    public GroupsIterator(OneDriveAPI api) {
        this.api = Objects.requireNonNull(api);
        this.jsonObjectIterator = new JsonObjectIterator(api, GROUP_LIST_URL.build(api.getBaseURL())) {

            @Override
            protected void onResponse(JsonObject response) {
                GroupsIterator.this.onResponse(response);
            }
        };
    }

    @Override
    public boolean hasNext() throws OneDriveRuntimeException {
        return jsonObjectIterator.hasNext();
    }

    @Override
    public GroupItem.Metadata next() {
        JsonObject nextObject = jsonObjectIterator.next();
        return new GroupItem.Metadata(nextObject);
    }

    /**
     * @since 1.1
     */
    protected void onResponse(JsonObject response) {
        // Hook method
    }
}
