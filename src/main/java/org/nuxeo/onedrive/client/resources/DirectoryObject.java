package org.nuxeo.onedrive.client.resources;

import com.eclipsesource.json.JsonObject;

import org.nuxeo.onedrive.client.OneDriveAPI;
import org.nuxeo.onedrive.client.OneDriveAPIException;
import org.nuxeo.onedrive.client.OneDriveJsonObject;
import org.nuxeo.onedrive.client.OneDriveRuntimeException;

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

    public static DirectoryObject.Metadata fromJson(final OneDriveAPI api, final JsonObject jsonObject) {
        final String type = jsonObject.get("@odata.type").asString();
        switch (type) {
            case "#microsoft.graph.group":
                return GroupItem.fromJson(api, jsonObject);

            default:
                throw new OneDriveRuntimeException(
                        new OneDriveAPIException(String.format("The object type %s is currently not handled.", type)));
        }
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
