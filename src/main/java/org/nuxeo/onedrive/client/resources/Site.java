package org.nuxeo.onedrive.client.resources;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.nuxeo.onedrive.client.*;
import org.nuxeo.onedrive.client.facets.RootFacet;
import org.nuxeo.onedrive.client.facets.SharePointIdsFacet;
import org.nuxeo.onedrive.client.facets.SiteCollectionFacet;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Site extends BaseItem {
    private final SiteIdentifier identifier;
    private final String hostname;
    private final String path;

    public Site(final OneDriveAPI api) {
        super(api);
        identifier = null;
        hostname = null;
        path = null;
    }

    public Site(final OneDriveAPI api, final String id) {
        super(api, id);
        identifier = SiteIdentifier.Id;
        hostname = null;
        path = null;
    }

    public Site(final OneDriveAPI api, final String hostname, final String path) {
        super(api);
        identifier = SiteIdentifier.Path;
        this.hostname = hostname;
        this.path = path;
    }

    public String getBasePath() {
        if (isRoot()) {
            return "/sites/root";
        } else if (null == getId()) {
            return "/sites/" + hostname + ":" + path;
        } else {
            return "/sites/" + getId();
        }
    }

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

    public Metadata getMetadata(OneDriveExpand... expands) throws IOException {
        final QueryStringBuilder query = new QueryStringBuilder().set("expand", expands);
        final URL url = new URLTemplate(getBasePath()).build(getApi().getBaseURL(), query);
        final OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        final OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        JsonObject jsonObject = response.getContent();
        response.close();
        return new Metadata(jsonObject);
    }

    public static Site.Metadata fromJson(final OneDriveAPI api, final JsonObject jsonObject) {
        final String id = jsonObject.get("id").asString();
        return new Site(api, id).new Metadata(jsonObject);
    }

    public class Metadata extends BaseItem.Metadata {
        private RootFacet root;
        private SharePointIdsFacet sharepointIds;
        private SiteCollectionFacet siteCollection;
        private String displayName;

        //private Object analytics;
        //private Object contentTypes;
        private OneDriveDrive.Metadata drive;
        private List<OneDriveDrive.Metadata> drives;
        //private Collection<BaseItem> items;
        //private Collection<Object> lists;
        private List<Metadata> sites;
        //private Collection<Object> columns;
        //private Object oneNote;

        public RootFacet getRoot() {
            return root;
        }

        public SharePointIdsFacet getSharepointIds() {
            return sharepointIds;
        }

        public SiteCollectionFacet getSiteCollection() {
            return siteCollection;
        }

        public String getDisplayName() {
            return displayName;
        }

        public OneDriveDrive.Metadata getDrive() {
            return drive;
        }

        public List<OneDriveDrive.Metadata> getDrives() {
            return drives;
        }

        public List<Metadata> getSites() {
            return sites;
        }

        @Override
        public Site getItem() {
            return Site.this;
        }

        public Metadata(final JsonObject jsonObject) {
            super(jsonObject);
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
                    root = new RootFacet().fromJson(value.asObject());
                    break;
                case "sharepointIds":
                    sharepointIds = new SharePointIdsFacet().fromJson(value.asObject());
                    break;
                case "siteCollection":
                    siteCollection = new SiteCollectionFacet().fromJson(value.asObject());
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

        private OneDriveDrive.Metadata parseDrive(final JsonObject jsonObject) {
            final String id = jsonObject.get("id").asString();
            return new OneDriveDrive(getApi(), id).new Metadata(jsonObject);
        }

        private List<OneDriveDrive.Metadata> parseDrives(final JsonArray jsonArray) {
            final ArrayList<OneDriveDrive.Metadata> drives = new ArrayList<>(jsonArray.size());

            jsonArray.forEach(v -> drives.add(parseDrive(v.asObject())));

            return drives;
        }

        private List<Site.Metadata> parseSites(final JsonArray jsonArray) {
            final ArrayList<Site.Metadata> sites = new ArrayList<>(jsonArray.size());

            for (int i = 0; i < jsonArray.size(); i++) {
                sites.set(i, fromJson(getApi(), jsonArray.get(i).asObject()));
            }

            return sites;
        }
    }
}
