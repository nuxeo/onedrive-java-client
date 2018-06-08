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

    public static class Metadata extends OneDriveJsonObject {
        private String id;

        public Metadata() {
        }

        public Metadata(JsonObject jsonObject) {
            super(jsonObject);
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
