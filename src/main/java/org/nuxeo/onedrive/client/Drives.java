package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.types.DirectoryObject;
import org.nuxeo.onedrive.client.types.Drive;
import org.nuxeo.onedrive.client.types.Site;

import java.net.URL;
import java.util.Iterator;

public final class Drives {
    public static Iterator<Drive.Metadata> getDrives(final OneDriveAPI api) {
        return new DrivesIterator(api, new URLTemplate("/drives").build(api.getBaseURL()));
    }

    public static Iterator<Drive.Metadata> getDrives(final DirectoryObject object) {
        final URL objectDrivesURL = createDrivesUrl(object.getApi(), object.getPath());
        return new DrivesIterator(object.getApi(), objectDrivesURL);
    }

    public static Iterator<Drive.Metadata> getDrives(final Site site) {
        return new DrivesIterator(site.getApi(), new URLTemplate(site.getAction("/drives")).build(site.getApi().getBaseURL()));
    }

    private static URL createDrivesUrl(final OneDriveAPI api, final String basePath) {
        return new URLTemplate(createDrivesPath(basePath)).build(api.getBaseURL());
    }

    private static String createDrivesPath(final String basePath) {
        return basePath + "/drives";
    }

    private static class DrivesIterator implements Iterator<Drive.Metadata> {
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
        public Drive.Metadata next() {
            final JsonObject nextObject = iterator.next();
            final String id = nextObject.get("id").asString();

            return new Drive(api, id).new Metadata().fromJson(nextObject);
        }
    }
}
