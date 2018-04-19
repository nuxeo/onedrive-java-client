package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;

import java.io.IOException;

public class OneDriveRemoteItem extends OneDriveItem {
    OneDriveRemoteItem(OneDriveAPI api) {
        super(api);
    }

    public OneDriveRemoteItem(OneDriveAPI api, OneDriveResource parent) {
        super(api, parent);
    }

    public OneDriveRemoteItem(OneDriveAPI api, OneDriveResource parent, String resourceIdentifier, ItemIdentifierType itemIdentifierType) {
        super(api, parent, resourceIdentifier, itemIdentifierType);
    }

    @Override
    public Metadata getMetadata(OneDriveExpand... expand) throws IOException {
        return null;
    }

    public class Metadata extends OneDriveItem.Metadata {

        public Metadata(JsonObject json) {
            super(json);
        }
    }
}
