package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;

import java.io.IOException;
import java.net.URL;

public class OneDrivePackageItem extends OneDriveItem {

    OneDrivePackageItem(OneDriveAPI api) {
        super(api);
    }

    public OneDrivePackageItem(OneDriveAPI api, String fileId) {
        super(api, fileId);
    }

    public OneDrivePackageItem(OneDriveAPI api, OneDriveDrive drive, String path) {
        super(api, drive, path);
    }

    @Override
    public OneDriveItem.Metadata getMetadata(OneDriveExpand... expands) throws IOException {
        QueryStringBuilder query = new QueryStringBuilder().set("expand", expands);
        final URL url = getMetadataURL().build(getApi().getBaseURL(), query, getResourceIdentifier());
        OneDriveJsonRequest request = new OneDriveJsonRequest(url, "GET");
        OneDriveJsonResponse response = request.sendRequest(getApi().getExecutor());
        return new Metadata(response.getContent());
    }

    public class Metadata extends OneDriveItem.Metadata {

        public Metadata(JsonObject json) {
            super(json);
        }

        @Override
        public OneDriveResource getResource() {
            return OneDrivePackageItem.this;
        }
    }
}
