package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.resources.GroupItem;

import java.util.Iterator;
import java.util.Objects;

public class GroupsIterator implements Iterator<GroupItem.Metadata> {
    private final static URLTemplate GROUP_LIST_URL = new URLTemplate("/memberOf/$/microsoft.graph.group");

    private final OneDriveAPI api;
    private final JsonObjectIterator jsonObjectIterator;

    public GroupsIterator(OneDriveAPI api) {
        this.api = Objects.requireNonNull(api);
        this.jsonObjectIterator = new JsonObjectIterator(api, GROUP_LIST_URL.build(api.getEmailURL()));
    }

    @Override
    public boolean hasNext() throws OneDriveRuntimeException {
        return jsonObjectIterator.hasNext();
    }

    @Override
    public GroupItem.Metadata next() {
        final JsonObject jsonObject = jsonObjectIterator.next();
        final String id = jsonObject.get("id").asString();
        final GroupItem groupItem = new GroupItem(api, id);
        return groupItem.new Metadata(jsonObject);
    }
}
