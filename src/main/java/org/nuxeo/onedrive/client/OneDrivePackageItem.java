package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.net.URL;

public class OneDrivePackageItem extends OneDriveItem {

    public OneDrivePackageItem(OneDriveAPI api, OneDriveResource parent, String resourceIdentifier, ItemIdentifierType itemIdentifierType) {
        super(api, parent, resourceIdentifier, itemIdentifierType);
    }

    @Override
    public OneDriveItem.Metadata getMetadata(OneDriveExpand... expands) throws IOException {
        QueryStringBuilder query = new QueryStringBuilder().set("expand", expands);
        final URL url = getMetadataURL().build(getApi().getBaseURL(), query, getItemIdentifier());
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        return new Metadata(response.getContent());
    }

    public static Metadata parseJson(OneDriveAPI api, JsonObject nextObject) {
        final String id = nextObject.get("id").asString();
        final OneDriveDrive drive = new OneDriveDrive(api, nextObject.get("parentReference").asObject().get("driveId").asString());
        final OneDrivePackageItem packageItem = new OneDrivePackageItem(api, drive, id, OneDriveItem.ItemIdentifierType.Id);
        return packageItem.new Metadata(nextObject);
    }

    public class Metadata extends OneDriveItem.Metadata {

        public Metadata(JsonObject json) {
            super(json);
        }

        @Override
        public OneDrivePackageItem getResource() {
            return OneDrivePackageItem.this;
        }
    }
}
