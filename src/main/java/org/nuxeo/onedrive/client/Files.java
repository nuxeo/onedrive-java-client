package org.nuxeo.onedrive.client;

import java.net.URL;
import java.util.Iterator;

import org.nuxeo.onedrive.client.OneDriveItem.Metadata;
import org.nuxeo.onedrive.client.resources.User;

public final class Files {
    public static Iterator<OneDriveItem.Metadata> getSharedWithMe(final User user) {
        return new ItemIterator(user.getApi(),
                new URLTemplate(user.getOperationPath("/drive/sharedWithMe")).build(user.getApi().getBaseURL()));
    }

    private final static class ItemIterator implements Iterator<OneDriveItem.Metadata> {
        private final OneDriveAPI api;
        private final JsonObjectIterator iterator;

        public ItemIterator(final OneDriveAPI api, final URL url) {
            this.api = api;
            this.iterator = new JsonObjectIterator(api, url);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Metadata next() {
            return OneDriveItem.parseJson(api, iterator.next());
        }
    }
}
