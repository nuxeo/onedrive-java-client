package org.nuxeo.onedrive.client;

import java.net.URL;
import java.util.Iterator;

import com.eclipsesource.json.JsonObject;

import org.nuxeo.onedrive.client.resources.DirectoryObject;
import org.nuxeo.onedrive.client.resources.User;
import org.nuxeo.onedrive.client.resources.DirectoryObject.Metadata;

public final class Groups {
    public static Iterator<DirectoryObject.Metadata> getMemberOfGroups(final User user) {
        return new DirectoryIterator(user.getApi(),
                new URLTemplate(user.getOperationPath("/memberOf")).build(user.getApi().getBaseURL()));
    }

    private final static class DirectoryIterator implements Iterator<DirectoryObject.Metadata> {
        private final OneDriveAPI api;
        private final JsonObjectIterator iterator;

        public DirectoryIterator(final OneDriveAPI api, final URL url) {
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
            return DirectoryObject.fromJson(api, root);
        }
    }
}
