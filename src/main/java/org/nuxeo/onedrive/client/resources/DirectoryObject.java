package org.nuxeo.onedrive.client.resources;

import com.eclipsesource.json.JsonObject;

import org.nuxeo.onedrive.client.OneDriveAPI;
import org.nuxeo.onedrive.client.OneDriveJsonObject;

public abstract class DirectoryObject {
    private final OneDriveAPI api;
    private final String id;

    protected DirectoryObject(final OneDriveAPI api, final String id) {
        this.api = api;
        this.id = id;
    }

    public OneDriveAPI getApi() {
        return api;
    }

    public String getId() {
        return id;
    }

    public abstract class Metadata extends OneDriveJsonObject {
        private String id;

        protected Metadata() {
        }

        protected Metadata(JsonObject jsonObject) {
            super(jsonObject);
        }

        public abstract DirectoryObject asDirectoryObject();

        public String getId() {
            if (null != id) {
                return id;
            }
            return DirectoryObject.this.id;
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            final String name = member.getName();

            if ("id".equals(name)) {
                id = member.getValue().asString();
            } else {
                super.parseMember(member);
            }
        }
    }
}
