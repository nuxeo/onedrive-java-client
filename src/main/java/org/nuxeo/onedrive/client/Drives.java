package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.resources.GroupItem;
import org.nuxeo.onedrive.client.resources.Site;

import java.net.URL;
import java.util.Iterator;

public final class Drives {
    public static Iterator<OneDriveDrive.Metadata> getDrives(final GroupItem group) {
        final URL groupDrivesURL = createDrivesUrl(group.getApi(), group.getBasePath());
        return new DrivesIterator(group.getApi(), groupDrivesURL);
    }

    public static Iterator<OneDriveDrive.Metadata> getDrives(final Site site) {
        final URL siteDrivesURL = createDrivesUrl(site.getApi(), site.getBasePath());
        return new DrivesIterator(site.getApi(), siteDrivesURL);
    }

    private static URL createDrivesUrl(final OneDriveAPI api, final String basePath) {
        return new URLTemplate(createDrivesPath(basePath)).build(api.getBaseURL());
    }

    private static String createDrivesPath(final String basePath) {
        return basePath + "/drives";
    }

    private static class DrivesIterator implements Iterator<OneDriveDrive.Metadata> {
        private final OneDriveAPI api;
        private final JsonObjectIterator iterator;

        public DrivesIterator(final OneDriveAPI api, final URL url) {
            this.api = api;
            iterator = new JsonObjectIterator(api, url);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public OneDriveDrive.Metadata next() {
            final JsonObject nextObject = iterator.next();
            final String id = nextObject.get("id").asString();

            return new OneDriveDrive(api, id).new Metadata(nextObject);
        }
    }
}
