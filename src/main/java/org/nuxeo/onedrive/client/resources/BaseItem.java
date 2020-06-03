package org.nuxeo.onedrive.client.resources;

import com.eclipsesource.json.JsonObject;
import org.nuxeo.onedrive.client.OneDriveAPI;
import org.nuxeo.onedrive.client.OneDriveIdentitySet;
import org.nuxeo.onedrive.client.OneDriveJsonObject;

import java.time.ZonedDateTime;

public abstract class BaseItem {
    private final OneDriveAPI api;
    private final String id;

    public OneDriveAPI getApi() {
        return api;
    }

    public String getId() {
        return id;
    }

    public BaseItem(final OneDriveAPI api) {
        this.api = api;
        this.id = null;
    }

    public BaseItem(final OneDriveAPI api, final String id) {
        this.api = api;
        this.id = id;
    }

    public abstract class Metadata extends OneDriveJsonObject {
        private OneDriveIdentitySet createdBy;
        private ZonedDateTime createdDateTime;
        private String description;
        private String eTag;
        private OneDriveIdentitySet lastModifiedBy;
        private ZonedDateTime lastModifiedDateTime;
        private String name;
        //private Object parentReference;
        public String webUrl;

        public String getId() {
            return BaseItem.this.id;
        }

        public BaseItem getItem() {
            return BaseItem.this;
        }

        public OneDriveIdentitySet getCreatedBy() {
            return createdBy;
        }

        public ZonedDateTime getCreatedDateTime() {
            return createdDateTime;
        }

        public String getDescription() {
            return description;
        }

        public String getETag() {
            return eTag;
        }

        public OneDriveIdentitySet getLastModifiedBy() {
            return lastModifiedBy;
        }

        public ZonedDateTime getLastModifiedDateTime() {
            return lastModifiedDateTime;
        }

        public String getName() {
            return name;
        }

        public Metadata(final JsonObject jsonObject) {
            super(jsonObject);
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            switch (member.getName()) {
                case "createdBy":
                    createdBy = new OneDriveIdentitySet(member.getValue().asObject());
                    break;
                case "createdDateTime":
                    createdDateTime = ZonedDateTime.parse(member.getValue().asString());
                    break;
                case "description":
                    description = member.getValue().asString();
                    break;
                case "eTag":
                    eTag = member.getValue().asString();
                    break;
                case "lastModifiedBy":
                    lastModifiedBy = new OneDriveIdentitySet(member.getValue().asObject());
                    break;
                case "lastModifiedDateTime":
                    lastModifiedDateTime = ZonedDateTime.parse(member.getValue().asString());
                    break;
                case "name":
                    name = member.getValue().asString();
                    break;
                case "parentReference":
                    // TODO
                    break;
                case "webUrl":
                    webUrl = member.getValue().asString();
                    break;
                default:
                    super.parseMember(member);
            }
        }
    }
}
