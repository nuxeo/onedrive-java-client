package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.types.Site;

import java.net.URL;
import java.util.Iterator;

public final class Sites {
    public static Iterator<Site.Metadata> getSites(final Site site) {
        return new SitesIterator(site.getApi(), createSitesUrl(site));
    }

    public static Iterator<Site.Metadata> getSites(final OneDriveAPI api) {
        return new SitesIterator(api, createSitesUrl(api, ""));
    }

    public static Iterator<Site.Metadata> getSites(final OneDriveAPI api, final String search) {
        final QueryStringBuilder qs = new QueryStringBuilder();
        qs.set("search", search);
        return new SitesIterator(api, createSitesUrl(api, qs, ""));
    }

    private static URL createSitesUrl(final Site site) {
        return new URLTemplate(site.getAction("/sites")).build(site.getApi().getBaseURL());
    }

    private static URL createSitesUrl(final OneDriveAPI api, final String basePath) {
        return new URLTemplate(createSitesPath(basePath)).build(api.getBaseURL());
    }

    private static URL createSitesUrl(final OneDriveAPI api, final QueryStringBuilder qs, final String basePath) {
        return new URLTemplate(createSitesPath(basePath)).build(api.getBaseURL(), qs);
    }

    private static String createSitesPath(final String basePath) {
        return basePath + "/sites";
    }

    private static class SitesIterator implements Iterator<Site.Metadata> {
        private final OneDriveAPI api;
        private final JsonObjectIterator iterator;

        public SitesIterator(final OneDriveAPI api, final URL url) {
            this.api = api;
            iterator = new JsonObjectIterator(api, url);
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Site.Metadata next() {
            final JsonObject jsonObject = iterator.next();
            final String id = jsonObject.get("id").asString();

            return new Site(api, id).new Metadata().fromJson(jsonObject);
        }
    }
}
