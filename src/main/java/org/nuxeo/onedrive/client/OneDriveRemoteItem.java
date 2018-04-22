package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.IOException;
import java.net.URL;

public class OneDriveRemoteItem extends OneDriveItem {
    public OneDriveRemoteItem(OneDriveAPI api, OneDriveResource parent, String resourceIdentifier, ItemIdentifierType itemIdentifierType) {
        super(api, parent, resourceIdentifier, itemIdentifierType);
    }

    @Override
    public Metadata getMetadata(OneDriveExpand... expands) throws IOException {
        QueryStringBuilder query = new QueryStringBuilder().set("expand", expands);
        final URL url = getMetadataURL().build(getApi().getBaseURL(), query);
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        return new Metadata(response.getContent());
    }

    public class Metadata extends OneDriveItem.Metadata {

        private OneDriveItem.Metadata remoteItem;

        public OneDriveItem.Metadata getRemoteItem() {
            return remoteItem;
        }

        public Metadata(JsonObject json) {
            super(json);
        }

        @Override
        protected void parseMember(JsonObject.Member member) {
            JsonValue value = member.getValue();
            String memberName = member.getName();

            if ("remoteItem".equals(memberName)) {
                remoteItem = OneDriveItem.parseJson(getApi(), value.asObject());
            } else {
                super.parseMember(member);
            }
        }

        @Override
        public OneDriveRemoteItem getResource() {
            return OneDriveRemoteItem.this;
        }
    }
}
