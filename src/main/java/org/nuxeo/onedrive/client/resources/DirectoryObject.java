package org.nuxeo.onedrive.client.resources;

import com.eclipsesource.json.JsonObject;

import org.nuxeo.onedrive.client.OneDriveJsonObject;

public abstract class DirectoryObject {
    private final String id;

    protected DirectoryObject(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static abstract class Metadata extends OneDriveJsonObject {
        private String id;

        protected Metadata() {
        }

        protected Metadata(JsonObject jsonObject) {
            super(jsonObject);
        }

        public abstract DirectoryObject asDirectoryObject();

        public String getId() {
            return id;
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
