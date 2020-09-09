package org.nuxeo.onedrive.client.types;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.nuxeo.onedrive.client.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Site extends BaseItem {
    private final Site parent;
    private final SiteIdentifier identifier;

    Site(final OneDriveAPI api) {
        super(api);
        this.parent = null;
        this.identifier = null;
    }

    Site(final OneDriveAPI api, final String id, final SiteIdentifier identifier) {
        super(api, id);
        this.parent = null;
        this.identifier = identifier;
    }

    Site(final Site parent, final String id, final SiteIdentifier identifier) {
        super(parent.getApi(), id);
        this.parent = parent;
        this.identifier = identifier;
    }

    public static Site byId(final OneDriveAPI api, final String id) {
        return new Site(api, id, SiteIdentifier.Id);
    }

    public static Site byId(final Site parent, final String id) {
        return new Site(parent, id, SiteIdentifier.Id);
    }

    public static Site byHostname(final OneDriveAPI api, final String hostname) {
        return new Site(api, hostname, null);
    }

    public static Site byPath(final Site site, final String path) {
        return new Site(site, path, SiteIdentifier.Path);
    }

    @Override
    public String getPath() {
        if (identifier == null) {
            return "/sites/" + getId();
        }

        String path;
        if (identifier == SiteIdentifier.Path) {
            path = ":" + getId();
        }
        else {
            path = "/sites/" + getId();
        }

        if (null != parent) {
            return parent.getAction(path);
        }
        return path;
    }

    @Deprecated
    public String getBasePath() {
        return getPath();
    }

    @Override
    public String getAction(String action) {
        String path = getPath();
        if (SiteIdentifier.Path == identifier) {
            path += ":";
        }
        return path + action;
    }

    @Override
    public Metadata getMetadata() throws IOException {
        final URL url = new URLTemplate(getBasePath()).build(getApi().getBaseURL());
        final OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        try (final OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor())) {
            JsonObject jsonObject = response.getContent();
            return new Metadata().fromJson(jsonObject);
        }
    }

    @Deprecated
    public String getActionPath(final String action) {
        String path = getBasePath();
        if (identifier == SiteIdentifier.Path) {
            path += ":";
        }
        path += "/" + action;

        return path;
    }

    private boolean isRoot() {
        return null == getId() && null == identifier;
    }

    private enum SiteIdentifier {
        Id, Path
    }

    public static Site.Metadata fromJson(final OneDriveAPI api, final JsonObject jsonObject) {
        final String id = jsonObject.get("id").asString();
        return new Site(api, id, SiteIdentifier.Id).new Metadata().fromJson(jsonObject);
    }

    public class Metadata extends BaseItem.Metadata<Metadata> {
        private Root root;
        private SharePointIds sharepointIds;
        private SiteCollection siteCollection;
        private String displayName;

        //private Object analytics;
        //private Object contentTypes;
        private Drive.Metadata drive;
        private List<Drive.Metadata> drives;
        //private Collection<BaseItem> items;
        //private Collection<Object> lists;
        private List<Metadata> sites;
        //private Collection<Object> columns;
        //private Object oneNote;

        public Root getRoot() {
            return root;
        }

        public SharePointIds getSharepointIds() {
            return sharepointIds;
        }

        public SiteCollection getSiteCollection() {
            return siteCollection;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Drive.Metadata getDrive() {
            return drive;
        }

        public List<Drive.Metadata> getDrives() {
            return drives;
        }

        public List<Metadata> getSites() {
            return sites;
        }

        @Override
        public Site getItem() {
            return Site.this;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            final String name = member.getName();
            final JsonValue value = member.getValue();
            if (parseProperty(name, value)) {
                return;
            }
            if (parseRelation(name, value)) {
                return;
            }
            super.parseMember(member);
        }

        private boolean parseProperty(final String name, final JsonValue value) {
            switch (name) {
                case "root":
                    root = new Root().fromJson(value.asObject());
                    break;
                case "sharepointIds":
                    sharepointIds = new SharePointIds().fromJson(value.asObject());
                    break;
                case "siteCollection":
                    siteCollection = new SiteCollection().fromJson(value.asObject());
                    break;
                case "displayName":
                    displayName = value.asString();
                    break;
                default:
                    return false;
            }
            return true;
        }

        private boolean parseRelation(final String name, final JsonValue value) {
            switch (name) {
                case "analytics":
                    break;
                case "contentTypes":
                    break;
                case "drive":
                    drive = parseDrive(value.asObject());
                    break;
                case "drives":
                    drives = parseDrives(value.asArray());
                    break;
                case "items":
                    break;
                case "lists":
                    break;
                case "sites":
                    sites = parseSites(value.asArray());
                    break;
                case "columns":
                    break;
                case "oneNote":
                    break;
                default:
                    return false;
            }
            return true;
        }

        private Drive.Metadata parseDrive(final JsonObject jsonObject) {
            final String id = jsonObject.get("id").asString();
            return new Drive(getApi(), id).new Metadata().fromJson(jsonObject);
        }

        private List<Drive.Metadata> parseDrives(final JsonArray jsonArray) {
            final ArrayList<Drive.Metadata> drives = new ArrayList<>(jsonArray.size());

            jsonArray.forEach(v -> drives.add(parseDrive(v.asObject())));

            return drives;
        }

        private List<Site.Metadata> parseSites(final JsonArray jsonArray) {
            final ArrayList<Site.Metadata> sites = new ArrayList<>(jsonArray.size());

            jsonArray.forEach(v -> sites.add(Site.fromJson(getApi(), v.asObject())));
            jsonArray.forEach(v -> sites.add(Site.fromJson(getApi(), v.asObject())));

            return sites;
        }
    }
}
