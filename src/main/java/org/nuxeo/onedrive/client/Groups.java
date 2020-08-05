package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.types.GroupItem;
import org.nuxeo.onedrive.client.types.GroupItem.Metadata;
import org.nuxeo.onedrive.client.types.User;

import java.net.URL;
import java.util.Iterator;

public final class Groups {
    public static Iterator<GroupItem.Metadata> getMemberOfGroups(final User user) {
        return new GroupItemIterator(user.getApi(),
                new URLTemplate(user.getOperationPath("/memberOf/$/microsoft.graph.group")).build(user.getApi().getBaseURL()));
    }

    private final static class GroupItemIterator implements Iterator<GroupItem.Metadata> {
        private final OneDriveAPI api;
        private final JsonObjectIterator iterator;

        public GroupItemIterator(final OneDriveAPI api, final URL url) {
            this.api = api;
            this.iterator = new JsonObjectIterator(api, url);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Metadata next() {
            final JsonObject root = iterator.next();
            return GroupItem.fromJson(api, root);
        }
    }
}
