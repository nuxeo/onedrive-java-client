package org.nuxeo.onedrive.client;

import com.eclipsesource.json.JsonObject;

public class OneDrivePatchOperation {
    private final JsonObject jsonObject = new JsonObject();

    public void rename(String newName) {
        jsonObject.add("name", newName);
    }

    public void move(OneDriveFolder newParent) {
        final JsonObject parentReference = new JsonObject();
        parentReference.add("path", newParent.getDrivePath());
        jsonObject.add("parentReference", parentReference);
    }

    JsonObject build() {
        return jsonObject;
    }
}
